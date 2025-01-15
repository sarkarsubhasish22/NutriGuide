package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class AxisRecord extends StandardRecord {
    public static final short AXIS_TYPE_CATEGORY_OR_X_AXIS = 0;
    public static final short AXIS_TYPE_SERIES_AXIS = 2;
    public static final short AXIS_TYPE_VALUE_AXIS = 1;
    public static final short sid = 4125;
    private short field_1_axisType;
    private int field_2_reserved1;
    private int field_3_reserved2;
    private int field_4_reserved3;
    private int field_5_reserved4;

    public AxisRecord() {
    }

    public AxisRecord(RecordInputStream in) {
        this.field_1_axisType = in.readShort();
        this.field_2_reserved1 = in.readInt();
        this.field_3_reserved2 = in.readInt();
        this.field_4_reserved3 = in.readInt();
        this.field_5_reserved4 = in.readInt();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[AXIS]\n");
        buffer.append("    .axisType             = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getAxisType()));
        buffer.append(" (");
        buffer.append(getAxisType());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .reserved1            = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getReserved1()));
        buffer.append(" (");
        buffer.append(getReserved1());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .reserved2            = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getReserved2()));
        buffer.append(" (");
        buffer.append(getReserved2());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .reserved3            = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getReserved3()));
        buffer.append(" (");
        buffer.append(getReserved3());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .reserved4            = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getReserved4()));
        buffer.append(" (");
        buffer.append(getReserved4());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("[/AXIS]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_axisType);
        out.writeInt(this.field_2_reserved1);
        out.writeInt(this.field_3_reserved2);
        out.writeInt(this.field_4_reserved3);
        out.writeInt(this.field_5_reserved4);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 18;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        AxisRecord rec = new AxisRecord();
        rec.field_1_axisType = this.field_1_axisType;
        rec.field_2_reserved1 = this.field_2_reserved1;
        rec.field_3_reserved2 = this.field_3_reserved2;
        rec.field_4_reserved3 = this.field_4_reserved3;
        rec.field_5_reserved4 = this.field_5_reserved4;
        return rec;
    }

    public short getAxisType() {
        return this.field_1_axisType;
    }

    public void setAxisType(short field_1_axisType2) {
        this.field_1_axisType = field_1_axisType2;
    }

    public int getReserved1() {
        return this.field_2_reserved1;
    }

    public void setReserved1(int field_2_reserved12) {
        this.field_2_reserved1 = field_2_reserved12;
    }

    public int getReserved2() {
        return this.field_3_reserved2;
    }

    public void setReserved2(int field_3_reserved22) {
        this.field_3_reserved2 = field_3_reserved22;
    }

    public int getReserved3() {
        return this.field_4_reserved3;
    }

    public void setReserved3(int field_4_reserved32) {
        this.field_4_reserved3 = field_4_reserved32;
    }

    public int getReserved4() {
        return this.field_5_reserved4;
    }

    public void setReserved4(int field_5_reserved42) {
        this.field_5_reserved4 = field_5_reserved42;
    }
}
