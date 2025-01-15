package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class CategorySeriesAxisRecord extends StandardRecord {
    private static final BitField crossesFarRight = BitFieldFactory.getInstance(2);
    private static final BitField reversed = BitFieldFactory.getInstance(4);
    public static final short sid = 4128;
    private static final BitField valueAxisCrossing = BitFieldFactory.getInstance(1);
    private short field_1_crossingPoint;
    private short field_2_labelFrequency;
    private short field_3_tickMarkFrequency;
    private short field_4_options;

    public CategorySeriesAxisRecord() {
    }

    public CategorySeriesAxisRecord(RecordInputStream in) {
        this.field_1_crossingPoint = in.readShort();
        this.field_2_labelFrequency = in.readShort();
        this.field_3_tickMarkFrequency = in.readShort();
        this.field_4_options = in.readShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[CATSERRANGE]\n");
        buffer.append("    .crossingPoint        = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getCrossingPoint()));
        buffer.append(" (");
        buffer.append(getCrossingPoint());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .labelFrequency       = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getLabelFrequency()));
        buffer.append(" (");
        buffer.append(getLabelFrequency());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .tickMarkFrequency    = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getTickMarkFrequency()));
        buffer.append(" (");
        buffer.append(getTickMarkFrequency());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .options              = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getOptions()));
        buffer.append(" (");
        buffer.append(getOptions());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("         .valueAxisCrossing        = ");
        buffer.append(isValueAxisCrossing());
        buffer.append(10);
        buffer.append("         .crossesFarRight          = ");
        buffer.append(isCrossesFarRight());
        buffer.append(10);
        buffer.append("         .reversed                 = ");
        buffer.append(isReversed());
        buffer.append(10);
        buffer.append("[/CATSERRANGE]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_crossingPoint);
        out.writeShort(this.field_2_labelFrequency);
        out.writeShort(this.field_3_tickMarkFrequency);
        out.writeShort(this.field_4_options);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 8;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        CategorySeriesAxisRecord rec = new CategorySeriesAxisRecord();
        rec.field_1_crossingPoint = this.field_1_crossingPoint;
        rec.field_2_labelFrequency = this.field_2_labelFrequency;
        rec.field_3_tickMarkFrequency = this.field_3_tickMarkFrequency;
        rec.field_4_options = this.field_4_options;
        return rec;
    }

    public short getCrossingPoint() {
        return this.field_1_crossingPoint;
    }

    public void setCrossingPoint(short field_1_crossingPoint2) {
        this.field_1_crossingPoint = field_1_crossingPoint2;
    }

    public short getLabelFrequency() {
        return this.field_2_labelFrequency;
    }

    public void setLabelFrequency(short field_2_labelFrequency2) {
        this.field_2_labelFrequency = field_2_labelFrequency2;
    }

    public short getTickMarkFrequency() {
        return this.field_3_tickMarkFrequency;
    }

    public void setTickMarkFrequency(short field_3_tickMarkFrequency2) {
        this.field_3_tickMarkFrequency = field_3_tickMarkFrequency2;
    }

    public short getOptions() {
        return this.field_4_options;
    }

    public void setOptions(short field_4_options2) {
        this.field_4_options = field_4_options2;
    }

    public void setValueAxisCrossing(boolean value) {
        this.field_4_options = valueAxisCrossing.setShortBoolean(this.field_4_options, value);
    }

    public boolean isValueAxisCrossing() {
        return valueAxisCrossing.isSet(this.field_4_options);
    }

    public void setCrossesFarRight(boolean value) {
        this.field_4_options = crossesFarRight.setShortBoolean(this.field_4_options, value);
    }

    public boolean isCrossesFarRight() {
        return crossesFarRight.isSet(this.field_4_options);
    }

    public void setReversed(boolean value) {
        this.field_4_options = reversed.setShortBoolean(this.field_4_options, value);
    }

    public boolean isReversed() {
        return reversed.isSet(this.field_4_options);
    }
}
