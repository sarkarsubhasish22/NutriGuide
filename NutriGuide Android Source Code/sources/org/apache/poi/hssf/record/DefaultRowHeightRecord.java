package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndianOutput;

public final class DefaultRowHeightRecord extends StandardRecord {
    public static final short sid = 549;
    private short field_1_option_flags;
    private short field_2_row_height;

    public DefaultRowHeightRecord() {
    }

    public DefaultRowHeightRecord(RecordInputStream in) {
        this.field_1_option_flags = in.readShort();
        this.field_2_row_height = in.readShort();
    }

    public void setOptionFlags(short flags) {
        this.field_1_option_flags = flags;
    }

    public void setRowHeight(short height) {
        this.field_2_row_height = height;
    }

    public short getOptionFlags() {
        return this.field_1_option_flags;
    }

    public short getRowHeight() {
        return this.field_2_row_height;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[DEFAULTROWHEIGHT]\n");
        buffer.append("    .optionflags    = ");
        buffer.append(Integer.toHexString(getOptionFlags()));
        buffer.append("\n");
        buffer.append("    .rowheight      = ");
        buffer.append(Integer.toHexString(getRowHeight()));
        buffer.append("\n");
        buffer.append("[/DEFAULTROWHEIGHT]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(getOptionFlags());
        out.writeShort(getRowHeight());
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 4;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        DefaultRowHeightRecord rec = new DefaultRowHeightRecord();
        rec.field_1_option_flags = this.field_1_option_flags;
        rec.field_2_row_height = this.field_2_row_height;
        return rec;
    }
}
