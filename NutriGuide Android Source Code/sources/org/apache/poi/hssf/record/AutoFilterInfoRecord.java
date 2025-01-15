package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndianOutput;

public final class AutoFilterInfoRecord extends StandardRecord {
    public static final short sid = 157;
    private short _cEntries;

    public AutoFilterInfoRecord() {
    }

    public AutoFilterInfoRecord(RecordInputStream in) {
        this._cEntries = in.readShort();
    }

    public void setNumEntries(short num) {
        this._cEntries = num;
    }

    public short getNumEntries() {
        return this._cEntries;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[AUTOFILTERINFO]\n");
        buffer.append("    .numEntries          = ");
        buffer.append(this._cEntries);
        buffer.append("\n");
        buffer.append("[/AUTOFILTERINFO]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this._cEntries);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 2;
    }

    public short getSid() {
        return 157;
    }

    public Object clone() {
        return cloneViaReserialise();
    }
}
