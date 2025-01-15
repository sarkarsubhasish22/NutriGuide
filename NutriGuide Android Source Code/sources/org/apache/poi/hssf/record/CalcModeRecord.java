package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndianOutput;

public final class CalcModeRecord extends StandardRecord {
    public static final short AUTOMATIC = 1;
    public static final short AUTOMATIC_EXCEPT_TABLES = -1;
    public static final short MANUAL = 0;
    public static final short sid = 13;
    private short field_1_calcmode;

    public CalcModeRecord() {
    }

    public CalcModeRecord(RecordInputStream in) {
        this.field_1_calcmode = in.readShort();
    }

    public void setCalcMode(short calcmode) {
        this.field_1_calcmode = calcmode;
    }

    public short getCalcMode() {
        return this.field_1_calcmode;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[CALCMODE]\n");
        buffer.append("    .calcmode       = ");
        buffer.append(Integer.toHexString(getCalcMode()));
        buffer.append("\n");
        buffer.append("[/CALCMODE]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(getCalcMode());
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 2;
    }

    public short getSid() {
        return 13;
    }

    public Object clone() {
        CalcModeRecord rec = new CalcModeRecord();
        rec.field_1_calcmode = this.field_1_calcmode;
        return rec;
    }
}
