package org.apache.poi.hssf.eventusermodel.dummyrecord;

public final class LastCellOfRowDummyRecord extends DummyRecordBase {
    private int lastColumnNumber;
    private int row;

    public /* bridge */ /* synthetic */ int serialize(int x0, byte[] x1) {
        return super.serialize(x0, x1);
    }

    public LastCellOfRowDummyRecord(int row2, int lastColumnNumber2) {
        this.row = row2;
        this.lastColumnNumber = lastColumnNumber2;
    }

    public int getRow() {
        return this.row;
    }

    public int getLastColumnNumber() {
        return this.lastColumnNumber;
    }
}
