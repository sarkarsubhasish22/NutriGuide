package org.apache.poi.hssf.record;

import java.util.Arrays;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class HeaderFooterRecord extends StandardRecord {
    private static final byte[] BLANK_GUID = new byte[16];
    public static final short sid = 2204;
    private byte[] _rawData;

    public HeaderFooterRecord(byte[] data) {
        this._rawData = data;
    }

    public HeaderFooterRecord(RecordInputStream in) {
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
        System.arraycopy(this._rawData, 12, guid, 0, guid.length);
        return guid;
    }

    public boolean isCurrentSheet() {
        return Arrays.equals(getGuid(), BLANK_GUID);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        sb.append("HEADERFOOTER");
        sb.append("] (0x");
        sb.append(Integer.toHexString(UnknownRecord.HEADER_FOOTER_089C).toUpperCase() + ")\n");
        sb.append("  rawData=");
        sb.append(HexDump.toHex(this._rawData));
        sb.append("\n");
        sb.append("[/");
        sb.append("HEADERFOOTER");
        sb.append("]\n");
        return sb.toString();
    }

    public Object clone() {
        return cloneViaReserialise();
    }
}
