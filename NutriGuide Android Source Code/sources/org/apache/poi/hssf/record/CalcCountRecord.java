package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndianOutput;

public final class CalcCountRecord extends StandardRecord {
    public static final short sid = 12;
    private short field_1_iterations;

    public CalcCountRecord() {
    }

    public CalcCountRecord(RecordInputStream in) {
        this.field_1_iterations = in.readShort();
    }

    public void setIterations(short iterations) {
        this.field_1_iterations = iterations;
    }

    public short getIterations() {
        return this.field_1_iterations;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[CALCCOUNT]\n");
        buffer.append("    .iterations     = ");
        buffer.append(Integer.toHexString(getIterations()));
        buffer.append("\n");
        buffer.append("[/CALCCOUNT]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(getIterations());
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 2;
    }

    public short getSid() {
        return 12;
    }

    public Object clone() {
        CalcCountRecord rec = new CalcCountRecord();
        rec.field_1_iterations = this.field_1_iterations;
        return rec;
    }
}
