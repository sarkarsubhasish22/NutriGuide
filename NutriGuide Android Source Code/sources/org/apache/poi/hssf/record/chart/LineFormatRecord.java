package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class LineFormatRecord extends StandardRecord {
    public static final short LINE_PATTERN_DARK_GRAY_PATTERN = 6;
    public static final short LINE_PATTERN_DASH = 1;
    public static final short LINE_PATTERN_DASH_DOT = 3;
    public static final short LINE_PATTERN_DASH_DOT_DOT = 4;
    public static final short LINE_PATTERN_DOT = 2;
    public static final short LINE_PATTERN_LIGHT_GRAY_PATTERN = 8;
    public static final short LINE_PATTERN_MEDIUM_GRAY_PATTERN = 7;
    public static final short LINE_PATTERN_NONE = 5;
    public static final short LINE_PATTERN_SOLID = 0;
    public static final short WEIGHT_HAIRLINE = -1;
    public static final short WEIGHT_MEDIUM = 1;
    public static final short WEIGHT_NARROW = 0;
    public static final short WEIGHT_WIDE = 2;
    private static final BitField auto = BitFieldFactory.getInstance(1);
    private static final BitField drawTicks = BitFieldFactory.getInstance(4);
    public static final short sid = 4103;
    private static final BitField unknown = BitFieldFactory.getInstance(4);
    private int field_1_lineColor;
    private short field_2_linePattern;
    private short field_3_weight;
    private short field_4_format;
    private short field_5_colourPaletteIndex;

    public LineFormatRecord() {
    }

    public LineFormatRecord(RecordInputStream in) {
        this.field_1_lineColor = in.readInt();
        this.field_2_linePattern = in.readShort();
        this.field_3_weight = in.readShort();
        this.field_4_format = in.readShort();
        this.field_5_colourPaletteIndex = in.readShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[LINEFORMAT]\n");
        buffer.append("    .lineColor            = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getLineColor()));
        buffer.append(" (");
        buffer.append(getLineColor());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .linePattern          = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getLinePattern()));
        buffer.append(" (");
        buffer.append(getLinePattern());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .weight               = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getWeight()));
        buffer.append(" (");
        buffer.append(getWeight());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .format               = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getFormat()));
        buffer.append(" (");
        buffer.append(getFormat());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("         .auto                     = ");
        buffer.append(isAuto());
        buffer.append(10);
        buffer.append("         .drawTicks                = ");
        buffer.append(isDrawTicks());
        buffer.append(10);
        buffer.append("         .unknown                  = ");
        buffer.append(isUnknown());
        buffer.append(10);
        buffer.append("    .colourPaletteIndex   = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getColourPaletteIndex()));
        buffer.append(" (");
        buffer.append(getColourPaletteIndex());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("[/LINEFORMAT]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeInt(this.field_1_lineColor);
        out.writeShort(this.field_2_linePattern);
        out.writeShort(this.field_3_weight);
        out.writeShort(this.field_4_format);
        out.writeShort(this.field_5_colourPaletteIndex);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 12;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        LineFormatRecord rec = new LineFormatRecord();
        rec.field_1_lineColor = this.field_1_lineColor;
        rec.field_2_linePattern = this.field_2_linePattern;
        rec.field_3_weight = this.field_3_weight;
        rec.field_4_format = this.field_4_format;
        rec.field_5_colourPaletteIndex = this.field_5_colourPaletteIndex;
        return rec;
    }

    public int getLineColor() {
        return this.field_1_lineColor;
    }

    public void setLineColor(int field_1_lineColor2) {
        this.field_1_lineColor = field_1_lineColor2;
    }

    public short getLinePattern() {
        return this.field_2_linePattern;
    }

    public void setLinePattern(short field_2_linePattern2) {
        this.field_2_linePattern = field_2_linePattern2;
    }

    public short getWeight() {
        return this.field_3_weight;
    }

    public void setWeight(short field_3_weight2) {
        this.field_3_weight = field_3_weight2;
    }

    public short getFormat() {
        return this.field_4_format;
    }

    public void setFormat(short field_4_format2) {
        this.field_4_format = field_4_format2;
    }

    public short getColourPaletteIndex() {
        return this.field_5_colourPaletteIndex;
    }

    public void setColourPaletteIndex(short field_5_colourPaletteIndex2) {
        this.field_5_colourPaletteIndex = field_5_colourPaletteIndex2;
    }

    public void setAuto(boolean value) {
        this.field_4_format = auto.setShortBoolean(this.field_4_format, value);
    }

    public boolean isAuto() {
        return auto.isSet(this.field_4_format);
    }

    public void setDrawTicks(boolean value) {
        this.field_4_format = drawTicks.setShortBoolean(this.field_4_format, value);
    }

    public boolean isDrawTicks() {
        return drawTicks.isSet(this.field_4_format);
    }

    public void setUnknown(boolean value) {
        this.field_4_format = unknown.setShortBoolean(this.field_4_format, value);
    }

    public boolean isUnknown() {
        return unknown.isSet(this.field_4_format);
    }
}
