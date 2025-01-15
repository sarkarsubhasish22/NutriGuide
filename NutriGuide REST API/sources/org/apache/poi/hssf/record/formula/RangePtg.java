package org.apache.poi.hssf.record.formula;

import org.apache.poi.util.LittleEndianOutput;

public final class RangePtg extends OperationPtg {
    public static final int SIZE = 1;
    public static final OperationPtg instance = new RangePtg();
    public static final byte sid = 17;

    private RangePtg() {
    }

    public final boolean isBaseToken() {
        return true;
    }

    public int getSize() {
        return 1;
    }

    public void write(LittleEndianOutput out) {
        out.writeByte(getPtgClass() + sid);
    }

    public String toFormulaString() {
        return ":";
    }

    public String toFormulaString(String[] operands) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(operands[0]);
        buffer.append(":");
        buffer.append(operands[1]);
        return buffer.toString();
    }

    public int getNumberOfOperands() {
        return 2;
    }
}
