package org.apache.poi.hssf.record.formula;

public final class DividePtg extends ValueOperatorPtg {
    public static final ValueOperatorPtg instance = new DividePtg();
    public static final byte sid = 6;

    private DividePtg() {
    }

    /* access modifiers changed from: protected */
    public byte getSid() {
        return 6;
    }

    public int getNumberOfOperands() {
        return 2;
    }

    public String toFormulaString(String[] operands) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(operands[0]);
        buffer.append("/");
        buffer.append(operands[1]);
        return buffer.toString();
    }
}
