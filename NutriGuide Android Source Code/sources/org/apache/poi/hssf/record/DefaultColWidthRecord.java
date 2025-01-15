package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndianOutput;

public final class DefaultColWidthRecord extends StandardRecord {
    public static final short sid = 85;
    private int field_1_col_width;

    public DefaultColWidthRecord() {
    }

    public DefaultColWidthRecord(RecordInputStream in) {
        this.field_1_col_width = in.readUShort();
    }

    public void setColWidth(int width) {
        this.field_1_col_width = width;
    }

    public int getColWidth() {
        return this.field_1_col_width;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[DEFAULTCOLWIDTH]\n");
        buffer.append("    .colwidth      = ");
        buffer.append(Integer.toHexString(getColWidth()));
        buffer.append("\n");
        buffer.append("[/DEFAULTCOLWIDTH]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(getColWidth());
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 2;
    }

    public short getSid() {
        return 85;
    }

    public Object clone() {
        DefaultColWidthRecord rec = new DefaultColWidthRecord();
        rec.field_1_col_width = this.field_1_col_width;
        return rec;
    }
}
