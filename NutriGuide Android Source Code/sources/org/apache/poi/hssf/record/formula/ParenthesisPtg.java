package org.apache.poi.hssf.record.formula;

import org.apache.poi.util.LittleEndianOutput;

public final class ParenthesisPtg extends ControlPtg {
    private static final int SIZE = 1;
    public static final ControlPtg instance = new ParenthesisPtg();
    public static final byte sid = 21;

    private ParenthesisPtg() {
    }

    public void write(LittleEndianOutput out) {
        out.writeByte(getPtgClass() + sid);
    }

    public int getSize() {
        return 1;
    }

    public String toFormulaString() {
        return "()";
    }

    public String toFormulaString(String[] operands) {
        return "(" + operands[0] + ")";
    }
}
