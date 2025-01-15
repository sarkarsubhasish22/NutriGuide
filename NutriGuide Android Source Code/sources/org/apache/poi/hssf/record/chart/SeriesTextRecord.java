package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.StringUtil;

public final class SeriesTextRecord extends StandardRecord {
    private static final int MAX_LEN = 255;
    public static final short sid = 4109;
    private int field_1_id;
    private String field_4_text;
    private boolean is16bit;

    public SeriesTextRecord() {
        this.field_4_text = "";
        this.is16bit = false;
    }

    public SeriesTextRecord(RecordInputStream in) {
        this.field_1_id = in.readUShort();
        int field_2_textLength = in.readUByte();
        boolean z = (in.readUByte() & 1) == 0 ? false : true;
        this.is16bit = z;
        if (z) {
            this.field_4_text = in.readUnicodeLEString(field_2_textLength);
        } else {
            this.field_4_text = in.readCompressedUnicode(field_2_textLength);
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[SERIESTEXT]\n");
        sb.append("  .id     =");
        sb.append(HexDump.shortToHex(getId()));
        sb.append(10);
        sb.append("  .textLen=");
        sb.append(this.field_4_text.length());
        sb.append(10);
        sb.append("  .is16bit=");
        sb.append(this.is16bit);
        sb.append(10);
        sb.append("  .text   =");
        sb.append(" (");
        sb.append(getText());
        sb.append(" )");
        sb.append(10);
        sb.append("[/SERIESTEXT]\n");
        return sb.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_id);
        out.writeByte(this.field_4_text.length());
        if (this.is16bit) {
            out.writeByte(1);
            StringUtil.putUnicodeLE(this.field_4_text, out);
            return;
        }
        out.writeByte(0);
        StringUtil.putCompressedUnicode(this.field_4_text, out);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return (this.field_4_text.length() * (this.is16bit ? 2 : 1)) + 4;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        SeriesTextRecord rec = new SeriesTextRecord();
        rec.field_1_id = this.field_1_id;
        rec.is16bit = this.is16bit;
        rec.field_4_text = this.field_4_text;
        return rec;
    }

    public int getId() {
        return this.field_1_id;
    }

    public void setId(int id) {
        this.field_1_id = id;
    }

    public String getText() {
        return this.field_4_text;
    }

    public void setText(String text) {
        if (text.length() <= 255) {
            this.field_4_text = text;
            this.is16bit = StringUtil.hasMultibyte(text);
            return;
        }
        throw new IllegalArgumentException("Text is too long (" + text.length() + ">" + 255 + ")");
    }
}
