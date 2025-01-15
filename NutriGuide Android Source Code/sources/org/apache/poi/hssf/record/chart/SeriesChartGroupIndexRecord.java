package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class SeriesChartGroupIndexRecord extends StandardRecord {
    public static final short sid = 4165;
    private short field_1_chartGroupIndex;

    public SeriesChartGroupIndexRecord() {
    }

    public SeriesChartGroupIndexRecord(RecordInputStream in) {
        this.field_1_chartGroupIndex = in.readShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[SERTOCRT]\n");
        buffer.append("    .chartGroupIndex      = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getChartGroupIndex()));
        buffer.append(" (");
        buffer.append(getChartGroupIndex());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("[/SERTOCRT]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_chartGroupIndex);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 2;
    }

    public short getSid() {
        return 4165;
    }

    public Object clone() {
        SeriesChartGroupIndexRecord rec = new SeriesChartGroupIndexRecord();
        rec.field_1_chartGroupIndex = this.field_1_chartGroupIndex;
        return rec;
    }

    public short getChartGroupIndex() {
        return this.field_1_chartGroupIndex;
    }

    public void setChartGroupIndex(short field_1_chartGroupIndex2) {
        this.field_1_chartGroupIndex = field_1_chartGroupIndex2;
    }
}
