package org.apache.poi.hssf.record.formula;

public final class UnaryPlusPtg extends ValueOperatorPtg {
    private static final String ADD = "+";
    public static final ValueOperatorPtg instance = new UnaryPlusPtg();
    public static final byte sid = 18;

    private UnaryPlusPtg() {
    }

    /* access modifiers changed from: protected */
    public byte getSid() {
        return sid;
    }

    public int getNumberOfOperands() {
        return 1;
    }

    public String toFormulaString(String[] operands) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(ADD);
        buffer.append(operands[0]);
        return buffer.toString();
    }
}
