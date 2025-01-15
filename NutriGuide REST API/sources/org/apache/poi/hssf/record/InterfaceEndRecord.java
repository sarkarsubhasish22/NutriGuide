package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndianOutput;

public final class InterfaceEndRecord extends StandardRecord {
    public static final InterfaceEndRecord instance = new InterfaceEndRecord();
    public static final short sid = 226;

    private InterfaceEndRecord() {
    }

    public static Record create(RecordInputStream in) {
        int remaining = in.remaining();
        if (remaining == 0) {
            return instance;
        }
        if (remaining == 2) {
            return new InterfaceHdrRecord(in);
        }
        throw new RecordFormatException("Invalid record data size: " + in.remaining());
    }

    public String toString() {
        return "[INTERFACEEND/]\n";
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
}
