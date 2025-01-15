package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.LittleEndianOutput;

public final class EndRecord extends StandardRecord {
    public static final short sid = 4148;

    public EndRecord() {
    }

    public EndRecord(RecordInputStream in) {
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[END]\n");
        buffer.append("[/END]\n");
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
        return new EndRecord();
    }
}
