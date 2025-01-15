package org.apache.poi.ss.util;

import org.apache.poi.ss.SpreadsheetVersion;

public abstract class CellRangeAddressBase {
    private int _firstCol;
    private int _firstRow;
    private int _lastCol;
    private int _lastRow;

    protected CellRangeAddressBase(int firstRow, int lastRow, int firstCol, int lastCol) {
        this._firstRow = firstRow;
        this._lastRow = lastRow;
        this._firstCol = firstCol;
        this._lastCol = lastCol;
    }

    public void validate(SpreadsheetVersion ssVersion) {
        validateRow(this._firstRow, ssVersion);
        validateRow(this._lastRow, ssVersion);
        validateColumn(this._firstCol, ssVersion);
        validateColumn(this._lastCol, ssVersion);
    }

    private static void validateRow(int row, SpreadsheetVersion ssVersion) {
        int maxrow = ssVersion.getLastRowIndex();
        if (row > maxrow) {
            throw new IllegalArgumentException("Maximum row number is " + maxrow);
        } else if (row < 0) {
            throw new IllegalArgumentException("Minumum row number is 0");
        }
    }

    private static void validateColumn(int column, SpreadsheetVersion ssVersion) {
        int maxcol = ssVersion.getLastColumnIndex();
        if (column > maxcol) {
            throw new IllegalArgumentException("Maximum column number is " + maxcol);
        } else if (column < 0) {
            throw new IllegalArgumentException("Minimum column number is 0");
        }
    }

    public final boolean isFullColumnRange() {
        return this._firstRow == 0 && this._lastRow == SpreadsheetVersion.EXCEL97.getLastRowIndex();
    }

    public final boolean isFullRowRange() {
        return this._firstCol == 0 && this._lastCol == SpreadsheetVersion.EXCEL97.getLastColumnIndex();
    }

    public final int getFirstColumn() {
        return this._firstCol;
    }

    public final int getFirstRow() {
        return this._firstRow;
    }

    public final int getLastColumn() {
        return this._lastCol;
    }

    public final int getLastRow() {
        return this._lastRow;
    }

    public boolean isInRange(int rowInd, int colInd) {
        return this._firstRow <= rowInd && rowInd <= this._lastRow && this._firstCol <= colInd && colInd <= this._lastCol;
    }

    public final void setFirstColumn(int firstCol) {
        this._firstCol = firstCol;
    }

    public final void setFirstRow(int firstRow) {
        this._firstRow = firstRow;
    }

    public final void setLastColumn(int lastCol) {
        this._lastCol = lastCol;
    }

    public final void setLastRow(int lastRow) {
        this._lastRow = lastRow;
    }

    public int getNumberOfCells() {
        return ((this._lastRow - this._firstRow) + 1) * ((this._lastCol - this._firstCol) + 1);
    }

    public final String toString() {
        CellReference crA = new CellReference(this._firstRow, this._firstCol);
        CellReference crB = new CellReference(this._lastRow, this._lastCol);
        return getClass().getName() + " [" + crA.formatAsString() + ":" + crB.formatAsString() + "]";
    }
}
