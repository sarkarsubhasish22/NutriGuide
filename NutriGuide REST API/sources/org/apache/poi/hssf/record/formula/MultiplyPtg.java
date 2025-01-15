package org.apache.poi.hssf.record.formula;

public final class MultiplyPtg extends ValueOperatorPtg {
    public static final ValueOperatorPtg instance = new MultiplyPtg();
    public static final byte sid = 5;

    private MultiplyPtg() {
    }

    /* access modifiers changed from: protected */
    public byte getSid() {
        return 5;
    }

    public int getNumberOfOperands() {
        return 2;
    }

    public String toFormulaString(String[] operands) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(operands[0]);
        buffer.append("*");
        buffer.append(operands[1]);
        return buffer.toString();
    }
}
