package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;

public final class LabelRecord extends Record implements CellValueRecordInterface {
    public static final short sid = 516;
    private int field_1_row;
    private short field_2_column;
    private short field_3_xf_index;
    private short field_4_string_len;
    private byte field_5_unicode_flag;
    private String field_6_value;

    public LabelRecord() {
    }

    public LabelRecord(RecordInputStream in) {
        this.field_1_row = in.readUShort();
        this.field_2_column = in.readShort();
        this.field_3_xf_index = in.readShort();
        this.field_4_string_len = in.readShort();
        this.field_5_unicode_flag = in.readByte();
        if (this.field_4_string_len <= 0) {
            this.field_6_value = "";
        } else if (isUnCompressedUnicode()) {
            this.field_6_value = in.readUnicodeLEString(this.field_4_string_len);
        } else {
            this.field_6_value = in.readCompressedUnicode(this.field_4_string_len);
        }
    }

    public int getRow() {
        return this.field_1_row;
    }

    public short getColumn() {
        return this.field_2_column;
    }

    public short getXFIndex() {
        return this.field_3_xf_index;
    }

    public short getStringLength() {
        return this.field_4_string_len;
    }

    public boolean isUnCompressedUnicode() {
        return this.field_5_unicode_flag == 1;
    }

    public String getValue() {
        return this.field_6_value;
    }

    public int serialize(int offset, byte[] data) {
        throw new RecordFormatException("Label Records are supported READ ONLY...convert to LabelSST");
    }

    public int getRecordSize() {
        throw new RecordFormatException("Label Records are supported READ ONLY...convert to LabelSST");
    }

    public short getSid() {
        return 516;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[LABEL]\n");
        sb.append("    .row       = ");
        sb.append(HexDump.shortToHex(getRow()));
        sb.append("\n");
        sb.append("    .column    = ");
        sb.append(HexDump.shortToHex(getColumn()));
        sb.append("\n");
        sb.append("    .xfindex   = ");
        sb.append(HexDump.shortToHex(getXFIndex()));
        sb.append("\n");
        sb.append("    .string_len= ");
        sb.append(HexDump.shortToHex(this.field_4_string_len));
        sb.append("\n");
        sb.append("    .unicode_flag= ");
        sb.append(HexDump.byteToHex(this.field_5_unicode_flag));
        sb.append("\n");
        sb.append("    .value       = ");
        sb.append(getValue());
        sb.append("\n");
        sb.append("[/LABEL]\n");
        return sb.toString();
    }

    public void setColumn(short col) {
    }

    public void setRow(int row) {
    }

    public void setXFIndex(short xf) {
    }

    public Object clone() {
        LabelRecord rec = new LabelRecord();
        rec.field_1_row = this.field_1_row;
        rec.field_2_column = this.field_2_column;
        rec.field_3_xf_index = this.field_3_xf_index;
        rec.field_4_string_len = this.field_4_string_len;
        rec.field_5_unicode_flag = this.field_5_unicode_flag;
        rec.field_6_value = this.field_6_value;
        return rec;
    }
}
