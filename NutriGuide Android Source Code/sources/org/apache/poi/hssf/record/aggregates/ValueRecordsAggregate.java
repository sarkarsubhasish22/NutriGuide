package org.apache.poi.hssf.record.aggregates;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.model.RecordStream;
import org.apache.poi.hssf.record.BlankRecord;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.MulBlankRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordBase;
import org.apache.poi.hssf.record.StringRecord;
import org.apache.poi.hssf.record.aggregates.RecordAggregate;
import org.apache.poi.hssf.record.formula.FormulaShifter;
import org.apache.poi.hssf.record.formula.Ptg;

public final class ValueRecordsAggregate {
    private static final int INDEX_NOT_SET = -1;
    private static final int MAX_ROW_INDEX = 65535;
    private int firstcell;
    private int lastcell;
    private CellValueRecordInterface[][] records;

    public ValueRecordsAggregate() {
        this(-1, -1, new CellValueRecordInterface[30][]);
    }

    private ValueRecordsAggregate(int firstCellIx, int lastCellIx, CellValueRecordInterface[][] pRecords) {
        this.firstcell = -1;
        this.lastcell = -1;
        this.firstcell = firstCellIx;
        this.lastcell = lastCellIx;
        this.records = pRecords;
    }

    public void insertCell(CellValueRecordInterface cell) {
        short column = cell.getColumn();
        int row = cell.getRow();
        if (row >= this.records.length) {
            CellValueRecordInterface[][] oldRecords = this.records;
            int newSize = oldRecords.length * 2;
            if (newSize < row + 1) {
                newSize = row + 1;
            }
            CellValueRecordInterface[][] cellValueRecordInterfaceArr = new CellValueRecordInterface[newSize][];
            this.records = cellValueRecordInterfaceArr;
            System.arraycopy(oldRecords, 0, cellValueRecordInterfaceArr, 0, oldRecords.length);
        }
        CellValueRecordInterface[][] oldRecords2 = this.records;
        CellValueRecordInterface[] rowCells = oldRecords2[row];
        if (rowCells == null) {
            int newSize2 = column + 1;
            if (newSize2 < 10) {
                newSize2 = 10;
            }
            rowCells = new CellValueRecordInterface[newSize2];
            oldRecords2[row] = rowCells;
        }
        if (column >= rowCells.length) {
            CellValueRecordInterface[] oldRowCells = rowCells;
            int newSize3 = oldRowCells.length * 2;
            if (newSize3 < column + 1) {
                newSize3 = column + 1;
            }
            rowCells = new CellValueRecordInterface[newSize3];
            System.arraycopy(oldRowCells, 0, rowCells, 0, oldRowCells.length);
            this.records[row] = rowCells;
        }
        rowCells[column] = cell;
        int i = this.firstcell;
        if (column < i || i == -1) {
            this.firstcell = column;
        }
        int i2 = this.lastcell;
        if (column > i2 || i2 == -1) {
            this.lastcell = column;
        }
    }

    public void removeCell(CellValueRecordInterface cell) {
        if (cell != null) {
            int row = cell.getRow();
            CellValueRecordInterface[][] cellValueRecordInterfaceArr = this.records;
            if (row < cellValueRecordInterfaceArr.length) {
                CellValueRecordInterface[] rowCells = cellValueRecordInterfaceArr[row];
                if (rowCells != null) {
                    short column = cell.getColumn();
                    if (column < rowCells.length) {
                        rowCells[column] = null;
                        return;
                    }
                    throw new RuntimeException("cell column is out of range");
                }
                throw new RuntimeException("cell row is already empty");
            }
            throw new RuntimeException("cell row is out of range");
        }
        throw new IllegalArgumentException("cell must not be null");
    }

    public void removeAllCellsValuesForRow(int rowIndex) {
        if (rowIndex < 0 || rowIndex > 65535) {
            throw new IllegalArgumentException("Specified rowIndex " + rowIndex + " is outside the allowable range (0.." + 65535 + ")");
        }
        CellValueRecordInterface[][] cellValueRecordInterfaceArr = this.records;
        if (rowIndex < cellValueRecordInterfaceArr.length) {
            cellValueRecordInterfaceArr[rowIndex] = null;
        }
    }

    public int getPhysicalNumberOfCells() {
        int count = 0;
        int r = 0;
        while (true) {
            CellValueRecordInterface[][] cellValueRecordInterfaceArr = this.records;
            if (r >= cellValueRecordInterfaceArr.length) {
                return count;
            }
            CellValueRecordInterface[] rowCells = cellValueRecordInterfaceArr[r];
            if (rowCells != null) {
                for (CellValueRecordInterface cellValueRecordInterface : rowCells) {
                    if (cellValueRecordInterface != null) {
                        count++;
                    }
                }
            }
            r++;
        }
    }

    public int getFirstCellNum() {
        return this.firstcell;
    }

    public int getLastCellNum() {
        return this.lastcell;
    }

    public void addMultipleBlanks(MulBlankRecord mbr) {
        for (int j = 0; j < mbr.getNumColumns(); j++) {
            BlankRecord br = new BlankRecord();
            br.setColumn((short) (mbr.getFirstColumn() + j));
            br.setRow(mbr.getRow());
            br.setXFIndex(mbr.getXFAt(j));
            insertCell(br);
        }
    }

    public void construct(CellValueRecordInterface rec, RecordStream rs, SharedValueManager sfh) {
        StringRecord cachedText;
        if (rec instanceof FormulaRecord) {
            FormulaRecord formulaRec = (FormulaRecord) rec;
            if (rs.peekNextClass() == StringRecord.class) {
                cachedText = (StringRecord) rs.getNext();
            } else {
                cachedText = null;
            }
            insertCell(new FormulaRecordAggregate(formulaRec, cachedText, sfh));
            return;
        }
        insertCell(rec);
    }

    public int getRowCellBlockSize(int startRow, int endRow) {
        int result = 0;
        for (int rowIx = startRow; rowIx <= endRow; rowIx++) {
            CellValueRecordInterface[][] cellValueRecordInterfaceArr = this.records;
            if (rowIx >= cellValueRecordInterfaceArr.length) {
                break;
            }
            result += getRowSerializedSize(cellValueRecordInterfaceArr[rowIx]);
        }
        return result;
    }

    public boolean rowHasCells(int row) {
        CellValueRecordInterface[] rowCells;
        CellValueRecordInterface[][] cellValueRecordInterfaceArr = this.records;
        if (row >= cellValueRecordInterfaceArr.length || (rowCells = cellValueRecordInterfaceArr[row]) == null) {
            return false;
        }
        for (CellValueRecordInterface cellValueRecordInterface : rowCells) {
            if (cellValueRecordInterface != null) {
                return true;
            }
        }
        return false;
    }

    private static int getRowSerializedSize(CellValueRecordInterface[] rowCells) {
        if (rowCells == null) {
            return 0;
        }
        int result = 0;
        int i = 0;
        while (i < rowCells.length) {
            RecordBase cvr = (RecordBase) rowCells[i];
            if (cvr != null) {
                int nBlank = countBlanks(rowCells, i);
                if (nBlank > 1) {
                    result += (nBlank * 2) + 10;
                    i += nBlank - 1;
                } else {
                    result += cvr.getRecordSize();
                }
            }
            i++;
        }
        return result;
    }

    public void visitCellsForRow(int rowIndex, RecordAggregate.RecordVisitor rv) {
        CellValueRecordInterface[] rowCells = this.records[rowIndex];
        if (rowCells != null) {
            int i = 0;
            while (i < rowCells.length) {
                RecordBase cvr = (RecordBase) rowCells[i];
                if (cvr != null) {
                    int nBlank = countBlanks(rowCells, i);
                    if (nBlank > 1) {
                        rv.visitRecord(createMBR(rowCells, i, nBlank));
                        i += nBlank - 1;
                    } else if (cvr instanceof RecordAggregate) {
                        ((RecordAggregate) cvr).visitContainedRecords(rv);
                    } else {
                        rv.visitRecord((Record) cvr);
                    }
                }
                i++;
            }
            return;
        }
        throw new IllegalArgumentException("Row [" + rowIndex + "] is empty");
    }

    private static int countBlanks(CellValueRecordInterface[] rowCellValues, int startIx) {
        int i = startIx;
        while (i < rowCellValues.length && (rowCellValues[i] instanceof BlankRecord)) {
            i++;
        }
        return i - startIx;
    }

    private MulBlankRecord createMBR(CellValueRecordInterface[] cellValues, int startIx, int nBlank) {
        short[] xfs = new short[nBlank];
        for (int i = 0; i < xfs.length; i++) {
            xfs[i] = cellValues[startIx + i].getXFIndex();
        }
        return new MulBlankRecord(cellValues[startIx].getRow(), startIx, xfs);
    }

    public void updateFormulasAfterRowShift(FormulaShifter shifter, int currentExternSheetIndex) {
        int i = 0;
        while (true) {
            CellValueRecordInterface[][] cellValueRecordInterfaceArr = this.records;
            if (i < cellValueRecordInterfaceArr.length) {
                CellValueRecordInterface[] rowCells = cellValueRecordInterfaceArr[i];
                if (rowCells != null) {
                    for (CellValueRecordInterface cell : rowCells) {
                        if (cell instanceof FormulaRecordAggregate) {
                            FormulaRecord fr = ((FormulaRecordAggregate) cell).getFormulaRecord();
                            Ptg[] ptgs = fr.getParsedExpression();
                            if (shifter.adjustFormula(ptgs, currentExternSheetIndex)) {
                                fr.setParsedExpression(ptgs);
                            }
                        }
                    }
                }
                i++;
            } else {
                return;
            }
        }
    }

    public CellValueRecordInterface[] getValueRecords() {
        List<CellValueRecordInterface> temp = new ArrayList<>();
        int rowIx = 0;
        while (true) {
            CellValueRecordInterface[][] cellValueRecordInterfaceArr = this.records;
            if (rowIx < cellValueRecordInterfaceArr.length) {
                CellValueRecordInterface[] rowCells = cellValueRecordInterfaceArr[rowIx];
                if (rowCells != null) {
                    for (CellValueRecordInterface cell : rowCells) {
                        if (cell != null) {
                            temp.add(cell);
                        }
                    }
                }
                rowIx++;
            } else {
                CellValueRecordInterface[] result = new CellValueRecordInterface[temp.size()];
                temp.toArray(result);
                return result;
            }
        }
    }

    public Object clone() {
        throw new RuntimeException("clone() should not be called.  ValueRecordsAggregate should be copied via Sheet.cloneSheet()");
    }
}
