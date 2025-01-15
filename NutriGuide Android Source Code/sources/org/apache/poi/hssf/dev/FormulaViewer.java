package org.apache.poi.hssf.dev;

import java.io.FileInputStream;
import java.io.PrintStream;
import java.util.List;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordFactory;
import org.apache.poi.hssf.record.formula.ExpPtg;
import org.apache.poi.hssf.record.formula.FuncPtg;
import org.apache.poi.hssf.record.formula.OperationPtg;
import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class FormulaViewer {
    private String file;
    private boolean list = false;

    public void run() throws Exception {
        List<Record> records = RecordFactory.createRecords(new POIFSFileSystem(new FileInputStream(this.file)).createDocumentInputStream("Workbook"));
        for (int k = 0; k < records.size(); k++) {
            Record record = records.get(k);
            if (record.getSid() == 6) {
                if (this.list) {
                    listFormula((FormulaRecord) record);
                } else {
                    parseFormulaRecord((FormulaRecord) record);
                }
            }
        }
    }

    private void listFormula(FormulaRecord record) {
        String numArg;
        Ptg[] tokens = record.getParsedExpression();
        int numptgs = tokens.length;
        Ptg token = tokens[numptgs - 1];
        if (token instanceof FuncPtg) {
            numArg = String.valueOf(numptgs - 1);
        } else {
            numArg = String.valueOf(-1);
        }
        StringBuffer buf = new StringBuffer();
        if (!(token instanceof ExpPtg)) {
            buf.append(((OperationPtg) token).toFormulaString());
            buf.append("~");
            byte ptgClass = token.getPtgClass();
            if (ptgClass == 0) {
                buf.append("REF");
            } else if (ptgClass == 32) {
                buf.append("VALUE");
            } else if (ptgClass == 64) {
                buf.append("ARRAY");
            }
            buf.append("~");
            if (numptgs > 1) {
                byte ptgClass2 = tokens[numptgs - 2].getPtgClass();
                if (ptgClass2 == 0) {
                    buf.append("REF");
                } else if (ptgClass2 == 32) {
                    buf.append("VALUE");
                } else if (ptgClass2 == 64) {
                    buf.append("ARRAY");
                }
            } else {
                buf.append("VALUE");
            }
            buf.append("~");
            buf.append(numArg);
            System.out.println(buf.toString());
        }
    }

    public void parseFormulaRecord(FormulaRecord record) {
        System.out.println("==============================");
        PrintStream printStream = System.out;
        printStream.print("row = " + record.getRow());
        PrintStream printStream2 = System.out;
        printStream2.println(", col = " + record.getColumn());
        PrintStream printStream3 = System.out;
        printStream3.println("value = " + record.getValue());
        PrintStream printStream4 = System.out;
        printStream4.print("xf = " + record.getXFIndex());
        PrintStream printStream5 = System.out;
        printStream5.print(", number of ptgs = " + record.getParsedExpression().length);
        PrintStream printStream6 = System.out;
        printStream6.println(", options = " + record.getOptions());
        PrintStream printStream7 = System.out;
        printStream7.println("RPN List = " + formulaString(record));
        PrintStream printStream8 = System.out;
        printStream8.println("Formula text = " + composeFormula(record));
    }

    private String formulaString(FormulaRecord record) {
        StringBuffer buf = new StringBuffer();
        Ptg[] tokens = record.getParsedExpression();
        for (Ptg token : tokens) {
            buf.append(token.toFormulaString());
            byte ptgClass = token.getPtgClass();
            if (ptgClass == 0) {
                buf.append("(R)");
            } else if (ptgClass == 32) {
                buf.append("(V)");
            } else if (ptgClass == 64) {
                buf.append("(A)");
            }
            buf.append(' ');
        }
        return buf.toString();
    }

    private static String composeFormula(FormulaRecord record) {
        return HSSFFormulaParser.toFormulaString((HSSFWorkbook) null, record.getParsedExpression());
    }

    public void setFile(String file2) {
        this.file = file2;
    }

    public void setList(boolean list2) {
        this.list = list2;
    }

    public static void main(String[] args) {
        if (args == null || args.length > 2 || args[0].equals("--help")) {
            System.out.println("FormulaViewer .8 proof that the devil lies in the details (or just in BIFF8 files in general)");
            System.out.println("usage: Give me a big fat file name");
        } else if (args[0].equals("--listFunctions")) {
            try {
                FormulaViewer viewer = new FormulaViewer();
                viewer.setFile(args[1]);
                viewer.setList(true);
                viewer.run();
            } catch (Exception e) {
                System.out.println("Whoops!");
                e.printStackTrace();
            }
        } else {
            try {
                FormulaViewer viewer2 = new FormulaViewer();
                viewer2.setFile(args[0]);
                viewer2.run();
            } catch (Exception e2) {
                System.out.println("Whoops!");
                e2.printStackTrace();
            }
        }
    }
}
