package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.StringUtil;

public final class NameCommentRecord extends StandardRecord {
    public static final short sid = 2196;
    private final short field_1_record_type;
    private final short field_2_frt_cell_ref_flag;
    private final long field_3_reserved;
    private String field_6_name_text;
    private String field_7_comment_text;

    public NameCommentRecord(String name, String comment) {
        this.field_1_record_type = 0;
        this.field_2_frt_cell_ref_flag = 0;
        this.field_3_reserved = 0;
        this.field_6_name_text = name;
        this.field_7_comment_text = comment;
    }

    public void serialize(LittleEndianOutput out) {
        int field_4_name_length = this.field_6_name_text.length();
        int field_5_comment_length = this.field_7_comment_text.length();
        out.writeShort(this.field_1_record_type);
        out.writeShort(this.field_2_frt_cell_ref_flag);
        out.writeLong(this.field_3_reserved);
        out.writeShort(field_4_name_length);
        out.writeShort(field_5_comment_length);
        out.writeByte(0);
        out.write(this.field_6_name_text.getBytes());
        out.writeByte(0);
        out.write(this.field_7_comment_text.getBytes());
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return this.field_6_name_text.length() + 18 + this.field_7_comment_text.length();
    }

    public NameCommentRecord(RecordInputStream ris) {
        LittleEndianInput in = ris;
        this.field_1_record_type = in.readShort();
        this.field_2_frt_cell_ref_flag = in.readShort();
        this.field_3_reserved = in.readLong();
        int field_4_name_length = in.readShort();
        int field_5_comment_length = in.readShort();
        in.readByte();
        this.field_6_name_text = StringUtil.readCompressedUnicode(in, field_4_name_length);
        in.readByte();
        this.field_7_comment_text = StringUtil.readCompressedUnicode(in, field_5_comment_length);
    }

    public short getSid() {
        return sid;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[NAMECMT]\n");
        sb.append("    .record type            = ");
        sb.append(HexDump.shortToHex(this.field_1_record_type));
        sb.append("\n");
        sb.append("    .frt cell ref flag      = ");
        sb.append(HexDump.byteToHex(this.field_2_frt_cell_ref_flag));
        sb.append("\n");
        sb.append("    .reserved               = ");
        sb.append(this.field_3_reserved);
        sb.append("\n");
        sb.append("    .name length            = ");
        sb.append(this.field_6_name_text.length());
        sb.append("\n");
        sb.append("    .comment length         = ");
        sb.append(this.field_7_comment_text.length());
        sb.append("\n");
        sb.append("    .name                   = ");
        sb.append(this.field_6_name_text);
        sb.append("\n");
        sb.append("    .comment                = ");
        sb.append(this.field_7_comment_text);
        sb.append("\n");
        sb.append("[/NAMECMT]\n");
        return sb.toString();
    }

    public String getNameText() {
        return this.field_6_name_text;
    }

    public void setNameText(String newName) {
        this.field_6_name_text = newName;
    }

    public String getCommentText() {
        return this.field_7_comment_text;
    }

    public void setCommentText(String comment) {
        this.field_7_comment_text = comment;
    }

    public short getRecordType() {
        return this.field_1_record_type;
    }
}
