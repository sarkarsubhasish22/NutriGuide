package org.apache.poi.ddf;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

public class EscherBlipRecord extends EscherRecord {
    private static final int HEADER_SIZE = 8;
    public static final String RECORD_DESCRIPTION = "msofbtBlip";
    public static final short RECORD_ID_END = -3817;
    public static final short RECORD_ID_START = -4072;
    protected byte[] field_pictureData;

    public int fillFields(byte[] data, int offset, EscherRecordFactory recordFactory) {
        int bytesAfterHeader = readHeader(data, offset);
        byte[] bArr = new byte[bytesAfterHeader];
        this.field_pictureData = bArr;
        System.arraycopy(data, offset + 8, bArr, 0, bytesAfterHeader);
        return bytesAfterHeader + 8;
    }

    public int serialize(int offset, byte[] data, EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, getRecordId(), this);
        LittleEndian.putShort(data, offset, getOptions());
        LittleEndian.putShort(data, offset + 2, getRecordId());
        byte[] bArr = this.field_pictureData;
        System.arraycopy(bArr, 0, data, offset + 4, bArr.length);
        listener.afterRecordSerialize(offset + 4 + this.field_pictureData.length, getRecordId(), this.field_pictureData.length + 4, this);
        return this.field_pictureData.length + 4;
    }

    public int getRecordSize() {
        return this.field_pictureData.length + 8;
    }

    public String getRecordName() {
        return "Blip";
    }

    public byte[] getPicturedata() {
        return this.field_pictureData;
    }

    public void setPictureData(byte[] pictureData) {
        this.field_pictureData = pictureData;
    }

    public String toString() {
        String extraData = HexDump.toHex(this.field_pictureData, 32);
        return getClass().getName() + ":" + 10 + "  RecordId: 0x" + HexDump.toHex(getRecordId()) + 10 + "  Options: 0x" + HexDump.toHex(getOptions()) + 10 + "  Extra Data:" + 10 + extraData;
    }
}
