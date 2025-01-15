package org.apache.poi.ddf;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

public class EscherBitmapBlip extends EscherBlipRecord {
    private static final int HEADER_SIZE = 8;
    public static final short RECORD_ID_DIB = -4065;
    public static final short RECORD_ID_JPEG = -4067;
    public static final short RECORD_ID_PNG = -4066;
    private byte[] field_1_UID;
    private byte field_2_marker = -1;

    public int fillFields(byte[] data, int offset, EscherRecordFactory recordFactory) {
        int bytesAfterHeader = readHeader(data, offset);
        int pos = offset + 8;
        byte[] bArr = new byte[16];
        this.field_1_UID = bArr;
        System.arraycopy(data, pos, bArr, 0, 16);
        int pos2 = pos + 16;
        this.field_2_marker = data[pos2];
        this.field_pictureData = new byte[(bytesAfterHeader - 17)];
        System.arraycopy(data, pos2 + 1, this.field_pictureData, 0, this.field_pictureData.length);
        return bytesAfterHeader + 8;
    }

    public int serialize(int offset, byte[] data, EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, getRecordId(), this);
        LittleEndian.putShort(data, offset, getOptions());
        LittleEndian.putShort(data, offset + 2, getRecordId());
        LittleEndian.putInt(data, offset + 4, getRecordSize() - 8);
        int pos = offset + 8;
        System.arraycopy(this.field_1_UID, 0, data, pos, 16);
        data[pos + 16] = this.field_2_marker;
        System.arraycopy(this.field_pictureData, 0, data, pos + 17, this.field_pictureData.length);
        listener.afterRecordSerialize(getRecordSize() + offset, getRecordId(), getRecordSize(), this);
        return this.field_pictureData.length + 25;
    }

    public int getRecordSize() {
        return this.field_pictureData.length + 25;
    }

    public byte[] getUID() {
        return this.field_1_UID;
    }

    public void setUID(byte[] field_1_UID2) {
        this.field_1_UID = field_1_UID2;
    }

    public byte getMarker() {
        return this.field_2_marker;
    }

    public void setMarker(byte field_2_marker2) {
        this.field_2_marker = field_2_marker2;
    }

    public String toString() {
        String extraData;
        String nl = System.getProperty("line.separator");
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        try {
            HexDump.dump(this.field_pictureData, 0, (OutputStream) b, 0);
            extraData = b.toString();
        } catch (Exception e) {
            extraData = e.toString();
        }
        return getClass().getName() + ":" + nl + "  RecordId: 0x" + HexDump.toHex(getRecordId()) + nl + "  Options: 0x" + HexDump.toHex(getOptions()) + nl + "  UID: 0x" + HexDump.toHex(this.field_1_UID) + nl + "  Marker: 0x" + HexDump.toHex(this.field_2_marker) + nl + "  Extra Data:" + nl + extraData;
    }
}
