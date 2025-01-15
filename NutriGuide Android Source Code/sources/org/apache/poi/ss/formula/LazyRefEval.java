package org.apache.poi.ss.formula;

import org.apache.poi.hssf.record.formula.AreaI;
import org.apache.poi.hssf.record.formula.eval.AreaEval;
import org.apache.poi.hssf.record.formula.eval.RefEvalBase;
import org.apache.poi.hssf.record.formula.eval.ValueEval;
import org.apache.poi.hssf.util.CellReference;

final class LazyRefEval extends RefEvalBase {
    private final SheetRefEvaluator _evaluator;

    public LazyRefEval(int rowIndex, int columnIndex, SheetRefEvaluator sre) {
        super(rowIndex, columnIndex);
        if (sre != null) {
            this._evaluator = sre;
            return;
        }
        throw new IllegalArgumentException("sre must not be null");
    }

    public ValueEval getInnerValueEval() {
        return this._evaluator.getEvalForCell(getRow(), getColumn());
    }

    public AreaEval offset(int relFirstRowIx, int relLastRowIx, int relFirstColIx, int relLastColIx) {
        return new LazyAreaEval(new AreaI.OffsetArea(getRow(), getColumn(), relFirstRowIx, relLastRowIx, relFirstColIx, relLastColIx), this._evaluator);
    }

    public String toString() {
        CellReference cr = new CellReference(getRow(), getColumn());
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getName());
        sb.append("[");
        sb.append(this._evaluator.getSheetName());
        sb.append('!');
        sb.append(cr.formatAsString());
        sb.append("]");
        return sb.toString();
    }
}
