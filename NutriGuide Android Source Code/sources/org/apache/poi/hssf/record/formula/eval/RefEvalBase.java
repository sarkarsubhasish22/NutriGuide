package org.apache.poi.hssf.record.formula.eval;

public abstract class RefEvalBase implements RefEval {
    private final int _columnIndex;
    private final int _rowIndex;

    protected RefEvalBase(int rowIndex, int columnIndex) {
        this._rowIndex = rowIndex;
        this._columnIndex = columnIndex;
    }

    public final int getRow() {
        return this._rowIndex;
    }

    public final int getColumn() {
        return this._columnIndex;
    }
}
