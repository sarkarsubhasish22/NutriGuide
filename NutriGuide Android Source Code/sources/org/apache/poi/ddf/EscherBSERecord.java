package org.apache.poi.ddf;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

public final class EscherBSERecord extends EscherRecord {
    public static final byte BT_DIB = 7;
    public static final byte BT_EMF = 2;
    public static final byte BT_ERROR = 0;
    public static final byte BT_JPEG = 5;
    public static final byte BT_PICT = 4;
    public static final byte BT_PNG = 6;
    public static final byte BT_UNKNOWN = 1;
    public static final byte BT_WMF = 3;
    public static final String RECORD_DESCRIPTION = "MsofbtBSE";
    public static final short RECORD_ID = -4089;
    private byte[] _remainingData;
    private byte field_10_unused2;
    private byte field_11_unused3;
    private EscherBlipRecord field_12_blipRecord;
    private byte field_1_blipTypeWin32;
    private byte field_2_blipTypeMacOS;
    private byte[] field_3_uid;
    private short field_4_tag;
    private int field_5_size;
    private int field_6_ref;
    private int field_7_offset;
    private byte field_8_usage;
    private byte field_9_name;

    public int fillFields(byte[] data, int offset, EscherRecordFactory recordFactory) {
        int bytesRemaining = readHeader(data, offset);
        int pos = offset + 8;
        this.field_1_blipTypeWin32 = data[pos];
        this.field_2_blipTypeMacOS = data[pos + 1];
        byte[] bArr = new byte[16];
        this.field_3_uid = bArr;
        int i = 0;
        System.arraycopy(data, pos + 2, bArr, 0, 16);
        this.field_4_tag = LittleEndian.getShort(data, pos + 18);
        this.field_5_size = LittleEndian.getInt(data, pos + 20);
        this.field_6_ref = LittleEndian.getInt(data, pos + 24);
        this.field_7_offset = LittleEndian.getInt(data, pos + 28);
        this.field_8_usage = data[pos + 32];
        this.field_9_name = data[pos + 33];
        this.field_10_unused2 = data[pos + 34];
        this.field_11_unused3 = data[pos + 35];
        int bytesRemaining2 = bytesRemaining - 36;
        int bytesRead = 0;
        if (bytesRemaining2 > 0) {
            EscherBlipRecord escherBlipRecord = (EscherBlipRecord) recordFactory.createRecord(data, pos + 36);
            this.field_12_blipRecord = escherBlipRecord;
            bytesRead = escherBlipRecord.fillFields(data, pos + 36, recordFactory);
        }
        int bytesRemaining3 = bytesRemaining2 - bytesRead;
        byte[] bArr2 = new byte[bytesRemaining3];
        this._remainingData = bArr2;
        System.arraycopy(data, pos + bytesRead + 36, bArr2, 0, bytesRemaining3);
        int i2 = bytesRemaining3 + 8 + 36;
        EscherBlipRecord escherBlipRecord2 = this.field_12_blipRecord;
        if (escherBlipRecord2 != null) {
            i = escherBlipRecord2.getRecordSize();
        }
        return i2 + i;
    }

    public int serialize(int offset, byte[] data, EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, getRecordId(), this);
        if (this._remainingData == null) {
            this._remainingData = new byte[0];
        }
        LittleEndian.putShort(data, offset, getOptions());
        LittleEndian.putShort(data, offset + 2, getRecordId());
        if (this._remainingData == null) {
            this._remainingData = new byte[0];
        }
        EscherBlipRecord escherBlipRecord = this.field_12_blipRecord;
        LittleEndian.putInt(data, offset + 4, this._remainingData.length + 36 + (escherBlipRecord == null ? 0 : escherBlipRecord.getRecordSize()));
        data[offset + 8] = this.field_1_blipTypeWin32;
        data[offset + 9] = this.field_2_blipTypeMacOS;
        for (int i = 0; i < 16; i++) {
            data[offset + 10 + i] = this.field_3_uid[i];
        }
        LittleEndian.putShort(data, offset + 26, this.field_4_tag);
        LittleEndian.putInt(data, offset + 28, this.field_5_size);
        LittleEndian.putInt(data, offset + 32, this.field_6_ref);
        LittleEndian.putInt(data, offset + 36, this.field_7_offset);
        data[offset + 40] = this.field_8_usage;
        data[offset + 41] = this.field_9_name;
        data[offset + 42] = this.field_10_unused2;
        data[offset + 43] = this.field_11_unused3;
        int bytesWritten = 0;
        EscherBlipRecord escherBlipRecord2 = this.field_12_blipRecord;
        if (escherBlipRecord2 != null) {
            bytesWritten = escherBlipRecord2.serialize(offset + 44, data, new NullEscherSerializationListener());
        }
        if (this._remainingData == null) {
            this._remainingData = new byte[0];
        }
        byte[] bArr = this._remainingData;
        System.arraycopy(bArr, 0, data, offset + 44 + bytesWritten, bArr.length);
        int pos = offset + 8 + 36 + this._remainingData.length + bytesWritten;
        listener.afterRecordSerialize(pos, getRecordId(), pos - offset, this);
        return pos - offset;
    }

    public int getRecordSize() {
        int field_12_size = 0;
        EscherBlipRecord escherBlipRecord = this.field_12_blipRecord;
        if (escherBlipRecord != null) {
            field_12_size = escherBlipRecord.getRecordSize();
        }
        int remaining_size = 0;
        byte[] bArr = this._remainingData;
        if (bArr != null) {
            remaining_size = bArr.length;
        }
        return field_12_size + 44 + remaining_size;
    }

    public String getRecordName() {
        return "BSE";
    }

    public byte getBlipTypeWin32() {
        return this.field_1_blipTypeWin32;
    }

    public void setBlipTypeWin32(byte blipTypeWin32) {
        this.field_1_blipTypeWin32 = blipTypeWin32;
    }

    public byte getBlipTypeMacOS() {
        return this.field_2_blipTypeMacOS;
    }

    public void setBlipTypeMacOS(byte blipTypeMacOS) {
        this.field_2_blipTypeMacOS = blipTypeMacOS;
    }

    public byte[] getUid() {
        return this.field_3_uid;
    }

    public void setUid(byte[] uid) {
        this.field_3_uid = uid;
    }

    public short getTag() {
        return this.field_4_tag;
    }

    public void setTag(short tag) {
        this.field_4_tag = tag;
    }

    public int getSize() {
        return this.field_5_size;
    }

    public void setSize(int size) {
        this.field_5_size = size;
    }

    public int getRef() {
        return this.field_6_ref;
    }

    public void setRef(int ref) {
        this.field_6_ref = ref;
    }

    public int getOffset() {
        return this.field_7_offset;
    }

    public void setOffset(int offset) {
        this.field_7_offset = offset;
    }

    public byte getUsage() {
        return this.field_8_usage;
    }

    public void setUsage(byte usage) {
        this.field_8_usage = usage;
    }

    public byte getName() {
        return this.field_9_name;
    }

    public void setName(byte name) {
        this.field_9_name = name;
    }

    public byte getUnused2() {
        return this.field_10_unused2;
    }

    public void setUnused2(byte unused2) {
        this.field_10_unused2 = unused2;
    }

    public byte getUnused3() {
        return this.field_11_unused3;
    }

    public void setUnused3(byte unused3) {
        this.field_11_unused3 = unused3;
    }

    public EscherBlipRecord getBlipRecord() {
        return this.field_12_blipRecord;
    }

    public void setBlipRecord(EscherBlipRecord blipRecord) {
        this.field_12_blipRecord = blipRecord;
    }

    public byte[] getRemainingData() {
        return this._remainingData;
    }

    public void setRemainingData(byte[] remainingData) {
        this._remainingData = remainingData;
    }

    public String toString() {
        byte[] bArr = this._remainingData;
        String extraData = bArr == null ? null : HexDump.toHex(bArr, 32);
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName());
        sb.append(":");
        sb.append(10);
        sb.append("  RecordId: 0x");
        sb.append(HexDump.toHex((short) RECORD_ID));
        sb.append(10);
        sb.append("  Options: 0x");
        sb.append(HexDump.toHex(getOptions()));
        sb.append(10);
        sb.append("  BlipTypeWin32: ");
        sb.append(this.field_1_blipTypeWin32);
        sb.append(10);
        sb.append("  BlipTypeMacOS: ");
        sb.append(this.field_2_blipTypeMacOS);
        sb.append(10);
        sb.append("  SUID: ");
        byte[] bArr2 = this.field_3_uid;
        sb.append(bArr2 == null ? "" : HexDump.toHex(bArr2));
        sb.append(10);
        sb.append("  Tag: ");
        sb.append(this.field_4_tag);
        sb.append(10);
        sb.append("  Size: ");
        sb.append(this.field_5_size);
        sb.append(10);
        sb.append("  Ref: ");
        sb.append(this.field_6_ref);
        sb.append(10);
        sb.append("  Offset: ");
        sb.append(this.field_7_offset);
        sb.append(10);
        sb.append("  Usage: ");
        sb.append(this.field_8_usage);
        sb.append(10);
        sb.append("  Name: ");
        sb.append(this.field_9_name);
        sb.append(10);
        sb.append("  Unused2: ");
        sb.append(this.field_10_unused2);
        sb.append(10);
        sb.append("  Unused3: ");
        sb.append(this.field_11_unused3);
        sb.append(10);
        sb.append("  blipRecord: ");
        sb.append(this.field_12_blipRecord);
        sb.append(10);
        sb.append("  Extra Data:");
        sb.append(10);
        sb.append(extraData);
        return sb.toString();
    }

    public static String getBlipType(byte b) {
        switch (b) {
            case 0:
                return " ERROR";
            case 1:
                return " UNKNOWN";
            case 2:
                return " EMF";
            case 3:
                return " WMF";
            case 4:
                return " PICT";
            case 5:
                return " JPEG";
            case 6:
                return " PNG";
            case 7:
                return " DIB";
            default:
                if (b < 32) {
                    return " NotKnown";
                }
                return " Client";
        }
    }
}
