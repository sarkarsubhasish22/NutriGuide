package org.apache.poi.hssf.record.formula.eval;

import org.apache.poi.hssf.record.formula.AreaI;

public abstract class AreaEvalBase implements AreaEval {
    private final int _firstColumn;
    private final int _firstRow;
    private final int _lastColumn;
    private final int _lastRow;
    private final int _nColumns;
    private final int _nRows;

    public abstract ValueEval getRelativeValue(int i, int i2);

    protected AreaEvalBase(int firstRow, int firstColumn, int lastRow, int lastColumn) {
        this._firstColumn = firstColumn;
        this._firstRow = firstRow;
        this._lastColumn = lastColumn;
        this._lastRow = lastRow;
        this._nColumns = (lastColumn - firstColumn) + 1;
        this._nRows = (lastRow - firstRow) + 1;
    }

    protected AreaEvalBase(AreaI ptg) {
        int firstRow = ptg.getFirstRow();
        this._firstRow = firstRow;
        int firstColumn = ptg.getFirstColumn();
        this._firstColumn = firstColumn;
        int lastRow = ptg.getLastRow();
        this._lastRow = lastRow;
        int lastColumn = ptg.getLastColumn();
        this._lastColumn = lastColumn;
        this._nColumns = (lastColumn - firstColumn) + 1;
        this._nRows = (lastRow - firstRow) + 1;
    }

    public final int getFirstColumn() {
        return this._firstColumn;
    }

    public final int getFirstRow() {
        return this._firstRow;
    }

    public final int getLastColumn() {
        return this._lastColumn;
    }

    public final int getLastRow() {
        return this._lastRow;
    }

    public final ValueEval getAbsoluteValue(int row, int col) {
        int rowOffsetIx = row - this._firstRow;
        int colOffsetIx = col - this._firstColumn;
        if (rowOffsetIx < 0 || rowOffsetIx >= this._nRows) {
            throw new IllegalArgumentException("Specified row index (" + row + ") is outside the allowed range (" + this._firstRow + ".." + this._lastRow + ")");
        } else if (colOffsetIx >= 0 && colOffsetIx < this._nColumns) {
            return getRelativeValue(rowOffsetIx, colOffsetIx);
        } else {
            throw new IllegalArgumentException("Specified column index (" + col + ") is outside the allowed range (" + this._firstColumn + ".." + col + ")");
        }
    }

    public final boolean contains(int row, int col) {
        return this._firstRow <= row && this._lastRow >= row && this._firstColumn <= col && this._lastColumn >= col;
    }

    public final boolean containsRow(int row) {
        return this._firstRow <= row && this._lastRow >= row;
    }

    public final boolean containsColumn(int col) {
        return this._firstColumn <= col && this._lastColumn >= col;
    }

    public final boolean isColumn() {
        return this._firstColumn == this._lastColumn;
    }

    public final boolean isRow() {
        return this._firstRow == this._lastRow;
    }

    public int getHeight() {
        return (this._lastRow - this._firstRow) + 1;
    }

    public final ValueEval getValue(int row, int col) {
        return getRelativeValue(row, col);
    }

    public int getWidth() {
        return (this._lastColumn - this._firstColumn) + 1;
    }
}
