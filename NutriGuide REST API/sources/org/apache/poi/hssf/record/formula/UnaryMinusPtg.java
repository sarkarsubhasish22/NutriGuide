package org.apache.poi.hssf.record.formula;

public final class UnaryMinusPtg extends ValueOperatorPtg {
    private static final String MINUS = "-";
    public static final ValueOperatorPtg instance = new UnaryMinusPtg();
    public static final byte sid = 19;

    private UnaryMinusPtg() {
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
        buffer.append(MINUS);
        buffer.append(operands[0]);
        return buffer.toString();
    }
}
