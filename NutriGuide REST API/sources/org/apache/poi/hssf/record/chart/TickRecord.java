package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class TickRecord extends StandardRecord {
    private static final BitField autoTextBackground = BitFieldFactory.getInstance(2);
    private static final BitField autoTextColor = BitFieldFactory.getInstance(1);
    private static final BitField autorotate = BitFieldFactory.getInstance(32);
    private static final BitField rotation = BitFieldFactory.getInstance(28);
    public static final short sid = 4126;
    private short field_10_options;
    private short field_11_tickColor;
    private short field_12_zero5;
    private byte field_1_majorTickType;
    private byte field_2_minorTickType;
    private byte field_3_labelPosition;
    private byte field_4_background;
    private int field_5_labelColorRgb;
    private int field_6_zero1;
    private int field_7_zero2;
    private int field_8_zero3;
    private int field_9_zero4;

    public TickRecord() {
    }

    public TickRecord(RecordInputStream in) {
        this.field_1_majorTickType = in.readByte();
        this.field_2_minorTickType = in.readByte();
        this.field_3_labelPosition = in.readByte();
        this.field_4_background = in.readByte();
        this.field_5_labelColorRgb = in.readInt();
        this.field_6_zero1 = in.readInt();
        this.field_7_zero2 = in.readInt();
        this.field_8_zero3 = in.readInt();
        this.field_9_zero4 = in.readInt();
        this.field_10_options = in.readShort();
        this.field_11_tickColor = in.readShort();
        this.field_12_zero5 = in.readShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[TICK]\n");
        buffer.append("    .majorTickType        = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getMajorTickType()));
        buffer.append(" (");
        buffer.append(getMajorTickType());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .minorTickType        = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getMinorTickType()));
        buffer.append(" (");
        buffer.append(getMinorTickType());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .labelPosition        = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getLabelPosition()));
        buffer.append(" (");
        buffer.append(getLabelPosition());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .background           = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getBackground()));
        buffer.append(" (");
        buffer.append(getBackground());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .labelColorRgb        = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getLabelColorRgb()));
        buffer.append(" (");
        buffer.append(getLabelColorRgb());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .zero1                = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getZero1()));
        buffer.append(" (");
        buffer.append(getZero1());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .zero2                = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getZero2()));
        buffer.append(" (");
        buffer.append(getZero2());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .options              = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getOptions()));
        buffer.append(" (");
        buffer.append(getOptions());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("         .autoTextColor            = ");
        buffer.append(isAutoTextColor());
        buffer.append(10);
        buffer.append("         .autoTextBackground       = ");
        buffer.append(isAutoTextBackground());
        buffer.append(10);
        buffer.append("         .rotation                 = ");
        buffer.append(getRotation());
        buffer.append(10);
        buffer.append("         .autorotate               = ");
        buffer.append(isAutorotate());
        buffer.append(10);
        buffer.append("    .tickColor            = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getTickColor()));
        buffer.append(" (");
        buffer.append(getTickColor());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .zero3                = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getZero3()));
        buffer.append(" (");
        buffer.append(getZero3());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("[/TICK]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeByte(this.field_1_majorTickType);
        out.writeByte(this.field_2_minorTickType);
        out.writeByte(this.field_3_labelPosition);
        out.writeByte(this.field_4_background);
        out.writeInt(this.field_5_labelColorRgb);
        out.writeInt(this.field_6_zero1);
        out.writeInt(this.field_7_zero2);
        out.writeInt(this.field_8_zero3);
        out.writeInt(this.field_9_zero4);
        out.writeShort(this.field_10_options);
        out.writeShort(this.field_11_tickColor);
        out.writeShort(this.field_12_zero5);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 30;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        TickRecord rec = new TickRecord();
        rec.field_1_majorTickType = this.field_1_majorTickType;
        rec.field_2_minorTickType = this.field_2_minorTickType;
        rec.field_3_labelPosition = this.field_3_labelPosition;
        rec.field_4_background = this.field_4_background;
        rec.field_5_labelColorRgb = this.field_5_labelColorRgb;
        rec.field_6_zero1 = this.field_6_zero1;
        rec.field_7_zero2 = this.field_7_zero2;
        rec.field_8_zero3 = this.field_8_zero3;
        rec.field_9_zero4 = this.field_9_zero4;
        rec.field_10_options = this.field_10_options;
        rec.field_11_tickColor = this.field_11_tickColor;
        rec.field_12_zero5 = this.field_12_zero5;
        return rec;
    }

    public byte getMajorTickType() {
        return this.field_1_majorTickType;
    }

    public void setMajorTickType(byte field_1_majorTickType2) {
        this.field_1_majorTickType = field_1_majorTickType2;
    }

    public byte getMinorTickType() {
        return this.field_2_minorTickType;
    }

    public void setMinorTickType(byte field_2_minorTickType2) {
        this.field_2_minorTickType = field_2_minorTickType2;
    }

    public byte getLabelPosition() {
        return this.field_3_labelPosition;
    }

    public void setLabelPosition(byte field_3_labelPosition2) {
        this.field_3_labelPosition = field_3_labelPosition2;
    }

    public byte getBackground() {
        return this.field_4_background;
    }

    public void setBackground(byte field_4_background2) {
        this.field_4_background = field_4_background2;
    }

    public int getLabelColorRgb() {
        return this.field_5_labelColorRgb;
    }

    public void setLabelColorRgb(int field_5_labelColorRgb2) {
        this.field_5_labelColorRgb = field_5_labelColorRgb2;
    }

    public int getZero1() {
        return this.field_6_zero1;
    }

    public void setZero1(int field_6_zero12) {
        this.field_6_zero1 = field_6_zero12;
    }

    public int getZero2() {
        return this.field_7_zero2;
    }

    public void setZero2(int field_7_zero22) {
        this.field_7_zero2 = field_7_zero22;
    }

    public short getOptions() {
        return this.field_10_options;
    }

    public void setOptions(short field_10_options2) {
        this.field_10_options = field_10_options2;
    }

    public short getTickColor() {
        return this.field_11_tickColor;
    }

    public void setTickColor(short field_11_tickColor2) {
        this.field_11_tickColor = field_11_tickColor2;
    }

    public short getZero3() {
        return this.field_12_zero5;
    }

    public void setZero3(short field_12_zero3) {
        this.field_12_zero5 = field_12_zero3;
    }

    public void setAutoTextColor(boolean value) {
        this.field_10_options = autoTextColor.setShortBoolean(this.field_10_options, value);
    }

    public boolean isAutoTextColor() {
        return autoTextColor.isSet(this.field_10_options);
    }

    public void setAutoTextBackground(boolean value) {
        this.field_10_options = autoTextBackground.setShortBoolean(this.field_10_options, value);
    }

    public boolean isAutoTextBackground() {
        return autoTextBackground.isSet(this.field_10_options);
    }

    public void setRotation(short value) {
        this.field_10_options = rotation.setShortValue(this.field_10_options, value);
    }

    public short getRotation() {
        return rotation.getShortValue(this.field_10_options);
    }

    public void setAutorotate(boolean value) {
        this.field_10_options = autorotate.setShortBoolean(this.field_10_options, value);
    }

    public boolean isAutorotate() {
        return autorotate.isSet(this.field_10_options);
    }
}
