package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class FrameRecord extends StandardRecord {
    public static final short BORDER_TYPE_REGULAR = 0;
    public static final short BORDER_TYPE_SHADOW = 1;
    private static final BitField autoPosition = BitFieldFactory.getInstance(2);
    private static final BitField autoSize = BitFieldFactory.getInstance(1);
    public static final short sid = 4146;
    private short field_1_borderType;
    private short field_2_options;

    public FrameRecord() {
    }

    public FrameRecord(RecordInputStream in) {
        this.field_1_borderType = in.readShort();
        this.field_2_options = in.readShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[FRAME]\n");
        buffer.append("    .borderType           = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getBorderType()));
        buffer.append(" (");
        buffer.append(getBorderType());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .options              = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getOptions()));
        buffer.append(" (");
        buffer.append(getOptions());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("         .autoSize                 = ");
        buffer.append(isAutoSize());
        buffer.append(10);
        buffer.append("         .autoPosition             = ");
        buffer.append(isAutoPosition());
        buffer.append(10);
        buffer.append("[/FRAME]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_borderType);
        out.writeShort(this.field_2_options);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 4;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        FrameRecord rec = new FrameRecord();
        rec.field_1_borderType = this.field_1_borderType;
        rec.field_2_options = this.field_2_options;
        return rec;
    }

    public short getBorderType() {
        return this.field_1_borderType;
    }

    public void setBorderType(short field_1_borderType2) {
        this.field_1_borderType = field_1_borderType2;
    }

    public short getOptions() {
        return this.field_2_options;
    }

    public void setOptions(short field_2_options2) {
        this.field_2_options = field_2_options2;
    }

    public void setAutoSize(boolean value) {
        this.field_2_options = autoSize.setShortBoolean(this.field_2_options, value);
    }

    public boolean isAutoSize() {
        return autoSize.isSet(this.field_2_options);
    }

    public void setAutoPosition(boolean value) {
        this.field_2_options = autoPosition.setShortBoolean(this.field_2_options, value);
    }

    public boolean isAutoPosition() {
        return autoPosition.isSet(this.field_2_options);
    }
}
