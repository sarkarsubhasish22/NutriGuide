package org.apache.poi.ddf;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

public class EscherSpRecord extends EscherRecord {
    public static final int FLAG_BACKGROUND = 1024;
    public static final int FLAG_CHILD = 2;
    public static final int FLAG_CONNECTOR = 256;
    public static final int FLAG_DELETED = 8;
    public static final int FLAG_FLIPHORIZ = 64;
    public static final int FLAG_FLIPVERT = 128;
    public static final int FLAG_GROUP = 1;
    public static final int FLAG_HASSHAPETYPE = 2048;
    public static final int FLAG_HAVEANCHOR = 512;
    public static final int FLAG_HAVEMASTER = 32;
    public static final int FLAG_OLESHAPE = 16;
    public static final int FLAG_PATRIARCH = 4;
    public static final String RECORD_DESCRIPTION = "MsofbtSp";
    public static final short RECORD_ID = -4086;
    private int field_1_shapeId;
    private int field_2_flags;

    public int fillFields(byte[] data, int offset, EscherRecordFactory recordFactory) {
        int readHeader = readHeader(data, offset);
        int pos = offset + 8;
        this.field_1_shapeId = LittleEndian.getInt(data, pos + 0);
        int size = 0 + 4;
        this.field_2_flags = LittleEndian.getInt(data, pos + size);
        int size2 = size + 4;
        return getRecordSize();
    }

    public int serialize(int offset, byte[] data, EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, getRecordId(), this);
        LittleEndian.putShort(data, offset, getOptions());
        LittleEndian.putShort(data, offset + 2, getRecordId());
        LittleEndian.putInt(data, offset + 4, 8);
        LittleEndian.putInt(data, offset + 8, this.field_1_shapeId);
        LittleEndian.putInt(data, offset + 12, this.field_2_flags);
        listener.afterRecordSerialize(getRecordSize() + offset, getRecordId(), getRecordSize(), this);
        return 16;
    }

    public int getRecordSize() {
        return 16;
    }

    public short getRecordId() {
        return RECORD_ID;
    }

    public String getRecordName() {
        return "Sp";
    }

    public String toString() {
        String nl = System.getProperty("line.separator");
        return getClass().getName() + ":" + nl + "  RecordId: 0x" + HexDump.toHex((short) RECORD_ID) + nl + "  Options: 0x" + HexDump.toHex(getOptions()) + nl + "  ShapeId: " + this.field_1_shapeId + nl + "  Flags: " + decodeFlags(this.field_2_flags) + " (0x" + HexDump.toHex(this.field_2_flags) + ")" + nl;
    }

    private String decodeFlags(int flags) {
        StringBuffer result = new StringBuffer();
        String str = "";
        result.append((flags & 1) != 0 ? "|GROUP" : str);
        result.append((flags & 2) != 0 ? "|CHILD" : str);
        result.append((flags & 4) != 0 ? "|PATRIARCH" : str);
        result.append((flags & 8) != 0 ? "|DELETED" : str);
        result.append((flags & 16) != 0 ? "|OLESHAPE" : str);
        result.append((flags & 32) != 0 ? "|HAVEMASTER" : str);
        result.append((flags & 64) != 0 ? "|FLIPHORIZ" : str);
        result.append((flags & 128) != 0 ? "|FLIPVERT" : str);
        result.append((flags & 256) != 0 ? "|CONNECTOR" : str);
        result.append((flags & 512) != 0 ? "|HAVEANCHOR" : str);
        result.append((flags & 1024) != 0 ? "|BACKGROUND" : str);
        if ((flags & 2048) != 0) {
            str = "|HASSHAPETYPE";
        }
        result.append(str);
        if (result.length() > 0) {
            result.deleteCharAt(0);
        }
        return result.toString();
    }

    public int getShapeId() {
        return this.field_1_shapeId;
    }

    public void setShapeId(int field_1_shapeId2) {
        this.field_1_shapeId = field_1_shapeId2;
    }

    public int getFlags() {
        return this.field_2_flags;
    }

    public void setFlags(int field_2_flags2) {
        this.field_2_flags = field_2_flags2;
    }
}
