package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class DatRecord extends StandardRecord {
    private static final BitField border = BitFieldFactory.getInstance(4);
    private static final BitField horizontalBorder = BitFieldFactory.getInstance(1);
    private static final BitField showSeriesKey = BitFieldFactory.getInstance(8);
    public static final short sid = 4195;
    private static final BitField verticalBorder = BitFieldFactory.getInstance(2);
    private short field_1_options;

    public DatRecord() {
    }

    public DatRecord(RecordInputStream in) {
        this.field_1_options = in.readShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[DAT]\n");
        buffer.append("    .options              = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getOptions()));
        buffer.append(" (");
        buffer.append(getOptions());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("         .horizontalBorder         = ");
        buffer.append(isHorizontalBorder());
        buffer.append(10);
        buffer.append("         .verticalBorder           = ");
        buffer.append(isVerticalBorder());
        buffer.append(10);
        buffer.append("         .border                   = ");
        buffer.append(isBorder());
        buffer.append(10);
        buffer.append("         .showSeriesKey            = ");
        buffer.append(isShowSeriesKey());
        buffer.append(10);
        buffer.append("[/DAT]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_options);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 2;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        DatRecord rec = new DatRecord();
        rec.field_1_options = this.field_1_options;
        return rec;
    }

    public short getOptions() {
        return this.field_1_options;
    }

    public void setOptions(short field_1_options2) {
        this.field_1_options = field_1_options2;
    }

    public void setHorizontalBorder(boolean value) {
        this.field_1_options = horizontalBorder.setShortBoolean(this.field_1_options, value);
    }

    public boolean isHorizontalBorder() {
        return horizontalBorder.isSet(this.field_1_options);
    }

    public void setVerticalBorder(boolean value) {
        this.field_1_options = verticalBorder.setShortBoolean(this.field_1_options, value);
    }

    public boolean isVerticalBorder() {
        return verticalBorder.isSet(this.field_1_options);
    }

    public void setBorder(boolean value) {
        this.field_1_options = border.setShortBoolean(this.field_1_options, value);
    }

    public boolean isBorder() {
        return border.isSet(this.field_1_options);
    }

    public void setShowSeriesKey(boolean value) {
        this.field_1_options = showSeriesKey.setShortBoolean(this.field_1_options, value);
    }

    public boolean isShowSeriesKey() {
        return showSeriesKey.isSet(this.field_1_options);
    }
}
