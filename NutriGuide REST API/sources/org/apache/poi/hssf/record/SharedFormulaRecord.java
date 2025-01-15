package org.apache.poi.hssf.record;

import org.apache.poi.hssf.record.formula.AreaPtg;
import org.apache.poi.hssf.record.formula.AreaPtgBase;
import org.apache.poi.hssf.record.formula.OperandPtg;
import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.hssf.record.formula.RefPtg;
import org.apache.poi.hssf.record.formula.RefPtgBase;
import org.apache.poi.hssf.util.CellRangeAddress8Bit;
import org.apache.poi.ss.formula.Formula;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

public final class SharedFormulaRecord extends SharedValueRecordBase {
    public static final short sid = 1212;
    private int field_5_reserved;
    private Formula field_7_parsed_expr;

    public SharedFormulaRecord() {
        this(new CellRangeAddress8Bit(0, 0, 0, 0));
    }

    private SharedFormulaRecord(CellRangeAddress8Bit range) {
        super(range);
        this.field_7_parsed_expr = Formula.create(Ptg.EMPTY_PTG_ARRAY);
    }

    public SharedFormulaRecord(RecordInputStream in) {
        super((LittleEndianInput) in);
        this.field_5_reserved = in.readShort();
        this.field_7_parsed_expr = Formula.read(in.readShort(), in, in.available());
    }

    /* access modifiers changed from: protected */
    public void serializeExtraData(LittleEndianOutput out) {
        out.writeShort(this.field_5_reserved);
        this.field_7_parsed_expr.serialize(out);
    }

    /* access modifiers changed from: protected */
    public int getExtraDataSize() {
        return this.field_7_parsed_expr.getEncodedSize() + 2;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[SHARED FORMULA (");
        buffer.append(HexDump.intToHex(1212));
        buffer.append("]\n");
        buffer.append("    .range      = ");
        buffer.append(getRange().toString());
        buffer.append("\n");
        buffer.append("    .reserved    = ");
        buffer.append(HexDump.shortToHex(this.field_5_reserved));
        buffer.append("\n");
        Ptg[] ptgs = this.field_7_parsed_expr.getTokens();
        for (int k = 0; k < ptgs.length; k++) {
            buffer.append("Formula[");
            buffer.append(k);
            buffer.append("]");
            Ptg ptg = ptgs[k];
            buffer.append(ptg.toString());
            buffer.append(ptg.getRVAType());
            buffer.append("\n");
        }
        buffer.append("[/SHARED FORMULA]\n");
        return buffer.toString();
    }

    public short getSid() {
        return sid;
    }

    public static Ptg[] convertSharedFormulas(Ptg[] ptgs, int formulaRow, int formulaColumn) {
        Ptg[] ptgArr = ptgs;
        int i = formulaRow;
        int i2 = formulaColumn;
        Ptg[] newPtgStack = new Ptg[ptgArr.length];
        for (int k = 0; k < ptgArr.length; k++) {
            Ptg ptg = ptgArr[k];
            byte originalOperandClass = -1;
            if (!ptg.isBaseToken()) {
                originalOperandClass = ptg.getPtgClass();
            }
            if (ptg instanceof RefPtgBase) {
                RefPtgBase refNPtg = (RefPtgBase) ptg;
                ptg = new RefPtg(fixupRelativeRow(i, refNPtg.getRow(), refNPtg.isRowRelative()), fixupRelativeColumn(i2, refNPtg.getColumn(), refNPtg.isColRelative()), refNPtg.isRowRelative(), refNPtg.isColRelative());
                ptg.setClass(originalOperandClass);
            } else if (ptg instanceof AreaPtgBase) {
                AreaPtgBase areaNPtg = (AreaPtgBase) ptg;
                ptg = new AreaPtg(fixupRelativeRow(i, areaNPtg.getFirstRow(), areaNPtg.isFirstRowRelative()), fixupRelativeRow(i, areaNPtg.getLastRow(), areaNPtg.isLastRowRelative()), fixupRelativeColumn(i2, areaNPtg.getFirstColumn(), areaNPtg.isFirstColRelative()), fixupRelativeColumn(i2, areaNPtg.getLastColumn(), areaNPtg.isLastColRelative()), areaNPtg.isFirstRowRelative(), areaNPtg.isLastRowRelative(), areaNPtg.isFirstColRelative(), areaNPtg.isLastColRelative());
                ptg.setClass(originalOperandClass);
            } else if (ptg instanceof OperandPtg) {
                ptg = ((OperandPtg) ptg).copy();
            }
            newPtgStack[k] = ptg;
        }
        return newPtgStack;
    }

    public Ptg[] getFormulaTokens(FormulaRecord formula) {
        int formulaRow = formula.getRow();
        int formulaColumn = formula.getColumn();
        if (isInRange(formulaRow, formulaColumn)) {
            return convertSharedFormulas(this.field_7_parsed_expr.getTokens(), formulaRow, formulaColumn);
        }
        throw new RuntimeException("Shared Formula Conversion: Coding Error");
    }

    private static int fixupRelativeColumn(int currentcolumn, int column, boolean relative) {
        if (relative) {
            return (column + currentcolumn) & 255;
        }
        return column;
    }

    private static int fixupRelativeRow(int currentrow, int row, boolean relative) {
        if (relative) {
            return (row + currentrow) & 65535;
        }
        return row;
    }

    public Object clone() {
        SharedFormulaRecord result = new SharedFormulaRecord(getRange());
        result.field_5_reserved = this.field_5_reserved;
        result.field_7_parsed_expr = this.field_7_parsed_expr.copy();
        return result;
    }

    public boolean isFormulaSame(SharedFormulaRecord other) {
        return this.field_7_parsed_expr.isSame(other.field_7_parsed_expr);
    }
}
