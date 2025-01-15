package org.apache.poi.hssf.usermodel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.model.InternalSheet;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.record.BoolErrRecord;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.hssf.record.CommonObjectDataSubRecord;
import org.apache.poi.hssf.record.DrawingRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.HyperlinkRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NoteRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordBase;
import org.apache.poi.hssf.record.SubRecord;
import org.apache.poi.hssf.record.TextObjectRecord;
import org.apache.poi.hssf.record.aggregates.FormulaRecordAggregate;
import org.apache.poi.hssf.record.formula.ExpPtg;
import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

public class HSSFCell implements Cell {
    public static final short ENCODING_COMPRESSED_UNICODE = 0;
    public static final short ENCODING_UNCHANGED = -1;
    public static final short ENCODING_UTF_16 = 1;
    private static final String FILE_FORMAT_NAME = "BIFF8";
    private static final String LAST_COLUMN_NAME = SpreadsheetVersion.EXCEL97.getLastColumnName();
    public static final int LAST_COLUMN_NUMBER = SpreadsheetVersion.EXCEL97.getLastColumnIndex();
    private static POILogger log = POILogFactory.getLogger(HSSFCell.class);
    private final HSSFWorkbook _book;
    private int _cellType;
    private HSSFComment _comment;
    private CellValueRecordInterface _record;
    private final HSSFSheet _sheet;
    private HSSFRichTextString _stringValue;

    protected HSSFCell(HSSFWorkbook book, HSSFSheet sheet, int row, short col) {
        checkBounds(col);
        this._stringValue = null;
        this._book = book;
        this._sheet = sheet;
        setCellType(3, false, row, col, sheet.getSheet().getXFIndexForColAt(col));
    }

    public HSSFSheet getSheet() {
        return this._sheet;
    }

    public HSSFRow getRow() {
        return this._sheet.getRow(getRowIndex());
    }

    protected HSSFCell(HSSFWorkbook book, HSSFSheet sheet, int row, short col, int type) {
        checkBounds(col);
        this._cellType = -1;
        this._stringValue = null;
        this._book = book;
        this._sheet = sheet;
        setCellType(type, false, row, col, sheet.getSheet().getXFIndexForColAt(col));
    }

    protected HSSFCell(HSSFWorkbook book, HSSFSheet sheet, CellValueRecordInterface cval) {
        this._record = cval;
        int determineType = determineType(cval);
        this._cellType = determineType;
        this._stringValue = null;
        this._book = book;
        this._sheet = sheet;
        if (determineType == 1) {
            this._stringValue = new HSSFRichTextString(book.getWorkbook(), (LabelSSTRecord) cval);
        } else if (determineType == 2) {
            this._stringValue = new HSSFRichTextString(((FormulaRecordAggregate) cval).getStringValue());
        }
        setCellStyle(new HSSFCellStyle(cval.getXFIndex(), book.getWorkbook().getExFormatAt(cval.getXFIndex()), book));
    }

    private static int determineType(CellValueRecordInterface cval) {
        if (cval instanceof FormulaRecordAggregate) {
            return 2;
        }
        Record record = (Record) cval;
        short sid = record.getSid();
        if (sid == 253) {
            return 1;
        }
        if (sid == 513) {
            return 3;
        }
        if (sid == 515) {
            return 0;
        }
        if (sid == 517) {
            return ((BoolErrRecord) record).isBoolean() ? 4 : 5;
        }
        throw new RuntimeException("Bad cell value rec (" + cval.getClass().getName() + ")");
    }

    /* access modifiers changed from: protected */
    public InternalWorkbook getBoundWorkbook() {
        return this._book.getWorkbook();
    }

    public int getRowIndex() {
        return this._record.getRow();
    }

    public void setCellNum(short num) {
        this._record.setColumn(num);
    }

    /* access modifiers changed from: protected */
    public void updateCellNum(short num) {
        this._record.setColumn(num);
    }

    public short getCellNum() {
        return (short) getColumnIndex();
    }

    public int getColumnIndex() {
        return this._record.getColumn() & 65535;
    }

    public void setCellType(int cellType) {
        notifyFormulaChanging();
        if (isPartOfArrayFormulaGroup()) {
            notifyArrayFormulaChanging();
        }
        setCellType(cellType, true, this._record.getRow(), this._record.getColumn(), this._record.getXFIndex());
    }

    /* JADX WARNING: type inference failed for: r2v2, types: [org.apache.poi.hssf.record.CellValueRecordInterface] */
    /* JADX WARNING: type inference failed for: r4v4, types: [org.apache.poi.hssf.record.CellValueRecordInterface] */
    /* JADX WARNING: type inference failed for: r5v6, types: [org.apache.poi.hssf.record.CellValueRecordInterface] */
    /* JADX WARNING: type inference failed for: r6v3, types: [org.apache.poi.hssf.record.CellValueRecordInterface] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setCellType(int r8, boolean r9, int r10, short r11, short r12) {
        /*
            r7 = this;
            r0 = 5
            if (r8 > r0) goto L_0x013d
            r1 = 0
            if (r8 == 0) goto L_0x0101
            r2 = 1
            if (r8 == r2) goto L_0x00ba
            r2 = 2
            if (r8 == r2) goto L_0x0089
            r2 = 3
            if (r8 == r2) goto L_0x0069
            r2 = 4
            if (r8 == r2) goto L_0x003f
            if (r8 == r0) goto L_0x0016
            goto L_0x0128
        L_0x0016:
            r0 = r1
            r2 = r1
            r3 = r1
            r4 = r1
            r5 = 0
            int r6 = r7._cellType
            if (r8 == r6) goto L_0x0026
            org.apache.poi.hssf.record.BoolErrRecord r6 = new org.apache.poi.hssf.record.BoolErrRecord
            r6.<init>()
            r5 = r6
            goto L_0x002b
        L_0x0026:
            org.apache.poi.hssf.record.CellValueRecordInterface r6 = r7._record
            r5 = r6
            org.apache.poi.hssf.record.BoolErrRecord r5 = (org.apache.poi.hssf.record.BoolErrRecord) r5
        L_0x002b:
            r5.setColumn(r11)
            if (r9 == 0) goto L_0x0035
            r6 = 15
            r5.setValue((byte) r6)
        L_0x0035:
            r5.setXFIndex(r12)
            r5.setRow(r10)
            r7._record = r5
            goto L_0x0128
        L_0x003f:
            r0 = r1
            r2 = r1
            r3 = r1
            r4 = 0
            int r5 = r7._cellType
            if (r8 == r5) goto L_0x004e
            org.apache.poi.hssf.record.BoolErrRecord r5 = new org.apache.poi.hssf.record.BoolErrRecord
            r5.<init>()
            r4 = r5
            goto L_0x0053
        L_0x004e:
            org.apache.poi.hssf.record.CellValueRecordInterface r5 = r7._record
            r4 = r5
            org.apache.poi.hssf.record.BoolErrRecord r4 = (org.apache.poi.hssf.record.BoolErrRecord) r4
        L_0x0053:
            r4.setColumn(r11)
            if (r9 == 0) goto L_0x005f
            boolean r5 = r7.convertCellValueToBoolean()
            r4.setValue((boolean) r5)
        L_0x005f:
            r4.setXFIndex(r12)
            r4.setRow(r10)
            r7._record = r4
            goto L_0x0128
        L_0x0069:
            r0 = r1
            r2 = r1
            r3 = 0
            int r4 = r7._cellType
            if (r8 == r4) goto L_0x0077
            org.apache.poi.hssf.record.BlankRecord r4 = new org.apache.poi.hssf.record.BlankRecord
            r4.<init>()
            r3 = r4
            goto L_0x007c
        L_0x0077:
            org.apache.poi.hssf.record.CellValueRecordInterface r4 = r7._record
            r3 = r4
            org.apache.poi.hssf.record.BlankRecord r3 = (org.apache.poi.hssf.record.BlankRecord) r3
        L_0x007c:
            r3.setColumn(r11)
            r3.setXFIndex(r12)
            r3.setRow(r10)
            r7._record = r3
            goto L_0x0128
        L_0x0089:
            int r0 = r7._cellType
            if (r8 == r0) goto L_0x009c
            org.apache.poi.hssf.usermodel.HSSFSheet r0 = r7._sheet
            org.apache.poi.hssf.model.InternalSheet r0 = r0.getSheet()
            org.apache.poi.hssf.record.aggregates.RowRecordsAggregate r0 = r0.getRowsAggregate()
            org.apache.poi.hssf.record.aggregates.FormulaRecordAggregate r0 = r0.createFormula(r10, r11)
            goto L_0x00a6
        L_0x009c:
            org.apache.poi.hssf.record.CellValueRecordInterface r0 = r7._record
            org.apache.poi.hssf.record.aggregates.FormulaRecordAggregate r0 = (org.apache.poi.hssf.record.aggregates.FormulaRecordAggregate) r0
            r0.setRow(r10)
            r0.setColumn(r11)
        L_0x00a6:
            if (r9 == 0) goto L_0x00b3
            org.apache.poi.hssf.record.FormulaRecord r1 = r0.getFormulaRecord()
            double r2 = r7.getNumericCellValue()
            r1.setValue(r2)
        L_0x00b3:
            r0.setXFIndex(r12)
            r7._record = r0
            goto L_0x0128
        L_0x00ba:
            r0 = r1
            int r2 = r7._cellType
            if (r8 != r2) goto L_0x00c4
            org.apache.poi.hssf.record.CellValueRecordInterface r2 = r7._record
            org.apache.poi.hssf.record.LabelSSTRecord r2 = (org.apache.poi.hssf.record.LabelSSTRecord) r2
            goto L_0x00d2
        L_0x00c4:
            org.apache.poi.hssf.record.LabelSSTRecord r2 = new org.apache.poi.hssf.record.LabelSSTRecord
            r2.<init>()
            r2.setColumn(r11)
            r2.setRow(r10)
            r2.setXFIndex(r12)
        L_0x00d2:
            if (r9 == 0) goto L_0x00fe
            java.lang.String r3 = r7.convertCellValueToString()
            org.apache.poi.hssf.usermodel.HSSFWorkbook r4 = r7._book
            org.apache.poi.hssf.model.InternalWorkbook r4 = r4.getWorkbook()
            org.apache.poi.hssf.record.common.UnicodeString r5 = new org.apache.poi.hssf.record.common.UnicodeString
            r5.<init>((java.lang.String) r3)
            int r4 = r4.addSSTString(r5)
            r2.setSSTIndex(r4)
            org.apache.poi.hssf.usermodel.HSSFWorkbook r5 = r7._book
            org.apache.poi.hssf.model.InternalWorkbook r5 = r5.getWorkbook()
            org.apache.poi.hssf.record.common.UnicodeString r5 = r5.getSSTString(r4)
            org.apache.poi.hssf.usermodel.HSSFRichTextString r6 = new org.apache.poi.hssf.usermodel.HSSFRichTextString
            r6.<init>()
            r7._stringValue = r6
            r6.setUnicodeString(r5)
        L_0x00fe:
            r7._record = r2
            goto L_0x0128
        L_0x0101:
            r0 = r1
            r1 = 0
            int r2 = r7._cellType
            if (r8 == r2) goto L_0x010e
            org.apache.poi.hssf.record.NumberRecord r2 = new org.apache.poi.hssf.record.NumberRecord
            r2.<init>()
            r1 = r2
            goto L_0x0113
        L_0x010e:
            org.apache.poi.hssf.record.CellValueRecordInterface r2 = r7._record
            r1 = r2
            org.apache.poi.hssf.record.NumberRecord r1 = (org.apache.poi.hssf.record.NumberRecord) r1
        L_0x0113:
            r1.setColumn(r11)
            if (r9 == 0) goto L_0x011f
            double r2 = r7.getNumericCellValue()
            r1.setValue(r2)
        L_0x011f:
            r1.setXFIndex(r12)
            r1.setRow(r10)
            r7._record = r1
        L_0x0128:
            int r0 = r7._cellType
            if (r8 == r0) goto L_0x013a
            r1 = -1
            if (r0 == r1) goto L_0x013a
            org.apache.poi.hssf.usermodel.HSSFSheet r0 = r7._sheet
            org.apache.poi.hssf.model.InternalSheet r0 = r0.getSheet()
            org.apache.poi.hssf.record.CellValueRecordInterface r1 = r7._record
            r0.replaceValueRecord(r1)
        L_0x013a:
            r7._cellType = r8
            return
        L_0x013d:
            java.lang.RuntimeException r0 = new java.lang.RuntimeException
            java.lang.String r1 = "I have no idea what type that is!"
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.hssf.usermodel.HSSFCell.setCellType(int, boolean, int, short, short):void");
    }

    public int getCellType() {
        return this._cellType;
    }

    public void setCellValue(double value) {
        if (Double.isInfinite(value)) {
            setCellErrorValue(FormulaError.DIV0.getCode());
        } else if (Double.isNaN(value)) {
            setCellErrorValue(FormulaError.NUM.getCode());
        } else {
            int row = this._record.getRow();
            short col = this._record.getColumn();
            short styleIndex = this._record.getXFIndex();
            int i = this._cellType;
            if (i != 0) {
                if (i != 2) {
                    setCellType(0, false, row, col, styleIndex);
                } else {
                    ((FormulaRecordAggregate) this._record).setCachedDoubleResult(value);
                    return;
                }
            }
            ((NumberRecord) this._record).setValue(value);
        }
    }

    public void setCellValue(Date value) {
        setCellValue(HSSFDateUtil.getExcelDate(value, this._book.getWorkbook().isUsing1904DateWindowing()));
    }

    public void setCellValue(Calendar value) {
        setCellValue(HSSFDateUtil.getExcelDate(value, this._book.getWorkbook().isUsing1904DateWindowing()));
    }

    public void setCellValue(String value) {
        setCellValue((RichTextString) value == null ? null : new HSSFRichTextString(value));
    }

    public void setCellValue(RichTextString value) {
        HSSFRichTextString hvalue = (HSSFRichTextString) value;
        int row = this._record.getRow();
        short col = this._record.getColumn();
        short styleIndex = this._record.getXFIndex();
        if (hvalue == null) {
            notifyFormulaChanging();
            setCellType(3, false, row, col, styleIndex);
        } else if (hvalue.length() <= SpreadsheetVersion.EXCEL97.getMaxTextLength()) {
            int i = this._cellType;
            if (i == 2) {
                ((FormulaRecordAggregate) this._record).setCachedStringResult(hvalue.getString());
                this._stringValue = new HSSFRichTextString(value.getString());
                return;
            }
            if (i != 1) {
                setCellType(1, false, row, col, styleIndex);
            }
            int index = this._book.getWorkbook().addSSTString(hvalue.getUnicodeString());
            ((LabelSSTRecord) this._record).setSSTIndex(index);
            this._stringValue = hvalue;
            hvalue.setWorkbookReferences(this._book.getWorkbook(), (LabelSSTRecord) this._record);
            this._stringValue.setUnicodeString(this._book.getWorkbook().getSSTString(index));
        } else {
            throw new IllegalArgumentException("The maximum length of cell contents (text) is 32,767 characters");
        }
    }

    public void setCellFormula(String formula) {
        if (isPartOfArrayFormulaGroup()) {
            notifyArrayFormulaChanging();
        }
        int row = this._record.getRow();
        short col = this._record.getColumn();
        short styleIndex = this._record.getXFIndex();
        if (formula == null) {
            notifyFormulaChanging();
            setCellType(3, false, row, col, styleIndex);
            return;
        }
        Ptg[] ptgs = HSSFFormulaParser.parse(formula, this._book, 0, this._book.getSheetIndex((Sheet) this._sheet));
        setCellType(2, false, row, col, styleIndex);
        FormulaRecordAggregate agg = (FormulaRecordAggregate) this._record;
        FormulaRecord frec = agg.getFormulaRecord();
        frec.setOptions(2);
        frec.setValue(0.0d);
        if (agg.getXFIndex() == 0) {
            agg.setXFIndex(15);
        }
        agg.setParsedExpression(ptgs);
    }

    private void notifyFormulaChanging() {
        CellValueRecordInterface cellValueRecordInterface = this._record;
        if (cellValueRecordInterface instanceof FormulaRecordAggregate) {
            ((FormulaRecordAggregate) cellValueRecordInterface).notifyFormulaChanging();
        }
    }

    public String getCellFormula() {
        CellValueRecordInterface cellValueRecordInterface = this._record;
        if (cellValueRecordInterface instanceof FormulaRecordAggregate) {
            return HSSFFormulaParser.toFormulaString(this._book, ((FormulaRecordAggregate) cellValueRecordInterface).getFormulaTokens());
        }
        throw typeMismatch(2, this._cellType, true);
    }

    private static String getCellTypeName(int cellTypeCode) {
        if (cellTypeCode == 0) {
            return "numeric";
        }
        if (cellTypeCode == 1) {
            return "text";
        }
        if (cellTypeCode == 2) {
            return "formula";
        }
        if (cellTypeCode == 3) {
            return "blank";
        }
        if (cellTypeCode == 4) {
            return "boolean";
        }
        if (cellTypeCode == 5) {
            return "error";
        }
        return "#unknown cell type (" + cellTypeCode + ")#";
    }

    private static RuntimeException typeMismatch(int expectedTypeCode, int actualTypeCode, boolean isFormulaCell) {
        StringBuilder sb = new StringBuilder();
        sb.append("Cannot get a ");
        sb.append(getCellTypeName(expectedTypeCode));
        sb.append(" value from a ");
        sb.append(getCellTypeName(actualTypeCode));
        sb.append(" ");
        sb.append(isFormulaCell ? "formula " : "");
        sb.append("cell");
        return new IllegalStateException(sb.toString());
    }

    private static void checkFormulaCachedValueType(int expectedTypeCode, FormulaRecord fr) {
        int cachedValueType = fr.getCachedResultType();
        if (cachedValueType != expectedTypeCode) {
            throw typeMismatch(expectedTypeCode, cachedValueType, true);
        }
    }

    public double getNumericCellValue() {
        int i = this._cellType;
        if (i == 0) {
            return ((NumberRecord) this._record).getValue();
        }
        if (i == 2) {
            FormulaRecord fr = ((FormulaRecordAggregate) this._record).getFormulaRecord();
            checkFormulaCachedValueType(0, fr);
            return fr.getValue();
        } else if (i == 3) {
            return 0.0d;
        } else {
            throw typeMismatch(0, i, false);
        }
    }

    public Date getDateCellValue() {
        if (this._cellType == 3) {
            return null;
        }
        double value = getNumericCellValue();
        if (this._book.getWorkbook().isUsing1904DateWindowing()) {
            return HSSFDateUtil.getJavaDate(value, true);
        }
        return HSSFDateUtil.getJavaDate(value, false);
    }

    public String getStringCellValue() {
        return getRichStringCellValue().getString();
    }

    public HSSFRichTextString getRichStringCellValue() {
        int i = this._cellType;
        if (i == 1) {
            return this._stringValue;
        }
        String str = "";
        if (i == 2) {
            FormulaRecordAggregate fra = (FormulaRecordAggregate) this._record;
            checkFormulaCachedValueType(1, fra.getFormulaRecord());
            String strVal = fra.getStringValue();
            if (strVal != null) {
                str = strVal;
            }
            return new HSSFRichTextString(str);
        } else if (i == 3) {
            return new HSSFRichTextString(str);
        } else {
            throw typeMismatch(1, i, false);
        }
    }

    public void setCellValue(boolean value) {
        int row = this._record.getRow();
        short col = this._record.getColumn();
        short styleIndex = this._record.getXFIndex();
        int i = this._cellType;
        if (i != 2) {
            if (i != 4) {
                setCellType(4, false, row, col, styleIndex);
            }
            ((BoolErrRecord) this._record).setValue(value);
            return;
        }
        ((FormulaRecordAggregate) this._record).setCachedBooleanResult(value);
    }

    public void setCellErrorValue(byte errorCode) {
        int row = this._record.getRow();
        short col = this._record.getColumn();
        short styleIndex = this._record.getXFIndex();
        int i = this._cellType;
        if (i != 2) {
            if (i != 5) {
                setCellType(5, false, row, col, styleIndex);
            }
            ((BoolErrRecord) this._record).setValue(errorCode);
            return;
        }
        ((FormulaRecordAggregate) this._record).setCachedErrorResult(errorCode);
    }

    private boolean convertCellValueToBoolean() {
        int i = this._cellType;
        if (i != 0) {
            if (i == 1) {
                return Boolean.valueOf(this._book.getWorkbook().getSSTString(((LabelSSTRecord) this._record).getSSTIndex()).getString()).booleanValue();
            } else if (i != 2) {
                if (i != 3) {
                    if (i == 4) {
                        return ((BoolErrRecord) this._record).getBooleanValue();
                    }
                    if (i != 5) {
                        throw new RuntimeException("Unexpected cell type (" + this._cellType + ")");
                    }
                }
                return false;
            } else {
                FormulaRecord fr = ((FormulaRecordAggregate) this._record).getFormulaRecord();
                checkFormulaCachedValueType(4, fr);
                return fr.getCachedBooleanValue();
            }
        } else if (((NumberRecord) this._record).getValue() != 0.0d) {
            return true;
        } else {
            return false;
        }
    }

    private String convertCellValueToString() {
        int i = this._cellType;
        if (i == 0) {
            return NumberToTextConverter.toText(((NumberRecord) this._record).getValue());
        }
        if (i == 1) {
            return this._book.getWorkbook().getSSTString(((LabelSSTRecord) this._record).getSSTIndex()).getString();
        } else if (i == 2) {
            FormulaRecordAggregate fra = (FormulaRecordAggregate) this._record;
            FormulaRecord fr = fra.getFormulaRecord();
            int cachedResultType = fr.getCachedResultType();
            if (cachedResultType == 0) {
                return NumberToTextConverter.toText(fr.getValue());
            }
            if (cachedResultType == 1) {
                return fra.getStringValue();
            }
            if (cachedResultType == 4) {
                return fr.getCachedBooleanValue() ? "TRUE" : "FALSE";
            }
            if (cachedResultType == 5) {
                return HSSFErrorConstants.getText(fr.getCachedErrorValue());
            }
            throw new IllegalStateException("Unexpected formula result type (" + this._cellType + ")");
        } else if (i == 3) {
            return "";
        } else {
            if (i == 4) {
                return ((BoolErrRecord) this._record).getBooleanValue() ? "TRUE" : "FALSE";
            }
            if (i == 5) {
                return HSSFErrorConstants.getText(((BoolErrRecord) this._record).getErrorValue());
            }
            throw new IllegalStateException("Unexpected cell type (" + this._cellType + ")");
        }
    }

    public boolean getBooleanCellValue() {
        int i = this._cellType;
        if (i == 2) {
            FormulaRecord fr = ((FormulaRecordAggregate) this._record).getFormulaRecord();
            checkFormulaCachedValueType(4, fr);
            return fr.getCachedBooleanValue();
        } else if (i == 3) {
            return false;
        } else {
            if (i == 4) {
                return ((BoolErrRecord) this._record).getBooleanValue();
            }
            throw typeMismatch(4, i, false);
        }
    }

    public byte getErrorCellValue() {
        int i = this._cellType;
        if (i == 2) {
            FormulaRecord fr = ((FormulaRecordAggregate) this._record).getFormulaRecord();
            checkFormulaCachedValueType(5, fr);
            return (byte) fr.getCachedErrorValue();
        } else if (i == 5) {
            return ((BoolErrRecord) this._record).getErrorValue();
        } else {
            throw typeMismatch(5, i, false);
        }
    }

    public void setCellStyle(CellStyle style) {
        setCellStyle((HSSFCellStyle) style);
    }

    public void setCellStyle(HSSFCellStyle style) {
        style.verifyBelongsToWorkbook(this._book);
        this._record.setXFIndex(style.getIndex());
    }

    public HSSFCellStyle getCellStyle() {
        short styleIndex = this._record.getXFIndex();
        return new HSSFCellStyle(styleIndex, this._book.getWorkbook().getExFormatAt(styleIndex), this._book);
    }

    /* access modifiers changed from: protected */
    public CellValueRecordInterface getCellValueRecord() {
        return this._record;
    }

    private static void checkBounds(int cellIndex) {
        if (cellIndex < 0 || cellIndex > LAST_COLUMN_NUMBER) {
            throw new IllegalArgumentException("Invalid column index (" + cellIndex + ").  Allowable column range for " + FILE_FORMAT_NAME + " is (0.." + LAST_COLUMN_NUMBER + ") or ('A'..'" + LAST_COLUMN_NAME + "')");
        }
    }

    public void setAsActiveCell() {
        int row = this._record.getRow();
        short col = this._record.getColumn();
        this._sheet.getSheet().setActiveCellRow(row);
        this._sheet.getSheet().setActiveCellCol(col);
    }

    public String toString() {
        int cellType = getCellType();
        if (cellType != 0) {
            if (cellType == 1) {
                return getStringCellValue();
            }
            if (cellType == 2) {
                return getCellFormula();
            }
            if (cellType == 3) {
                return "";
            }
            if (cellType == 4) {
                return getBooleanCellValue() ? "TRUE" : "FALSE";
            }
            if (cellType == 5) {
                return ErrorEval.getText(((BoolErrRecord) this._record).getErrorValue());
            }
            return "Unknown Cell Type: " + getCellType();
        } else if (HSSFDateUtil.isCellDateFormatted(this)) {
            return new SimpleDateFormat("dd-MMM-yyyy").format(getDateCellValue());
        } else {
            return String.valueOf(getNumericCellValue());
        }
    }

    public void setCellComment(Comment comment) {
        if (comment == null) {
            removeCellComment();
            return;
        }
        comment.setRow(this._record.getRow());
        comment.setColumn(this._record.getColumn());
        this._comment = (HSSFComment) comment;
    }

    public HSSFComment getCellComment() {
        if (this._comment == null) {
            this._comment = findCellComment(this._sheet.getSheet(), this._record.getRow(), this._record.getColumn());
        }
        return this._comment;
    }

    public void removeCellComment() {
        HSSFComment comment = findCellComment(this._sheet.getSheet(), this._record.getRow(), this._record.getColumn());
        this._comment = null;
        if (comment != null) {
            List<RecordBase> sheetRecords = this._sheet.getSheet().getRecords();
            sheetRecords.remove(comment.getNoteRecord());
            if (comment.getTextObjectRecord() != null) {
                TextObjectRecord txo = comment.getTextObjectRecord();
                int txoAt = sheetRecords.indexOf(txo);
                if (!(sheetRecords.get(txoAt - 3) instanceof DrawingRecord) || !(sheetRecords.get(txoAt - 2) instanceof ObjRecord) || !(sheetRecords.get(txoAt - 1) instanceof DrawingRecord)) {
                    throw new IllegalStateException("Found the wrong records before the TextObjectRecord, can't remove comment");
                }
                sheetRecords.remove(txoAt - 1);
                sheetRecords.remove(txoAt - 2);
                sheetRecords.remove(txoAt - 3);
                sheetRecords.remove(txo);
            }
        }
    }

    protected static HSSFComment findCellComment(InternalSheet sheet, int row, int column) {
        HSSFComment comment = null;
        Map<Integer, TextObjectRecord> noteTxo = new HashMap<>();
        int i = 0;
        Iterator<RecordBase> it = sheet.getRecords().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            RecordBase rec = it.next();
            boolean z = false;
            if (rec instanceof NoteRecord) {
                NoteRecord note = (NoteRecord) rec;
                if (note.getRow() != row || note.getColumn() != column) {
                    i++;
                } else if (i < noteTxo.size()) {
                    TextObjectRecord txo = noteTxo.get(Integer.valueOf(note.getShapeId()));
                    if (txo != null) {
                        comment = new HSSFComment(note, txo);
                        comment.setRow(note.getRow());
                        comment.setColumn(note.getColumn());
                        comment.setAuthor(note.getAuthor());
                        if (note.getFlags() == 2) {
                            z = true;
                        }
                        comment.setVisible(z);
                        comment.setString(txo.getStr());
                    } else {
                        POILogger pOILogger = log;
                        int i2 = POILogger.WARN;
                        pOILogger.log(i2, (Object) "Failed to match NoteRecord and TextObjectRecord, row: " + row + ", column: " + column);
                    }
                } else {
                    POILogger pOILogger2 = log;
                    int i3 = POILogger.WARN;
                    pOILogger2.log(i3, (Object) "Failed to match NoteRecord and TextObjectRecord, row: " + row + ", column: " + column);
                }
            } else if (rec instanceof ObjRecord) {
                SubRecord sub = ((ObjRecord) rec).getSubRecords().get(0);
                if (sub instanceof CommonObjectDataSubRecord) {
                    CommonObjectDataSubRecord cmo = (CommonObjectDataSubRecord) sub;
                    if (cmo.getObjectType() == 25) {
                        while (true) {
                            if (!it.hasNext()) {
                                break;
                            }
                            RecordBase rec2 = it.next();
                            if (rec2 instanceof TextObjectRecord) {
                                noteTxo.put(Integer.valueOf(cmo.getObjectId()), (TextObjectRecord) rec2);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return comment;
    }

    public HSSFHyperlink getHyperlink() {
        for (RecordBase rec : this._sheet.getSheet().getRecords()) {
            if (rec instanceof HyperlinkRecord) {
                HyperlinkRecord link = (HyperlinkRecord) rec;
                if (link.getFirstColumn() == this._record.getColumn() && link.getFirstRow() == this._record.getRow()) {
                    return new HSSFHyperlink(link);
                }
            }
        }
        return null;
    }

    public void setHyperlink(Hyperlink hyperlink) {
        HSSFHyperlink link = (HSSFHyperlink) hyperlink;
        link.setFirstRow(this._record.getRow());
        link.setLastRow(this._record.getRow());
        link.setFirstColumn(this._record.getColumn());
        link.setLastColumn(this._record.getColumn());
        int type = link.getType();
        if (type != 1) {
            if (type == 2) {
                link.setLabel("place");
            } else if (type != 3) {
                if (type == 4) {
                    link.setLabel("file");
                }
            }
            List<RecordBase> records = this._sheet.getSheet().getRecords();
            records.add(records.size() - 1, link.record);
        }
        link.setLabel("url");
        List<RecordBase> records2 = this._sheet.getSheet().getRecords();
        records2.add(records2.size() - 1, link.record);
    }

    public int getCachedFormulaResultType() {
        if (this._cellType == 2) {
            return ((FormulaRecordAggregate) this._record).getFormulaRecord().getCachedResultType();
        }
        throw new IllegalStateException("Only formula cells have cached results");
    }

    /* access modifiers changed from: package-private */
    public void setCellArrayFormula(CellRangeAddress range) {
        setCellType(2, false, this._record.getRow(), this._record.getColumn(), this._record.getXFIndex());
        ((FormulaRecordAggregate) this._record).setParsedExpression(new Ptg[]{new ExpPtg(range.getFirstRow(), range.getFirstColumn())});
    }

    public CellRangeAddress getArrayFormulaRange() {
        if (this._cellType == 2) {
            return ((FormulaRecordAggregate) this._record).getArrayFormulaRange();
        }
        String ref = new CellReference((Cell) this).formatAsString();
        throw new IllegalStateException("Cell " + ref + " is not part of an array formula.");
    }

    public boolean isPartOfArrayFormulaGroup() {
        if (this._cellType != 2) {
            return false;
        }
        return ((FormulaRecordAggregate) this._record).isPartOfArrayFormula();
    }

    /* access modifiers changed from: package-private */
    public void notifyArrayFormulaChanging(String msg) {
        if (getArrayFormulaRange().getNumberOfCells() <= 1) {
            getRow().getSheet().removeArrayFormula(this);
            return;
        }
        throw new IllegalStateException(msg);
    }

    /* access modifiers changed from: package-private */
    public void notifyArrayFormulaChanging() {
        CellReference ref = new CellReference((Cell) this);
        notifyArrayFormulaChanging("Cell " + ref.formatAsString() + " is part of a multi-cell array formula. " + "You cannot change part of an array.");
    }
}
