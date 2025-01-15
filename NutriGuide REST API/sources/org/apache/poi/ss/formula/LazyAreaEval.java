package org.apache.poi.ss.formula;

import org.apache.poi.hssf.record.formula.AreaI;
import org.apache.poi.hssf.record.formula.eval.AreaEval;
import org.apache.poi.hssf.record.formula.eval.AreaEvalBase;
import org.apache.poi.hssf.record.formula.eval.ValueEval;
import org.apache.poi.hssf.util.CellReference;

final class LazyAreaEval extends AreaEvalBase {
    private final SheetRefEvaluator _evaluator;

    LazyAreaEval(AreaI ptg, SheetRefEvaluator evaluator) {
        super(ptg);
        this._evaluator = evaluator;
    }

    public LazyAreaEval(int firstRowIndex, int firstColumnIndex, int lastRowIndex, int lastColumnIndex, SheetRefEvaluator evaluator) {
        super(firstRowIndex, firstColumnIndex, lastRowIndex, lastColumnIndex);
        this._evaluator = evaluator;
    }

    public ValueEval getRelativeValue(int relativeRowIndex, int relativeColumnIndex) {
        return this._evaluator.getEvalForCell((getFirstRow() + relativeRowIndex) & 65535, (getFirstColumn() + relativeColumnIndex) & 255);
    }

    public AreaEval offset(int relFirstRowIx, int relLastRowIx, int relFirstColIx, int relLastColIx) {
        return new LazyAreaEval(new AreaI.OffsetArea(getFirstRow(), getFirstColumn(), relFirstRowIx, relLastRowIx, relFirstColIx, relLastColIx), this._evaluator);
    }

    public LazyAreaEval getRow(int rowIndex) {
        if (rowIndex < getHeight()) {
            int absRowIx = getFirstRow() + rowIndex;
            return new LazyAreaEval(absRowIx, getFirstColumn(), absRowIx, getLastColumn(), this._evaluator);
        }
        throw new IllegalArgumentException("Invalid rowIndex " + rowIndex + ".  Allowable range is (0.." + getHeight() + ").");
    }

    public LazyAreaEval getColumn(int columnIndex) {
        if (columnIndex < getWidth()) {
            int absColIx = getFirstColumn() + columnIndex;
            return new LazyAreaEval(getFirstRow(), absColIx, getLastRow(), absColIx, this._evaluator);
        }
        throw new IllegalArgumentException("Invalid columnIndex " + columnIndex + ".  Allowable range is (0.." + getWidth() + ").");
    }

    public String toString() {
        CellReference crA = new CellReference(getFirstRow(), getFirstColumn());
        CellReference crB = new CellReference(getLastRow(), getLastColumn());
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getName());
        sb.append("[");
        sb.append(this._evaluator.getSheetName());
        sb.append('!');
        sb.append(crA.formatAsString());
        sb.append(':');
        sb.append(crB.formatAsString());
        sb.append("]");
        return sb.toString();
    }
}
