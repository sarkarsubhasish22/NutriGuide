package org.apache.poi.ss.formula;

import java.util.HashMap;
import java.util.Map;

final class PlainCellCache {
    private Map<Loc, PlainValueCellCacheEntry> _plainValueEntriesByLoc = new HashMap();

    public static final class Loc {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private final int _bookSheetColumn;
        private final int _rowIndex;

        static {
            Class<PlainCellCache> cls = PlainCellCache.class;
        }

        public Loc(int bookIndex, int sheetIndex, int rowIndex, int columnIndex) {
            this._bookSheetColumn = toBookSheetColumn(bookIndex, sheetIndex, columnIndex);
            this._rowIndex = rowIndex;
        }

        public static int toBookSheetColumn(int bookIndex, int sheetIndex, int columnIndex) {
            return ((bookIndex & 255) << 24) + ((sheetIndex & 255) << 16) + ((65535 & columnIndex) << 0);
        }

        public Loc(int bookSheetColumn, int rowIndex) {
            this._bookSheetColumn = bookSheetColumn;
            this._rowIndex = rowIndex;
        }

        public int hashCode() {
            return this._bookSheetColumn + (this._rowIndex * 17);
        }

        public boolean equals(Object obj) {
            if (obj instanceof Loc) {
                Loc other = (Loc) obj;
                return this._bookSheetColumn == other._bookSheetColumn && this._rowIndex == other._rowIndex;
            }
            throw new AssertionError("these package-private cache key instances are only compared to themselves");
        }

        public int getRowIndex() {
            return this._rowIndex;
        }

        public int getColumnIndex() {
            return this._bookSheetColumn & 65535;
        }
    }

    public void put(Loc key, PlainValueCellCacheEntry cce) {
        this._plainValueEntriesByLoc.put(key, cce);
    }

    public void clear() {
        this._plainValueEntriesByLoc.clear();
    }

    public PlainValueCellCacheEntry get(Loc key) {
        return this._plainValueEntriesByLoc.get(key);
    }

    public void remove(Loc key) {
        this._plainValueEntriesByLoc.remove(key);
    }
}
