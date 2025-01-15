package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndianOutput;

public final class DimensionsRecord extends StandardRecord {
    public static final short sid = 512;
    private int field_1_first_row;
    private int field_2_last_row;
    private short field_3_first_col;
    private short field_4_last_col;
    private short field_5_zero;

    public DimensionsRecord() {
    }

    public DimensionsRecord(RecordInputStream in) {
        this.field_1_first_row = in.readInt();
        this.field_2_last_row = in.readInt();
        this.field_3_first_col = in.readShort();
        this.field_4_last_col = in.readShort();
        this.field_5_zero = in.readShort();
    }

    public void setFirstRow(int row) {
        this.field_1_first_row = row;
    }

    public void setLastRow(int row) {
        this.field_2_last_row = row;
    }

    public void setFirstCol(short col) {
        this.field_3_first_col = col;
    }

    public void setLastCol(short col) {
        this.field_4_last_col = col;
    }

    public int getFirstRow() {
        return this.field_1_first_row;
    }

    public int getLastRow() {
        return this.field_2_last_row;
    }

    public short getFirstCol() {
        return this.field_3_first_col;
    }

    public short getLastCol() {
        return this.field_4_last_col;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[DIMENSIONS]\n");
        buffer.append("    .firstrow       = ");
        buffer.append(Integer.toHexString(getFirstRow()));
        buffer.append("\n");
        buffer.append("    .lastrow        = ");
        buffer.append(Integer.toHexString(getLastRow()));
        buffer.append("\n");
        buffer.append("    .firstcol       = ");
        buffer.append(Integer.toHexString(getFirstCol()));
        buffer.append("\n");
        buffer.append("    .lastcol        = ");
        buffer.append(Integer.toHexString(getLastCol()));
        buffer.append("\n");
        buffer.append("    .zero           = ");
        buffer.append(Integer.toHexString(this.field_5_zero));
        buffer.append("\n");
        buffer.append("[/DIMENSIONS]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeInt(getFirstRow());
        out.writeInt(getLastRow());
        out.writeShort(getFirstCol());
        out.writeShort(getLastCol());
        out.writeShort(0);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 14;
    }

    public short getSid() {
        return 512;
    }

    public Object clone() {
        DimensionsRecord rec = new DimensionsRecord();
        rec.field_1_first_row = this.field_1_first_row;
        rec.field_2_last_row = this.field_2_last_row;
        rec.field_3_first_col = this.field_3_first_col;
        rec.field_4_last_col = this.field_4_last_col;
        rec.field_5_zero = this.field_5_zero;
        return rec;
    }
}
