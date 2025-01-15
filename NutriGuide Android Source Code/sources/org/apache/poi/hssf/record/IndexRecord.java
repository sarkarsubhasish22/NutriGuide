package org.apache.poi.hssf.record;

import org.apache.poi.util.IntList;
import org.apache.poi.util.LittleEndianOutput;

public class IndexRecord extends StandardRecord {
    public static final short sid = 523;
    private int field_2_first_row;
    private int field_3_last_row_add1;
    private int field_4_zero;
    private IntList field_5_dbcells;

    public IndexRecord() {
    }

    public IndexRecord(RecordInputStream in) {
        int field_1_zero = in.readInt();
        if (field_1_zero == 0) {
            this.field_2_first_row = in.readInt();
            this.field_3_last_row_add1 = in.readInt();
            this.field_4_zero = in.readInt();
            int nCells = in.remaining() / 4;
            this.field_5_dbcells = new IntList(nCells);
            for (int i = 0; i < nCells; i++) {
                this.field_5_dbcells.add(in.readInt());
            }
            return;
        }
        throw new RecordFormatException("Expected zero for field 1 but got " + field_1_zero);
    }

    public void setFirstRow(int row) {
        this.field_2_first_row = row;
    }

    public void setLastRowAdd1(int row) {
        this.field_3_last_row_add1 = row;
    }

    public void addDbcell(int cell) {
        if (this.field_5_dbcells == null) {
            this.field_5_dbcells = new IntList();
        }
        this.field_5_dbcells.add(cell);
    }

    public void setDbcell(int cell, int value) {
        this.field_5_dbcells.set(cell, value);
    }

    public int getFirstRow() {
        return this.field_2_first_row;
    }

    public int getLastRowAdd1() {
        return this.field_3_last_row_add1;
    }

    public int getNumDbcells() {
        IntList intList = this.field_5_dbcells;
        if (intList == null) {
            return 0;
        }
        return intList.size();
    }

    public int getDbcellAt(int cellnum) {
        return this.field_5_dbcells.get(cellnum);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[INDEX]\n");
        buffer.append("    .firstrow       = ");
        buffer.append(Integer.toHexString(getFirstRow()));
        buffer.append("\n");
        buffer.append("    .lastrowadd1    = ");
        buffer.append(Integer.toHexString(getLastRowAdd1()));
        buffer.append("\n");
        for (int k = 0; k < getNumDbcells(); k++) {
            buffer.append("    .dbcell_");
            buffer.append(k);
            buffer.append(" = ");
            buffer.append(Integer.toHexString(getDbcellAt(k)));
            buffer.append("\n");
        }
        buffer.append("[/INDEX]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeInt(0);
        out.writeInt(getFirstRow());
        out.writeInt(getLastRowAdd1());
        out.writeInt(this.field_4_zero);
        for (int k = 0; k < getNumDbcells(); k++) {
            out.writeInt(getDbcellAt(k));
        }
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return (getNumDbcells() * 4) + 16;
    }

    public static int getRecordSizeForBlockCount(int blockCount) {
        return (blockCount * 4) + 20;
    }

    public short getSid() {
        return 523;
    }

    public Object clone() {
        IndexRecord rec = new IndexRecord();
        rec.field_2_first_row = this.field_2_first_row;
        rec.field_3_last_row_add1 = this.field_3_last_row_add1;
        rec.field_4_zero = this.field_4_zero;
        IntList intList = new IntList();
        rec.field_5_dbcells = intList;
        intList.addAll(this.field_5_dbcells);
        return rec;
    }
}
