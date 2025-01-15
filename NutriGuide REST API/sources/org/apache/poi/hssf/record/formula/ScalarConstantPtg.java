package org.apache.poi.hssf.record.formula;

public abstract class ScalarConstantPtg extends Ptg {
    public final boolean isBaseToken() {
        return true;
    }

    public final byte getDefaultOperandClass() {
        return 32;
    }

    public final String toString() {
        StringBuffer sb = new StringBuffer(64);
        sb.append(getClass().getName());
        sb.append(" [");
        sb.append(toFormulaString());
        sb.append("]");
        return sb.toString();
    }
}
