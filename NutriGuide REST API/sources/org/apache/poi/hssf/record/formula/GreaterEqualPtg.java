package org.apache.poi.hssf.record.formula;

public final class GreaterEqualPtg extends ValueOperatorPtg {
    public static final int SIZE = 1;
    public static final ValueOperatorPtg instance = new GreaterEqualPtg();
    public static final byte sid = 12;

    private GreaterEqualPtg() {
    }

    /* access modifiers changed from: protected */
    public byte getSid() {
        return 12;
    }

    public int getNumberOfOperands() {
        return 2;
    }

    public String toFormulaString(String[] operands) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(operands[0]);
        buffer.append(">=");
        buffer.append(operands[1]);
        return buffer.toString();
    }
}
