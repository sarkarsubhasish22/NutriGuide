package org.apache.poi.hssf.extractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import org.apache.poi.POIOLE2TextExtractor;
import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.HeaderFooter;

public class ExcelExtractor extends POIOLE2TextExtractor implements org.apache.poi.ss.extractor.ExcelExtractor {
    private HSSFDataFormatter _formatter;
    private boolean _includeBlankCells;
    private boolean _includeCellComments;
    private boolean _includeHeadersFooters;
    private boolean _includeSheetNames;
    private boolean _shouldEvaluateFormulas;
    private HSSFWorkbook _wb;

    public ExcelExtractor(HSSFWorkbook wb) {
        super(wb);
        this._includeSheetNames = true;
        this._shouldEvaluateFormulas = true;
        this._includeCellComments = false;
        this._includeBlankCells = false;
        this._includeHeadersFooters = true;
        this._wb = wb;
        this._formatter = new HSSFDataFormatter();
    }

    public ExcelExtractor(POIFSFileSystem fs) throws IOException {
        this(fs.getRoot(), fs);
    }

    public ExcelExtractor(DirectoryNode dir, POIFSFileSystem fs) throws IOException {
        this(new HSSFWorkbook(dir, fs, true));
    }

    private static final class CommandParseException extends Exception {
        public CommandParseException(String msg) {
            super(msg);
        }
    }

    private static final class CommandArgs {
        private final boolean _evaluateFormulas;
        private final boolean _headersFooters;
        private final File _inputFile;
        private final boolean _requestHelp;
        private final boolean _showBlankCells;
        private final boolean _showCellComments;
        private final boolean _showSheetNames;

        public CommandArgs(String[] args) throws CommandParseException {
            int i;
            int nArgs = args.length;
            File inputFile = null;
            boolean requestHelp = false;
            boolean showSheetNames = true;
            boolean evaluateFormulas = true;
            boolean showCellComments = false;
            boolean showBlankCells = false;
            boolean headersFooters = true;
            int i2 = 0;
            while (true) {
                if (i2 >= nArgs) {
                    break;
                }
                String arg = args[i2];
                if ("-help".equalsIgnoreCase(arg)) {
                    requestHelp = true;
                    break;
                }
                if ("-i".equals(arg)) {
                    i = i2 + 1;
                    if (i < nArgs) {
                        String arg2 = args[i];
                        if (inputFile == null) {
                            inputFile = new File(arg2);
                            if (!inputFile.exists()) {
                                throw new CommandParseException("Specified input file '" + arg2 + "' does not exist");
                            } else if (inputFile.isDirectory()) {
                                throw new CommandParseException("Specified input file '" + arg2 + "' is a directory");
                            }
                        } else {
                            throw new CommandParseException("Only one input file can be supplied");
                        }
                    } else {
                        throw new CommandParseException("Expected filename after '-i'");
                    }
                } else if ("--show-sheet-names".equals(arg)) {
                    i = i2 + 1;
                    showSheetNames = parseBoolArg(args, i);
                } else if ("--evaluate-formulas".equals(arg)) {
                    i = i2 + 1;
                    evaluateFormulas = parseBoolArg(args, i);
                } else if ("--show-comments".equals(arg)) {
                    i = i2 + 1;
                    showCellComments = parseBoolArg(args, i);
                } else if ("--show-blanks".equals(arg)) {
                    i = i2 + 1;
                    showBlankCells = parseBoolArg(args, i);
                } else if ("--headers-footers".equals(arg)) {
                    i = i2 + 1;
                    headersFooters = parseBoolArg(args, i);
                } else {
                    throw new CommandParseException("Invalid argument '" + arg + "'");
                }
                i2 = i + 1;
            }
            this._requestHelp = requestHelp;
            this._inputFile = inputFile;
            this._showSheetNames = showSheetNames;
            this._evaluateFormulas = evaluateFormulas;
            this._showCellComments = showCellComments;
            this._showBlankCells = showBlankCells;
            this._headersFooters = headersFooters;
        }

        private static boolean parseBoolArg(String[] args, int i) throws CommandParseException {
            if (i < args.length) {
                String value = args[i].toUpperCase();
                if ("Y".equals(value) || "YES".equals(value) || "ON".equals(value) || "TRUE".equals(value)) {
                    return true;
                }
                if ("N".equals(value) || "NO".equals(value) || "OFF".equals(value) || "FALSE".equals(value)) {
                    return false;
                }
                throw new CommandParseException("Invalid value '" + args[i] + "' for '" + args[i - 1] + "'. Expected 'Y' or 'N'");
            }
            throw new CommandParseException("Expected value after '" + args[i - 1] + "'");
        }

        public boolean isRequestHelp() {
            return this._requestHelp;
        }

        public File getInputFile() {
            return this._inputFile;
        }

        public boolean shouldShowSheetNames() {
            return this._showSheetNames;
        }

        public boolean shouldEvaluateFormulas() {
            return this._evaluateFormulas;
        }

        public boolean shouldShowCellComments() {
            return this._showCellComments;
        }

        public boolean shouldShowBlankCells() {
            return this._showBlankCells;
        }

        public boolean shouldIncludeHeadersFooters() {
            return this._headersFooters;
        }
    }

    private static void printUsageMessage(PrintStream ps) {
        ps.println("Use:");
        ps.println("    " + ExcelExtractor.class.getName() + " [<flag> <value> [<flag> <value> [...]]] [-i <filename.xls>]");
        ps.println("       -i <filename.xls> specifies input file (default is to use stdin)");
        ps.println("       Flags can be set on or off by using the values 'Y' or 'N'.");
        ps.println("       Following are available flags and their default values:");
        ps.println("       --show-sheet-names  Y");
        ps.println("       --evaluate-formulas Y");
        ps.println("       --show-comments     N");
        ps.println("       --show-blanks       Y");
        ps.println("       --headers-footers   Y");
    }

    public static void main(String[] args) {
        InputStream is;
        try {
            CommandArgs cmdArgs = new CommandArgs(args);
            if (cmdArgs.isRequestHelp()) {
                printUsageMessage(System.out);
                return;
            }
            try {
                if (cmdArgs.getInputFile() == null) {
                    is = System.in;
                } else {
                    is = new FileInputStream(cmdArgs.getInputFile());
                }
                ExcelExtractor extractor = new ExcelExtractor(new HSSFWorkbook(is));
                extractor.setIncludeSheetNames(cmdArgs.shouldShowSheetNames());
                extractor.setFormulasNotResults(!cmdArgs.shouldEvaluateFormulas());
                extractor.setIncludeCellComments(cmdArgs.shouldShowCellComments());
                extractor.setIncludeBlankCells(cmdArgs.shouldShowBlankCells());
                extractor.setIncludeHeadersFooters(cmdArgs.shouldIncludeHeadersFooters());
                System.out.println(extractor.getText());
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        } catch (CommandParseException e2) {
            System.err.println(e2.getMessage());
            printUsageMessage(System.err);
            System.exit(1);
        }
    }

    public void setIncludeSheetNames(boolean includeSheetNames) {
        this._includeSheetNames = includeSheetNames;
    }

    public void setFormulasNotResults(boolean formulasNotResults) {
        this._shouldEvaluateFormulas = !formulasNotResults;
    }

    public void setIncludeCellComments(boolean includeCellComments) {
        this._includeCellComments = includeCellComments;
    }

    public void setIncludeBlankCells(boolean includeBlankCells) {
        this._includeBlankCells = includeBlankCells;
    }

    public void setIncludeHeadersFooters(boolean includeHeadersFooters) {
        this._includeHeadersFooters = includeHeadersFooters;
    }

    public String getText() {
        int lastRow;
        int firstRow;
        int firstCell;
        HSSFRow row;
        int lastRow2;
        int firstRow2;
        String name;
        StringBuffer text = new StringBuffer();
        this._wb.setMissingCellPolicy(HSSFRow.RETURN_BLANK_AS_NULL);
        HSSFComment comment = null;
        for (int i = 0; i < this._wb.getNumberOfSheets(); i++) {
            HSSFSheet sheet = this._wb.getSheetAt(i);
            if (sheet != null) {
                if (this._includeSheetNames && (name = this._wb.getSheetName(i)) != null) {
                    text.append(name);
                    text.append("\n");
                }
                if (this._includeHeadersFooters) {
                    text.append(_extractHeaderFooter(sheet.getHeader()));
                }
                int firstRow3 = sheet.getFirstRowNum();
                int lastRow3 = sheet.getLastRowNum();
                int j = firstRow3;
                while (j <= lastRow3) {
                    HSSFRow row2 = sheet.getRow(j);
                    if (row2 == null) {
                        firstRow = firstRow3;
                        lastRow = lastRow3;
                    } else {
                        int firstCell2 = row2.getFirstCellNum();
                        int lastCell = row2.getLastCellNum();
                        if (this._includeBlankCells) {
                            firstCell2 = 0;
                        }
                        int k = firstCell2;
                        while (k < lastCell) {
                            HSSFCell cell = row2.getCell(k);
                            boolean outputContents = true;
                            if (cell == null) {
                                outputContents = this._includeBlankCells;
                                firstRow2 = firstRow3;
                                lastRow2 = lastRow3;
                                row = row2;
                                firstCell = firstCell2;
                            } else {
                                firstRow2 = firstRow3;
                                int firstRow4 = cell.getCellType();
                                if (firstRow4 != 0) {
                                    lastRow2 = lastRow3;
                                    if (firstRow4 != 1) {
                                        row = row2;
                                        if (firstRow4 != 2) {
                                            if (firstRow4 == 4) {
                                                text.append(cell.getBooleanCellValue());
                                                firstCell = firstCell2;
                                            } else if (firstRow4 == 5) {
                                                text.append(ErrorEval.getText(cell.getErrorCellValue()));
                                                firstCell = firstCell2;
                                            } else {
                                                throw new RuntimeException("Unexpected cell type (" + cell.getCellType() + ")");
                                            }
                                        } else if (!this._shouldEvaluateFormulas) {
                                            text.append(cell.getCellFormula());
                                            firstCell = firstCell2;
                                        } else {
                                            int cachedFormulaResultType = cell.getCachedFormulaResultType();
                                            if (cachedFormulaResultType != 0) {
                                                if (cachedFormulaResultType == 1) {
                                                    HSSFRichTextString str = cell.getRichStringCellValue();
                                                    if (str != null && str.length() > 0) {
                                                        text.append(str.toString());
                                                    }
                                                } else if (cachedFormulaResultType == 4) {
                                                    text.append(cell.getBooleanCellValue());
                                                    firstCell = firstCell2;
                                                } else if (cachedFormulaResultType == 5) {
                                                    text.append(ErrorEval.getText(cell.getErrorCellValue()));
                                                    firstCell = firstCell2;
                                                }
                                                firstCell = firstCell2;
                                            } else {
                                                HSSFCellStyle style = cell.getCellStyle();
                                                if (style == null) {
                                                    text.append(cell.getNumericCellValue());
                                                    firstCell = firstCell2;
                                                } else {
                                                    HSSFComment hSSFComment = comment;
                                                    firstCell = firstCell2;
                                                    text.append(this._formatter.formatRawCellContents(cell.getNumericCellValue(), style.getDataFormat(), style.getDataFormatString()));
                                                }
                                            }
                                        }
                                    } else {
                                        row = row2;
                                        firstCell = firstCell2;
                                        text.append(cell.getRichStringCellValue().getString());
                                    }
                                } else {
                                    lastRow2 = lastRow3;
                                    row = row2;
                                    firstCell = firstCell2;
                                    text.append(this._formatter.formatCellValue(cell));
                                }
                                comment = cell.getCellComment();
                                if (this._includeCellComments && comment != null) {
                                    String commentText = comment.getString().getString().replace(10, ' ');
                                    text.append(" Comment by " + comment.getAuthor() + ": " + commentText);
                                }
                            }
                            if (outputContents && k < lastCell - 1) {
                                text.append("\t");
                            }
                            k++;
                            firstRow3 = firstRow2;
                            lastRow3 = lastRow2;
                            row2 = row;
                            firstCell2 = firstCell;
                        }
                        firstRow = firstRow3;
                        lastRow = lastRow3;
                        HSSFRow hSSFRow = row2;
                        int i2 = firstCell2;
                        text.append("\n");
                    }
                    j++;
                    firstRow3 = firstRow;
                    lastRow3 = lastRow;
                }
                int i3 = lastRow3;
                if (this._includeHeadersFooters != 0) {
                    text.append(_extractHeaderFooter(sheet.getFooter()));
                }
            }
        }
        return text.toString();
    }

    public static String _extractHeaderFooter(HeaderFooter hf) {
        StringBuffer text = new StringBuffer();
        if (hf.getLeft() != null) {
            text.append(hf.getLeft());
        }
        if (hf.getCenter() != null) {
            if (text.length() > 0) {
                text.append("\t");
            }
            text.append(hf.getCenter());
        }
        if (hf.getRight() != null) {
            if (text.length() > 0) {
                text.append("\t");
            }
            text.append(hf.getRight());
        }
        if (text.length() > 0) {
            text.append("\n");
        }
        return text.toString();
    }
}
