package org.apache.poi.hssf.record.formula;

public final class AddPtg extends ValueOperatorPtg {
    private static final String ADD = "+";
    public static final ValueOperatorPtg instance = new AddPtg();
    public static final byte sid = 3;

    private AddPtg() {
    }

    /* access modifiers changed from: protected */
    public byte getSid() {
        return 3;
    }

    public int getNumberOfOperands() {
        return 2;
    }

    public String toFormulaString(String[] operands) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(operands[0]);
        buffer.append(ADD);
        buffer.append(operands[1]);
        return buffer.toString();
    }
}
