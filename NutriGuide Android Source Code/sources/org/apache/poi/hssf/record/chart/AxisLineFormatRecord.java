package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class AxisLineFormatRecord extends StandardRecord {
    public static final short AXIS_TYPE_AXIS_LINE = 0;
    public static final short AXIS_TYPE_MAJOR_GRID_LINE = 1;
    public static final short AXIS_TYPE_MINOR_GRID_LINE = 2;
    public static final short AXIS_TYPE_WALLS_OR_FLOOR = 3;
    public static final short sid = 4129;
    private short field_1_axisType;

    public AxisLineFormatRecord() {
    }

    public AxisLineFormatRecord(RecordInputStream in) {
        this.field_1_axisType = in.readShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[AXISLINEFORMAT]\n");
        buffer.append("    .axisType             = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getAxisType()));
        buffer.append(" (");
        buffer.append(getAxisType());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("[/AXISLINEFORMAT]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_axisType);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 2;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        AxisLineFormatRecord rec = new AxisLineFormatRecord();
        rec.field_1_axisType = this.field_1_axisType;
        return rec;
    }

    public short getAxisType() {
        return this.field_1_axisType;
    }

    public void setAxisType(short field_1_axisType2) {
        this.field_1_axisType = field_1_axisType2;
    }
}
