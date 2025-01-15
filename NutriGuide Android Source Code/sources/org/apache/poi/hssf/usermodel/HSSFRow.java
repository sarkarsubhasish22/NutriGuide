package org.apache.poi.hssf.usermodel;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.hssf.record.RowRecord;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;

public final class HSSFRow implements Row {
    public static final int INITIAL_CAPACITY = 5;
    private HSSFWorkbook book;
    /* access modifiers changed from: private */
    public HSSFCell[] cells;
    private RowRecord row;
    private int rowNum;
    private HSSFSheet sheet;

    HSSFRow(HSSFWorkbook book2, HSSFSheet sheet2, int rowNum2) {
        this(book2, sheet2, new RowRecord(rowNum2));
    }

    HSSFRow(HSSFWorkbook book2, HSSFSheet sheet2, RowRecord record) {
        this.cells = new HSSFCell[5];
        this.book = book2;
        this.sheet = sheet2;
        this.row = record;
        setRowNum(record.getRowNumber());
        record.setEmpty();
    }

    public HSSFCell createCell(short columnIndex) {
        return createCell((int) columnIndex);
    }

    public HSSFCell createCell(short columnIndex, int type) {
        return createCell((int) columnIndex, type);
    }

    public HSSFCell createCell(int column) {
        return createCell(column, 3);
    }

    public HSSFCell createCell(int columnIndex, int type) {
        short shortCellNum = (short) columnIndex;
        if (columnIndex > 32767) {
            shortCellNum = (short) (65535 - columnIndex);
        }
        HSSFCell cell = new HSSFCell(this.book, this.sheet, getRowNum(), shortCellNum, type);
        addCell(cell);
        this.sheet.getSheet().addValueRecord(getRowNum(), cell.getCellValueRecord());
        return cell;
    }

    public void removeCell(Cell cell) {
        if (cell != null) {
            removeCell((HSSFCell) cell, true);
            return;
        }
        throw new IllegalArgumentException("cell must not be null");
    }

    private void removeCell(HSSFCell cell, boolean alsoRemoveRecords) {
        int column = cell.getColumnIndex();
        if (column >= 0) {
            HSSFCell[] hSSFCellArr = this.cells;
            if (column >= hSSFCellArr.length || cell != hSSFCellArr[column]) {
                throw new RuntimeException("Specified cell is not from this row");
            }
            if (cell.isPartOfArrayFormulaGroup()) {
                cell.notifyArrayFormulaChanging();
            }
            this.cells[column] = null;
            if (alsoRemoveRecords) {
                this.sheet.getSheet().removeValueRecord(getRowNum(), cell.getCellValueRecord());
            }
            if (cell.getColumnIndex() + 1 == this.row.getLastCol()) {
                RowRecord rowRecord = this.row;
                rowRecord.setLastCol(calculateNewLastCellPlusOne(rowRecord.getLastCol()));
            }
            if (cell.getColumnIndex() == this.row.getFirstCol()) {
                RowRecord rowRecord2 = this.row;
                rowRecord2.setFirstCol(calculateNewFirstCell(rowRecord2.getFirstCol()));
                return;
            }
            return;
        }
        throw new RuntimeException("Negative cell indexes not allowed");
    }

    /* access modifiers changed from: protected */
    public void removeAllCells() {
        int i = 0;
        while (true) {
            HSSFCell[] hSSFCellArr = this.cells;
            if (i < hSSFCellArr.length) {
                if (hSSFCellArr[i] != null) {
                    removeCell(hSSFCellArr[i], true);
                }
                i++;
            } else {
                this.cells = new HSSFCell[5];
                return;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public HSSFCell createCellFromRecord(CellValueRecordInterface cell) {
        HSSFCell hcell = new HSSFCell(this.book, this.sheet, cell);
        addCell(hcell);
        int colIx = cell.getColumn();
        if (this.row.isEmpty()) {
            this.row.setFirstCol(colIx);
            this.row.setLastCol(colIx + 1);
        } else if (colIx < this.row.getFirstCol()) {
            this.row.setFirstCol(colIx);
        } else if (colIx > this.row.getLastCol()) {
            this.row.setLastCol(colIx + 1);
        }
        return hcell;
    }

    public void setRowNum(int rowIndex) {
        int maxrow = SpreadsheetVersion.EXCEL97.getLastRowIndex();
        if (rowIndex < 0 || rowIndex > maxrow) {
            throw new IllegalArgumentException("Invalid row number (" + rowIndex + ") outside allowable range (0.." + maxrow + ")");
        }
        this.rowNum = rowIndex;
        RowRecord rowRecord = this.row;
        if (rowRecord != null) {
            rowRecord.setRowNumber(rowIndex);
        }
    }

    public int getRowNum() {
        return this.rowNum;
    }

    public HSSFSheet getSheet() {
        return this.sheet;
    }

    /* access modifiers changed from: protected */
    public int getOutlineLevel() {
        return this.row.getOutlineLevel();
    }

    public void moveCell(HSSFCell cell, short newColumn) {
        HSSFCell[] hSSFCellArr = this.cells;
        if (hSSFCellArr.length > newColumn && hSSFCellArr[newColumn] != null) {
            throw new IllegalArgumentException("Asked to move cell to column " + newColumn + " but there's already a cell there");
        } else if (hSSFCellArr[cell.getColumnIndex()].equals(cell)) {
            removeCell(cell, false);
            cell.updateCellNum(newColumn);
            addCell(cell);
        } else {
            throw new IllegalArgumentException("Asked to move a cell, but it didn't belong to our row");
        }
    }

    private void addCell(HSSFCell cell) {
        int column = cell.getColumnIndex();
        if (column >= this.cells.length) {
            HSSFCell[] oldCells = this.cells;
            int newSize = oldCells.length * 2;
            if (newSize < column + 1) {
                newSize = column + 1;
            }
            HSSFCell[] hSSFCellArr = new HSSFCell[newSize];
            this.cells = hSSFCellArr;
            System.arraycopy(oldCells, 0, hSSFCellArr, 0, oldCells.length);
        }
        this.cells[column] = cell;
        if (this.row.isEmpty() || column < this.row.getFirstCol()) {
            this.row.setFirstCol((short) column);
        }
        if (this.row.isEmpty() || column >= this.row.getLastCol()) {
            this.row.setLastCol((short) (column + 1));
        }
    }

    private HSSFCell retrieveCell(int cellIndex) {
        if (cellIndex < 0) {
            return null;
        }
        HSSFCell[] hSSFCellArr = this.cells;
        if (cellIndex >= hSSFCellArr.length) {
            return null;
        }
        return hSSFCellArr[cellIndex];
    }

    public HSSFCell getCell(short cellnum) {
        return getCell(65535 & cellnum);
    }

    public HSSFCell getCell(int cellnum) {
        return getCell(cellnum, this.book.getMissingCellPolicy());
    }

    public HSSFCell getCell(int cellnum, Row.MissingCellPolicy policy) {
        HSSFCell cell = retrieveCell(cellnum);
        if (policy == RETURN_NULL_AND_BLANK) {
            return cell;
        }
        if (policy == RETURN_BLANK_AS_NULL) {
            if (cell != null && cell.getCellType() == 3) {
                return null;
            }
            return cell;
        } else if (policy != CREATE_NULL_AS_BLANK) {
            throw new IllegalArgumentException("Illegal policy " + policy + " (" + policy.id + ")");
        } else if (cell == null) {
            return createCell(cellnum, 3);
        } else {
            return cell;
        }
    }

    public short getFirstCellNum() {
        if (this.row.isEmpty()) {
            return -1;
        }
        return (short) this.row.getFirstCol();
    }

    public short getLastCellNum() {
        if (this.row.isEmpty()) {
            return -1;
        }
        return (short) this.row.getLastCol();
    }

    public int getPhysicalNumberOfCells() {
        int count = 0;
        int i = 0;
        while (true) {
            HSSFCell[] hSSFCellArr = this.cells;
            if (i >= hSSFCellArr.length) {
                return count;
            }
            if (hSSFCellArr[i] != null) {
                count++;
            }
            i++;
        }
    }

    public void setHeight(short height) {
        if (height == -1) {
            this.row.setHeight(-32513);
            return;
        }
        this.row.setBadFontHeight(true);
        this.row.setHeight(height);
    }

    public void setZeroHeight(boolean zHeight) {
        this.row.setZeroHeight(zHeight);
    }

    public boolean getZeroHeight() {
        return this.row.getZeroHeight();
    }

    public void setHeightInPoints(float height) {
        if (height == -1.0f) {
            this.row.setHeight(-32513);
            return;
        }
        this.row.setBadFontHeight(true);
        this.row.setHeight((short) ((int) (20.0f * height)));
    }

    public short getHeight() {
        short height = this.row.getHeight();
        if ((32768 & height) != 0) {
            return this.sheet.getSheet().getDefaultRowHeight();
        }
        return (short) (height & Font.COLOR_NORMAL);
    }

    public float getHeightInPoints() {
        return ((float) getHeight()) / 20.0f;
    }

    /* access modifiers changed from: protected */
    public RowRecord getRowRecord() {
        return this.row;
    }

    private int calculateNewLastCellPlusOne(int lastcell) {
        int cellIx = lastcell - 1;
        HSSFCell r = retrieveCell(cellIx);
        while (r == null) {
            if (cellIx < 0) {
                return 0;
            }
            cellIx--;
            r = retrieveCell(cellIx);
        }
        return cellIx + 1;
    }

    private int calculateNewFirstCell(int firstcell) {
        int cellIx = firstcell + 1;
        HSSFCell r = retrieveCell(cellIx);
        while (r == null) {
            if (cellIx <= this.cells.length) {
                return 0;
            }
            cellIx++;
            r = retrieveCell(cellIx);
        }
        return cellIx;
    }

    public boolean isFormatted() {
        return this.row.getFormatted();
    }

    public HSSFCellStyle getRowStyle() {
        if (!isFormatted()) {
            return null;
        }
        short styleIndex = this.row.getXFIndex();
        return new HSSFCellStyle(styleIndex, this.book.getWorkbook().getExFormatAt(styleIndex), this.book);
    }

    public void setRowStyle(HSSFCellStyle style) {
        this.row.setFormatted(true);
        this.row.setXFIndex(style.getIndex());
    }

    public Iterator<Cell> cellIterator() {
        return new CellIterator();
    }

    public Iterator iterator() {
        return cellIterator();
    }

    private class CellIterator implements Iterator<Cell> {
        int nextId = -1;
        int thisId = -1;

        public CellIterator() {
            findNext();
        }

        public boolean hasNext() {
            return this.nextId < HSSFRow.this.cells.length;
        }

        public Cell next() {
            if (hasNext()) {
                HSSFCell[] access$000 = HSSFRow.this.cells;
                int i = this.nextId;
                HSSFCell cell = access$000[i];
                this.thisId = i;
                findNext();
                return cell;
            }
            throw new NoSuchElementException("At last element");
        }

        public void remove() {
            if (this.thisId != -1) {
                HSSFRow.this.cells[this.thisId] = null;
                return;
            }
            throw new IllegalStateException("remove() called before next()");
        }

        private void findNext() {
            int i = this.nextId;
            while (true) {
                i++;
                if (i >= HSSFRow.this.cells.length || HSSFRow.this.cells[i] != null) {
                    this.nextId = i;
                }
            }
            this.nextId = i;
        }
    }

    public int compareTo(Object obj) {
        HSSFRow loc = (HSSFRow) obj;
        if (getRowNum() == loc.getRowNum()) {
            return 0;
        }
        if (getRowNum() >= loc.getRowNum() && getRowNum() > loc.getRowNum()) {
            return 1;
        }
        return -1;
    }

    public boolean equals(Object obj) {
        if ((obj instanceof HSSFRow) && getRowNum() == ((HSSFRow) obj).getRowNum()) {
            return true;
        }
        return false;
    }
}
