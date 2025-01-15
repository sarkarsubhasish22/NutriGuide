package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndianOutput;

public final class UncalcedRecord extends StandardRecord {
    public static final short sid = 94;

    public UncalcedRecord() {
    }

    public short getSid() {
        return 94;
    }

    public UncalcedRecord(RecordInputStream in) {
        in.readShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[UNCALCED]\n");
        buffer.append("[/UNCALCED]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(0);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 2;
    }

    public static int getStaticRecordSize() {
        return 6;
    }
}
