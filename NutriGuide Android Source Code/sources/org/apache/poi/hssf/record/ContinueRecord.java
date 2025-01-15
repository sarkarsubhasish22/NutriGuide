package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class ContinueRecord extends StandardRecord {
    public static final short sid = 60;
    private byte[] _data;

    public ContinueRecord(byte[] data) {
        this._data = data;
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return this._data.length;
    }

    public void serialize(LittleEndianOutput out) {
        out.write(this._data);
    }

    public byte[] getData() {
        return this._data;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[CONTINUE RECORD]\n");
        buffer.append("    .data = ");
        buffer.append(HexDump.toHex(this._data));
        buffer.append("\n");
        buffer.append("[/CONTINUE RECORD]\n");
        return buffer.toString();
    }

    public short getSid() {
        return 60;
    }

    public ContinueRecord(RecordInputStream in) {
        this._data = in.readRemainder();
    }

    public Object clone() {
        return new ContinueRecord(this._data);
    }
}
