package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class SeriesIndexRecord extends StandardRecord {
    public static final short sid = 4197;
    private short field_1_index;

    public SeriesIndexRecord() {
    }

    public SeriesIndexRecord(RecordInputStream in) {
        this.field_1_index = in.readShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[SINDEX]\n");
        buffer.append("    .index                = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getIndex()));
        buffer.append(" (");
        buffer.append(getIndex());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("[/SINDEX]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_index);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 2;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        SeriesIndexRecord rec = new SeriesIndexRecord();
        rec.field_1_index = this.field_1_index;
        return rec;
    }

    public short getIndex() {
        return this.field_1_index;
    }

    public void setIndex(short field_1_index2) {
        this.field_1_index = field_1_index2;
    }
}
