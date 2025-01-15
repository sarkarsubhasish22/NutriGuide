package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.StringUtil;

public final class FormatRecord extends StandardRecord {
    public static final short sid = 1054;
    private final int field_1_index_code;
    private final boolean field_3_hasMultibyte;
    private final String field_4_formatstring;

    public FormatRecord(int indexCode, String fs) {
        this.field_1_index_code = indexCode;
        this.field_4_formatstring = fs;
        this.field_3_hasMultibyte = StringUtil.hasMultibyte(fs);
    }

    public FormatRecord(RecordInputStream in) {
        this.field_1_index_code = in.readShort();
        int field_3_unicode_len = in.readUShort();
        boolean z = (in.readByte() & 1) == 0 ? false : true;
        this.field_3_hasMultibyte = z;
        if (z) {
            this.field_4_formatstring = in.readUnicodeLEString(field_3_unicode_len);
        } else {
            this.field_4_formatstring = in.readCompressedUnicode(field_3_unicode_len);
        }
    }

    public int getIndexCode() {
        return this.field_1_index_code;
    }

    public String getFormatString() {
        return this.field_4_formatstring;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[FORMAT]\n");
        buffer.append("    .indexcode       = ");
        buffer.append(HexDump.shortToHex(getIndexCode()));
        buffer.append("\n");
        buffer.append("    .isUnicode       = ");
        buffer.append(this.field_3_hasMultibyte);
        buffer.append("\n");
        buffer.append("    .formatstring    = ");
        buffer.append(getFormatString());
        buffer.append("\n");
        buffer.append("[/FORMAT]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        String formatString = getFormatString();
        out.writeShort(getIndexCode());
        out.writeShort(formatString.length());
        out.writeByte(this.field_3_hasMultibyte ? 1 : 0);
        if (this.field_3_hasMultibyte) {
            StringUtil.putUnicodeLE(formatString, out);
        } else {
            StringUtil.putCompressedUnicode(formatString, out);
        }
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return (getFormatString().length() * (this.field_3_hasMultibyte ? 2 : 1)) + 5;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        return this;
    }
}
