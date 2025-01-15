package org.apache.poi.ss.util;

public class Region implements Comparable<Region> {
    private short _colFrom;
    private short _colTo;
    private int _rowFrom;
    private int _rowTo;

    public Region() {
    }

    public Region(int rowFrom, short colFrom, int rowTo, short colTo) {
        this._rowFrom = rowFrom;
        this._rowTo = rowTo;
        this._colFrom = colFrom;
        this._colTo = colTo;
    }

    public Region(String ref) {
        CellReference cellReferenceFrom = new CellReference(ref.substring(0, ref.indexOf(":")));
        CellReference cellReferenceTo = new CellReference(ref.substring(ref.indexOf(":") + 1));
        this._rowFrom = cellReferenceFrom.getRow();
        this._colFrom = cellReferenceFrom.getCol();
        this._rowTo = cellReferenceTo.getRow();
        this._colTo = cellReferenceTo.getCol();
    }

    public short getColumnFrom() {
        return this._colFrom;
    }

    public int getRowFrom() {
        return this._rowFrom;
    }

    public short getColumnTo() {
        return this._colTo;
    }

    public int getRowTo() {
        return this._rowTo;
    }

    public void setColumnFrom(short colFrom) {
        this._colFrom = colFrom;
    }

    public void setRowFrom(int rowFrom) {
        this._rowFrom = rowFrom;
    }

    public void setColumnTo(short colTo) {
        this._colTo = colTo;
    }

    public void setRowTo(int rowTo) {
        this._rowTo = rowTo;
    }

    public boolean contains(int row, short col) {
        if (this._rowFrom > row || this._rowTo < row || this._colFrom > col || this._colTo < col) {
            return false;
        }
        return true;
    }

    public boolean equals(Region r) {
        return compareTo(r) == 0;
    }

    public int compareTo(Region r) {
        if (getRowFrom() == r.getRowFrom() && getColumnFrom() == r.getColumnFrom() && getRowTo() == r.getRowTo() && getColumnTo() == r.getColumnTo()) {
            return 0;
        }
        if (getRowFrom() < r.getRowFrom() || getColumnFrom() < r.getColumnFrom() || getRowTo() < r.getRowTo() || getColumnTo() < r.getColumnTo()) {
            return 1;
        }
        return -1;
    }

    public int getArea() {
        return ((this._rowTo - this._rowFrom) + 1) * ((this._colTo - this._colFrom) + 1);
    }

    public static Region[] convertCellRangesToRegions(CellRangeAddress[] cellRanges) {
        int size = cellRanges.length;
        if (size < 1) {
            return new Region[0];
        }
        Region[] result = new Region[size];
        for (int i = 0; i != size; i++) {
            result[i] = convertToRegion(cellRanges[i]);
        }
        return result;
    }

    private static Region convertToRegion(CellRangeAddress cr) {
        return new Region(cr.getFirstRow(), (short) cr.getFirstColumn(), cr.getLastRow(), (short) cr.getLastColumn());
    }

    public static CellRangeAddress[] convertRegionsToCellRanges(Region[] regions) {
        int size = regions.length;
        if (size < 1) {
            return new CellRangeAddress[0];
        }
        CellRangeAddress[] result = new CellRangeAddress[size];
        for (int i = 0; i != size; i++) {
            result[i] = convertToCellRangeAddress(regions[i]);
        }
        return result;
    }

    public static CellRangeAddress convertToCellRangeAddress(Region r) {
        return new CellRangeAddress(r.getRowFrom(), r.getRowTo(), r.getColumnFrom(), r.getColumnTo());
    }

    public String getRegionRef() {
        CellReference cellRefFrom = new CellReference(this._rowFrom, this._colFrom);
        CellReference cellRefTo = new CellReference(this._rowTo, this._colTo);
        return cellRefFrom.formatAsString() + ":" + cellRefTo.formatAsString();
    }
}
