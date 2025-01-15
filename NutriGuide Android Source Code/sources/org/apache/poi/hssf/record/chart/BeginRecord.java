package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.LittleEndianOutput;

public final class BeginRecord extends StandardRecord {
    public static final short sid = 4147;

    public BeginRecord() {
    }

    public BeginRecord(RecordInputStream in) {
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[BEGIN]\n");
        buffer.append("[/BEGIN]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 0;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        return new BeginRecord();
    }
}
