package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class UserSViewBegin extends StandardRecord {
    public static final short sid = 426;
    private byte[] _rawData;

    public UserSViewBegin(byte[] data) {
        this._rawData = data;
    }

    public UserSViewBegin(RecordInputStream in) {
        this._rawData = in.readRemainder();
    }

    public void serialize(LittleEndianOutput out) {
        out.write(this._rawData);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return this._rawData.length;
    }

    public short getSid() {
        return sid;
    }

    public byte[] getGuid() {
        byte[] guid = new byte[16];
        System.arraycopy(this._rawData, 0, guid, 0, guid.length);
        return guid;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        sb.append("USERSVIEWBEGIN");
        sb.append("] (0x");
        sb.append(Integer.toHexString(426).toUpperCase() + ")\n");
        sb.append("  rawData=");
        sb.append(HexDump.toHex(this._rawData));
        sb.append("\n");
        sb.append("[/");
        sb.append("USERSVIEWBEGIN");
        sb.append("]\n");
        return sb.toString();
    }

    public Object clone() {
        return cloneViaReserialise();
    }
}
