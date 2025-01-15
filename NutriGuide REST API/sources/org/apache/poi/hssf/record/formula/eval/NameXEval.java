package org.apache.poi.hssf.record.formula.eval;

import org.apache.poi.hssf.record.formula.NameXPtg;

public final class NameXEval implements ValueEval {
    private final NameXPtg _ptg;

    public NameXEval(NameXPtg ptg) {
        this._ptg = ptg;
    }

    public NameXPtg getPtg() {
        return this._ptg;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(64);
        sb.append(getClass().getName());
        sb.append(" [");
        sb.append(this._ptg.getSheetRefIndex());
        sb.append(", ");
        sb.append(this._ptg.getNameIndex());
        sb.append("]");
        return sb.toString();
    }
}
