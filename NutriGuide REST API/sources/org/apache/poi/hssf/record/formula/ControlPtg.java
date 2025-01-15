package org.apache.poi.hssf.record.formula;

public abstract class ControlPtg extends Ptg {
    public boolean isBaseToken() {
        return true;
    }

    public final byte getDefaultOperandClass() {
        throw new IllegalStateException("Control tokens are not classified");
    }
}
