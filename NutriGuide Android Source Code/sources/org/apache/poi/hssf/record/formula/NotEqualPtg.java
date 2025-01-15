package org.apache.poi.hssf.record.formula;

public final class NotEqualPtg extends ValueOperatorPtg {
    public static final ValueOperatorPtg instance = new NotEqualPtg();
    public static final byte sid = 14;

    private NotEqualPtg() {
    }

    /* access modifiers changed from: protected */
    public byte getSid() {
        return sid;
    }

    public int getNumberOfOperands() {
        return 2;
    }

    public String toFormulaString(String[] operands) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(operands[0]);
        buffer.append("<>");
        buffer.append(operands[1]);
        return buffer.toString();
    }
}
