package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class LegendRecord extends StandardRecord {
    public static final byte SPACING_CLOSE = 0;
    public static final byte SPACING_MEDIUM = 1;
    public static final byte SPACING_OPEN = 2;
    public static final byte TYPE_BOTTOM = 0;
    public static final byte TYPE_CORNER = 1;
    public static final byte TYPE_LEFT = 4;
    public static final byte TYPE_RIGHT = 3;
    public static final byte TYPE_TOP = 2;
    public static final byte TYPE_UNDOCKED = 7;
    private static final BitField autoPosition = BitFieldFactory.getInstance(1);
    private static final BitField autoSeries = BitFieldFactory.getInstance(2);
    private static final BitField autoXPositioning = BitFieldFactory.getInstance(4);
    private static final BitField autoYPositioning = BitFieldFactory.getInstance(8);
    private static final BitField dataTable = BitFieldFactory.getInstance(32);
    public static final short sid = 4117;
    private static final BitField vertical = BitFieldFactory.getInstance(16);
    private int field_1_xAxisUpperLeft;
    private int field_2_yAxisUpperLeft;
    private int field_3_xSize;
    private int field_4_ySize;
    private byte field_5_type;
    private byte field_6_spacing;
    private short field_7_options;

    public LegendRecord() {
    }

    public LegendRecord(RecordInputStream in) {
        this.field_1_xAxisUpperLeft = in.readInt();
        this.field_2_yAxisUpperLeft = in.readInt();
        this.field_3_xSize = in.readInt();
        this.field_4_ySize = in.readInt();
        this.field_5_type = in.readByte();
        this.field_6_spacing = in.readByte();
        this.field_7_options = in.readShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[LEGEND]\n");
        buffer.append("    .xAxisUpperLeft       = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getXAxisUpperLeft()));
        buffer.append(" (");
        buffer.append(getXAxisUpperLeft());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .yAxisUpperLeft       = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getYAxisUpperLeft()));
        buffer.append(" (");
        buffer.append(getYAxisUpperLeft());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .xSize                = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getXSize()));
        buffer.append(" (");
        buffer.append(getXSize());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .ySize                = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getYSize()));
        buffer.append(" (");
        buffer.append(getYSize());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .type                 = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getType()));
        buffer.append(" (");
        buffer.append(getType());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .spacing              = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getSpacing()));
        buffer.append(" (");
        buffer.append(getSpacing());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .options              = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getOptions()));
        buffer.append(" (");
        buffer.append(getOptions());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("         .autoPosition             = ");
        buffer.append(isAutoPosition());
        buffer.append(10);
        buffer.append("         .autoSeries               = ");
        buffer.append(isAutoSeries());
        buffer.append(10);
        buffer.append("         .autoXPositioning         = ");
        buffer.append(isAutoXPositioning());
        buffer.append(10);
        buffer.append("         .autoYPositioning         = ");
        buffer.append(isAutoYPositioning());
        buffer.append(10);
        buffer.append("         .vertical                 = ");
        buffer.append(isVertical());
        buffer.append(10);
        buffer.append("         .dataTable                = ");
        buffer.append(isDataTable());
        buffer.append(10);
        buffer.append("[/LEGEND]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeInt(this.field_1_xAxisUpperLeft);
        out.writeInt(this.field_2_yAxisUpperLeft);
        out.writeInt(this.field_3_xSize);
        out.writeInt(this.field_4_ySize);
        out.writeByte(this.field_5_type);
        out.writeByte(this.field_6_spacing);
        out.writeShort(this.field_7_options);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 20;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        LegendRecord rec = new LegendRecord();
        rec.field_1_xAxisUpperLeft = this.field_1_xAxisUpperLeft;
        rec.field_2_yAxisUpperLeft = this.field_2_yAxisUpperLeft;
        rec.field_3_xSize = this.field_3_xSize;
        rec.field_4_ySize = this.field_4_ySize;
        rec.field_5_type = this.field_5_type;
        rec.field_6_spacing = this.field_6_spacing;
        rec.field_7_options = this.field_7_options;
        return rec;
    }

    public int getXAxisUpperLeft() {
        return this.field_1_xAxisUpperLeft;
    }

    public void setXAxisUpperLeft(int field_1_xAxisUpperLeft2) {
        this.field_1_xAxisUpperLeft = field_1_xAxisUpperLeft2;
    }

    public int getYAxisUpperLeft() {
        return this.field_2_yAxisUpperLeft;
    }

    public void setYAxisUpperLeft(int field_2_yAxisUpperLeft2) {
        this.field_2_yAxisUpperLeft = field_2_yAxisUpperLeft2;
    }

    public int getXSize() {
        return this.field_3_xSize;
    }

    public void setXSize(int field_3_xSize2) {
        this.field_3_xSize = field_3_xSize2;
    }

    public int getYSize() {
        return this.field_4_ySize;
    }

    public void setYSize(int field_4_ySize2) {
        this.field_4_ySize = field_4_ySize2;
    }

    public byte getType() {
        return this.field_5_type;
    }

    public void setType(byte field_5_type2) {
        this.field_5_type = field_5_type2;
    }

    public byte getSpacing() {
        return this.field_6_spacing;
    }

    public void setSpacing(byte field_6_spacing2) {
        this.field_6_spacing = field_6_spacing2;
    }

    public short getOptions() {
        return this.field_7_options;
    }

    public void setOptions(short field_7_options2) {
        this.field_7_options = field_7_options2;
    }

    public void setAutoPosition(boolean value) {
        this.field_7_options = autoPosition.setShortBoolean(this.field_7_options, value);
    }

    public boolean isAutoPosition() {
        return autoPosition.isSet(this.field_7_options);
    }

    public void setAutoSeries(boolean value) {
        this.field_7_options = autoSeries.setShortBoolean(this.field_7_options, value);
    }

    public boolean isAutoSeries() {
        return autoSeries.isSet(this.field_7_options);
    }

    public void setAutoXPositioning(boolean value) {
        this.field_7_options = autoXPositioning.setShortBoolean(this.field_7_options, value);
    }

    public boolean isAutoXPositioning() {
        return autoXPositioning.isSet(this.field_7_options);
    }

    public void setAutoYPositioning(boolean value) {
        this.field_7_options = autoYPositioning.setShortBoolean(this.field_7_options, value);
    }

    public boolean isAutoYPositioning() {
        return autoYPositioning.isSet(this.field_7_options);
    }

    public void setVertical(boolean value) {
        this.field_7_options = vertical.setShortBoolean(this.field_7_options, value);
    }

    public boolean isVertical() {
        return vertical.isSet(this.field_7_options);
    }

    public void setDataTable(boolean value) {
        this.field_7_options = dataTable.setShortBoolean(this.field_7_options, value);
    }

    public boolean isDataTable() {
        return dataTable.isSet(this.field_7_options);
    }
}
