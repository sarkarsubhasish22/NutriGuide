package org.apache.poi.hssf.eventusermodel.dummyrecord;

public final class MissingCellDummyRecord extends DummyRecordBase {
    private int column;
    private int row;

    public /* bridge */ /* synthetic */ int serialize(int x0, byte[] x1) {
        return super.serialize(x0, x1);
    }

    public MissingCellDummyRecord(int row2, int column2) {
        this.row = row2;
        this.column = column2;
    }

    public int getRow() {
        return this.row;
    }

    public int getColumn() {
        return this.column;
    }
}
