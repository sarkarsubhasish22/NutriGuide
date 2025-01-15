package org.apache.poi.hssf.record.formula;

public final class PowerPtg extends ValueOperatorPtg {
    public static final ValueOperatorPtg instance = new PowerPtg();
    public static final byte sid = 7;

    private PowerPtg() {
    }

    /* access modifiers changed from: protected */
    public byte getSid() {
        return 7;
    }

    public int getNumberOfOperands() {
        return 2;
    }

    public String toFormulaString(String[] operands) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(operands[0]);
        buffer.append("^");
        buffer.append(operands[1]);
        return buffer.toString();
    }
}
