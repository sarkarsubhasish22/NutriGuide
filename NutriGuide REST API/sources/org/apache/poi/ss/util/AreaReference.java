package org.apache.poi.ss.util;

import java.util.ArrayList;
import java.util.StringTokenizer;
import org.apache.poi.ss.SpreadsheetVersion;

public class AreaReference {
    private static final char CELL_DELIMITER = ':';
    private static final char SHEET_NAME_DELIMITER = '!';
    private static final char SPECIAL_NAME_DELIMITER = '\'';
    private final CellReference _firstCell;
    private final boolean _isSingleCell;
    private final CellReference _lastCell;

    public AreaReference(String reference) {
        if (isContiguous(reference)) {
            String[] parts = separateAreaRefs(reference);
            String part0 = parts[0];
            if (parts.length == 1) {
                CellReference cellReference = new CellReference(part0);
                this._firstCell = cellReference;
                this._lastCell = cellReference;
                this._isSingleCell = true;
            } else if (parts.length == 2) {
                String part1 = parts[1];
                if (!isPlainColumn(part0)) {
                    this._firstCell = new CellReference(part0);
                    this._lastCell = new CellReference(part1);
                    this._isSingleCell = part0.equals(part1);
                } else if (isPlainColumn(part1)) {
                    boolean firstIsAbs = CellReference.isPartAbsolute(part0);
                    boolean lastIsAbs = CellReference.isPartAbsolute(part1);
                    int col0 = CellReference.convertColStringToIndex(part0);
                    int col1 = CellReference.convertColStringToIndex(part1);
                    this._firstCell = new CellReference(0, col0, true, firstIsAbs);
                    this._lastCell = new CellReference(65535, col1, true, lastIsAbs);
                    this._isSingleCell = false;
                } else {
                    throw new RuntimeException("Bad area ref '" + reference + "'");
                }
            } else {
                throw new IllegalArgumentException("Bad area ref '" + reference + "'");
            }
        } else {
            throw new IllegalArgumentException("References passed to the AreaReference must be contiguous, use generateContiguous(ref) if you have non-contiguous references");
        }
    }

    private boolean isPlainColumn(String refPart) {
        for (int i = refPart.length() - 1; i >= 0; i--) {
            int ch = refPart.charAt(i);
            if ((ch != 36 || i != 0) && (ch < 65 || ch > 90)) {
                return false;
            }
        }
        return true;
    }

    public AreaReference(CellReference topLeft, CellReference botRight) {
        boolean lastRowAbs;
        int lastRow;
        boolean firstRowAbs;
        int firstRow;
        boolean lastColAbs;
        int lastColumn;
        boolean firstColAbs;
        int firstColumn;
        boolean z = true;
        boolean swapRows = topLeft.getRow() > botRight.getRow();
        boolean swapCols = topLeft.getCol() <= botRight.getCol() ? false : z;
        if (swapRows || swapCols) {
            if (swapRows) {
                firstRow = botRight.getRow();
                firstRowAbs = botRight.isRowAbsolute();
                lastRow = topLeft.getRow();
                lastRowAbs = topLeft.isRowAbsolute();
            } else {
                firstRow = topLeft.getRow();
                firstRowAbs = topLeft.isRowAbsolute();
                lastRow = botRight.getRow();
                lastRowAbs = botRight.isRowAbsolute();
            }
            if (swapCols) {
                firstColumn = botRight.getCol();
                firstColAbs = botRight.isColAbsolute();
                lastColumn = topLeft.getCol();
                lastColAbs = topLeft.isColAbsolute();
            } else {
                firstColumn = topLeft.getCol();
                firstColAbs = topLeft.isColAbsolute();
                lastColumn = botRight.getCol();
                lastColAbs = botRight.isColAbsolute();
            }
            this._firstCell = new CellReference(firstRow, firstColumn, firstRowAbs, firstColAbs);
            this._lastCell = new CellReference(lastRow, lastColumn, lastRowAbs, lastColAbs);
        } else {
            this._firstCell = topLeft;
            this._lastCell = botRight;
        }
        this._isSingleCell = false;
    }

    public static boolean isContiguous(String reference) {
        if (reference.indexOf(44) == -1) {
            return true;
        }
        return false;
    }

    public static AreaReference getWholeRow(String start, String end) {
        return new AreaReference("$A" + start + ":$IV" + end);
    }

    public static AreaReference getWholeColumn(String start, String end) {
        return new AreaReference(start + "$1:" + end + "$65536");
    }

    public static boolean isWholeColumnReference(CellReference topLeft, CellReference botRight) {
        if (topLeft.getRow() != 0 || !topLeft.isRowAbsolute() || botRight.getRow() != SpreadsheetVersion.EXCEL97.getLastRowIndex() || !botRight.isRowAbsolute()) {
            return false;
        }
        return true;
    }

    public boolean isWholeColumnReference() {
        return isWholeColumnReference(this._firstCell, this._lastCell);
    }

    public static AreaReference[] generateContiguous(String reference) {
        ArrayList refs = new ArrayList();
        StringTokenizer st = new StringTokenizer(reference, ",");
        while (st.hasMoreTokens()) {
            refs.add(new AreaReference(st.nextToken()));
        }
        return (AreaReference[]) refs.toArray(new AreaReference[refs.size()]);
    }

    public boolean isSingleCell() {
        return this._isSingleCell;
    }

    public CellReference getFirstCell() {
        return this._firstCell;
    }

    public CellReference getLastCell() {
        return this._lastCell;
    }

    public CellReference[] getAllReferencedCells() {
        if (this._isSingleCell) {
            return new CellReference[]{this._firstCell};
        }
        int minRow = Math.min(this._firstCell.getRow(), this._lastCell.getRow());
        int maxRow = Math.max(this._firstCell.getRow(), this._lastCell.getRow());
        int minCol = Math.min(this._firstCell.getCol(), this._lastCell.getCol());
        int maxCol = Math.max(this._firstCell.getCol(), this._lastCell.getCol());
        String sheetName = this._firstCell.getSheetName();
        ArrayList refs = new ArrayList();
        for (int row = minRow; row <= maxRow; row++) {
            for (int col = minCol; col <= maxCol; col++) {
                refs.add(new CellReference(sheetName, row, col, this._firstCell.isRowAbsolute(), this._firstCell.isColAbsolute()));
            }
        }
        return (CellReference[]) refs.toArray(new CellReference[refs.size()]);
    }

    public String formatAsString() {
        if (isWholeColumnReference()) {
            return CellReference.convertNumToColString(this._firstCell.getCol()) + ":" + CellReference.convertNumToColString(this._lastCell.getCol());
        }
        StringBuffer sb = new StringBuffer(32);
        sb.append(this._firstCell.formatAsString());
        if (!this._isSingleCell) {
            sb.append(CELL_DELIMITER);
            if (this._lastCell.getSheetName() == null) {
                sb.append(this._lastCell.formatAsString());
            } else {
                this._lastCell.appendCellReference(sb);
            }
        }
        return sb.toString();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(64);
        sb.append(getClass().getName());
        sb.append(" [");
        sb.append(formatAsString());
        sb.append("]");
        return sb.toString();
    }

    private static String[] separateAreaRefs(String reference) {
        int len = reference.length();
        int delimiterPos = -1;
        boolean insideDelimitedName = false;
        int i = 0;
        while (i < len) {
            char charAt = reference.charAt(i);
            if (charAt != '\'') {
                if (charAt == ':' && !insideDelimitedName) {
                    if (delimiterPos < 0) {
                        delimiterPos = i;
                    } else {
                        throw new IllegalArgumentException("More than one cell delimiter ':' appears in area reference '" + reference + "'");
                    }
                }
            } else if (!insideDelimitedName) {
                insideDelimitedName = true;
            } else if (i >= len - 1) {
                throw new IllegalArgumentException("Area reference '" + reference + "' ends with special name delimiter '" + SPECIAL_NAME_DELIMITER + "'");
            } else if (reference.charAt(i + 1) == '\'') {
                i++;
            } else {
                insideDelimitedName = false;
            }
            i++;
        }
        if (delimiterPos < 0) {
            return new String[]{reference};
        }
        String partA = reference.substring(0, delimiterPos);
        String partB = reference.substring(delimiterPos + 1);
        if (partB.indexOf(33) < 0) {
            int plingPos = partA.lastIndexOf(33);
            if (plingPos < 0) {
                return new String[]{partA, partB};
            }
            String sheetName = partA.substring(0, plingPos + 1);
            return new String[]{partA, sheetName + partB};
        }
        throw new RuntimeException("Unexpected ! in second cell reference of '" + reference + "'");
    }
}
