package org.apache.poi.hssf.record.formula;

public final class PercentPtg extends ValueOperatorPtg {
    private static final String PERCENT = "%";
    public static final int SIZE = 1;
    public static final ValueOperatorPtg instance = new PercentPtg();
    public static final byte sid = 20;

    private PercentPtg() {
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
        buffer.append(operands[0]);
        buffer.append(PERCENT);
        return buffer.toString();
    }
}
