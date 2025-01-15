package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class AreaFormatRecord extends StandardRecord {
    private static final BitField automatic = BitFieldFactory.getInstance(1);
    private static final BitField invert = BitFieldFactory.getInstance(2);
    public static final short sid = 4106;
    private int field_1_foregroundColor;
    private int field_2_backgroundColor;
    private short field_3_pattern;
    private short field_4_formatFlags;
    private short field_5_forecolorIndex;
    private short field_6_backcolorIndex;

    public AreaFormatRecord() {
    }

    public AreaFormatRecord(RecordInputStream in) {
        this.field_1_foregroundColor = in.readInt();
        this.field_2_backgroundColor = in.readInt();
        this.field_3_pattern = in.readShort();
        this.field_4_formatFlags = in.readShort();
        this.field_5_forecolorIndex = in.readShort();
        this.field_6_backcolorIndex = in.readShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[AREAFORMAT]\n");
        buffer.append("    .foregroundColor      = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getForegroundColor()));
        buffer.append(" (");
        buffer.append(getForegroundColor());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .backgroundColor      = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getBackgroundColor()));
        buffer.append(" (");
        buffer.append(getBackgroundColor());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .pattern              = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getPattern()));
        buffer.append(" (");
        buffer.append(getPattern());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .formatFlags          = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getFormatFlags()));
        buffer.append(" (");
        buffer.append(getFormatFlags());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("         .automatic                = ");
        buffer.append(isAutomatic());
        buffer.append(10);
        buffer.append("         .invert                   = ");
        buffer.append(isInvert());
        buffer.append(10);
        buffer.append("    .forecolorIndex       = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getForecolorIndex()));
        buffer.append(" (");
        buffer.append(getForecolorIndex());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .backcolorIndex       = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getBackcolorIndex()));
        buffer.append(" (");
        buffer.append(getBackcolorIndex());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("[/AREAFORMAT]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeInt(this.field_1_foregroundColor);
        out.writeInt(this.field_2_backgroundColor);
        out.writeShort(this.field_3_pattern);
        out.writeShort(this.field_4_formatFlags);
        out.writeShort(this.field_5_forecolorIndex);
        out.writeShort(this.field_6_backcolorIndex);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 16;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        AreaFormatRecord rec = new AreaFormatRecord();
        rec.field_1_foregroundColor = this.field_1_foregroundColor;
        rec.field_2_backgroundColor = this.field_2_backgroundColor;
        rec.field_3_pattern = this.field_3_pattern;
        rec.field_4_formatFlags = this.field_4_formatFlags;
        rec.field_5_forecolorIndex = this.field_5_forecolorIndex;
        rec.field_6_backcolorIndex = this.field_6_backcolorIndex;
        return rec;
    }

    public int getForegroundColor() {
        return this.field_1_foregroundColor;
    }

    public void setForegroundColor(int field_1_foregroundColor2) {
        this.field_1_foregroundColor = field_1_foregroundColor2;
    }

    public int getBackgroundColor() {
        return this.field_2_backgroundColor;
    }

    public void setBackgroundColor(int field_2_backgroundColor2) {
        this.field_2_backgroundColor = field_2_backgroundColor2;
    }

    public short getPattern() {
        return this.field_3_pattern;
    }

    public void setPattern(short field_3_pattern2) {
        this.field_3_pattern = field_3_pattern2;
    }

    public short getFormatFlags() {
        return this.field_4_formatFlags;
    }

    public void setFormatFlags(short field_4_formatFlags2) {
        this.field_4_formatFlags = field_4_formatFlags2;
    }

    public short getForecolorIndex() {
        return this.field_5_forecolorIndex;
    }

    public void setForecolorIndex(short field_5_forecolorIndex2) {
        this.field_5_forecolorIndex = field_5_forecolorIndex2;
    }

    public short getBackcolorIndex() {
        return this.field_6_backcolorIndex;
    }

    public void setBackcolorIndex(short field_6_backcolorIndex2) {
        this.field_6_backcolorIndex = field_6_backcolorIndex2;
    }

    public void setAutomatic(boolean value) {
        this.field_4_formatFlags = automatic.setShortBoolean(this.field_4_formatFlags, value);
    }

    public boolean isAutomatic() {
        return automatic.isSet(this.field_4_formatFlags);
    }

    public void setInvert(boolean value) {
        this.field_4_formatFlags = invert.setShortBoolean(this.field_4_formatFlags, value);
    }

    public boolean isInvert() {
        return invert.isSet(this.field_4_formatFlags);
    }
}
