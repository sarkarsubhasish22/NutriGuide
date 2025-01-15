package org.apache.poi.ss.formula;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.util.CellReference;

final class FormulaUsedBlankCellSet {
    private final Map<BookSheetKey, BlankCellSheetGroup> _sheetGroupsByBookSheet = new HashMap();

    public static final class BookSheetKey {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private final int _bookIndex;
        private final int _sheetIndex;

        static {
            Class<FormulaUsedBlankCellSet> cls = FormulaUsedBlankCellSet.class;
        }

        public BookSheetKey(int bookIndex, int sheetIndex) {
            this._bookIndex = bookIndex;
            this._sheetIndex = sheetIndex;
        }

        public int hashCode() {
            return (this._bookIndex * 17) + this._sheetIndex;
        }

        public boolean equals(Object obj) {
            if (obj instanceof BookSheetKey) {
                BookSheetKey other = (BookSheetKey) obj;
                return this._bookIndex == other._bookIndex && this._sheetIndex == other._sheetIndex;
            }
            throw new AssertionError("these private cache key instances are only compared to themselves");
        }
    }

    private static final class BlankCellSheetGroup {
        private BlankCellRectangleGroup _currentRectangleGroup;
        private int _currentRowIndex = -1;
        private int _firstColumnIndex;
        private int _lastColumnIndex;
        private final List<BlankCellRectangleGroup> _rectangleGroups = new ArrayList();

        public void addCell(int rowIndex, int columnIndex) {
            int i = this._currentRowIndex;
            if (i == -1) {
                this._currentRowIndex = rowIndex;
                this._firstColumnIndex = columnIndex;
                this._lastColumnIndex = columnIndex;
            } else if (i == rowIndex && this._lastColumnIndex + 1 == columnIndex) {
                this._lastColumnIndex = columnIndex;
            } else {
                BlankCellRectangleGroup blankCellRectangleGroup = this._currentRectangleGroup;
                if (blankCellRectangleGroup == null) {
                    this._currentRectangleGroup = new BlankCellRectangleGroup(i, this._firstColumnIndex, this._lastColumnIndex);
                } else if (!blankCellRectangleGroup.acceptRow(i, this._firstColumnIndex, this._lastColumnIndex)) {
                    this._rectangleGroups.add(this._currentRectangleGroup);
                    this._currentRectangleGroup = new BlankCellRectangleGroup(this._currentRowIndex, this._firstColumnIndex, this._lastColumnIndex);
                }
                this._currentRowIndex = rowIndex;
                this._firstColumnIndex = columnIndex;
                this._lastColumnIndex = columnIndex;
            }
        }

        public boolean containsCell(int rowIndex, int columnIndex) {
            for (int i = this._rectangleGroups.size() - 1; i >= 0; i--) {
                if (this._rectangleGroups.get(i).containsCell(rowIndex, columnIndex)) {
                    return true;
                }
            }
            BlankCellRectangleGroup blankCellRectangleGroup = this._currentRectangleGroup;
            if (blankCellRectangleGroup != null && blankCellRectangleGroup.containsCell(rowIndex, columnIndex)) {
                return true;
            }
            int i2 = this._currentRowIndex;
            if (i2 == -1 || i2 != rowIndex || this._firstColumnIndex > columnIndex || columnIndex > this._lastColumnIndex) {
                return false;
            }
            return true;
        }
    }

    private static final class BlankCellRectangleGroup {
        private final int _firstColumnIndex;
        private final int _firstRowIndex;
        private final int _lastColumnIndex;
        private int _lastRowIndex;

        public BlankCellRectangleGroup(int firstRowIndex, int firstColumnIndex, int lastColumnIndex) {
            this._firstRowIndex = firstRowIndex;
            this._firstColumnIndex = firstColumnIndex;
            this._lastColumnIndex = lastColumnIndex;
            this._lastRowIndex = firstRowIndex;
        }

        public boolean containsCell(int rowIndex, int columnIndex) {
            if (columnIndex >= this._firstColumnIndex && columnIndex <= this._lastColumnIndex && rowIndex >= this._firstRowIndex && rowIndex <= this._lastRowIndex) {
                return true;
            }
            return false;
        }

        public boolean acceptRow(int rowIndex, int firstColumnIndex, int lastColumnIndex) {
            if (firstColumnIndex != this._firstColumnIndex || lastColumnIndex != this._lastColumnIndex || rowIndex != this._lastRowIndex + 1) {
                return false;
            }
            this._lastRowIndex = rowIndex;
            return true;
        }

        public String toString() {
            StringBuffer sb = new StringBuffer(64);
            CellReference crA = new CellReference(this._firstRowIndex, this._firstColumnIndex, false, false);
            CellReference crB = new CellReference(this._lastRowIndex, this._lastColumnIndex, false, false);
            sb.append(getClass().getName());
            sb.append(" [");
            sb.append(crA.formatAsString());
            sb.append(':');
            sb.append(crB.formatAsString());
            sb.append("]");
            return sb.toString();
        }
    }

    public void addCell(int bookIndex, int sheetIndex, int rowIndex, int columnIndex) {
        getSheetGroup(bookIndex, sheetIndex).addCell(rowIndex, columnIndex);
    }

    private BlankCellSheetGroup getSheetGroup(int bookIndex, int sheetIndex) {
        BookSheetKey key = new BookSheetKey(bookIndex, sheetIndex);
        BlankCellSheetGroup result = this._sheetGroupsByBookSheet.get(key);
        if (result != null) {
            return result;
        }
        BlankCellSheetGroup result2 = new BlankCellSheetGroup();
        this._sheetGroupsByBookSheet.put(key, result2);
        return result2;
    }

    public boolean containsCell(BookSheetKey key, int rowIndex, int columnIndex) {
        BlankCellSheetGroup bcsg = this._sheetGroupsByBookSheet.get(key);
        if (bcsg == null) {
            return false;
        }
        return bcsg.containsCell(rowIndex, columnIndex);
    }

    public boolean isEmpty() {
        return this._sheetGroupsByBookSheet.isEmpty();
    }
}
