package org.apache.poi.hssf.eventusermodel.dummyrecord;

public final class MissingRowDummyRecord extends DummyRecordBase {
    private int rowNumber;

    public /* bridge */ /* synthetic */ int serialize(int x0, byte[] x1) {
        return super.serialize(x0, x1);
    }

    public MissingRowDummyRecord(int rowNumber2) {
        this.rowNumber = rowNumber2;
    }

    public int getRowNumber() {
        return this.rowNumber;
    }
}
