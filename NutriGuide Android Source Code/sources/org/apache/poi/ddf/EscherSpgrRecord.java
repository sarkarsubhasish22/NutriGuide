package org.apache.poi.ddf;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.RecordFormatException;

public class EscherSpgrRecord extends EscherRecord {
    public static final String RECORD_DESCRIPTION = "MsofbtSpgr";
    public static final short RECORD_ID = -4087;
    private int field_1_rectX1;
    private int field_2_rectY1;
    private int field_3_rectX2;
    private int field_4_rectY2;

    public int fillFields(byte[] data, int offset, EscherRecordFactory recordFactory) {
        int bytesRemaining = readHeader(data, offset);
        int pos = offset + 8;
        this.field_1_rectX1 = LittleEndian.getInt(data, pos + 0);
        int size = 0 + 4;
        this.field_2_rectY1 = LittleEndian.getInt(data, pos + size);
        int size2 = size + 4;
        this.field_3_rectX2 = LittleEndian.getInt(data, pos + size2);
        int size3 = size2 + 4;
        this.field_4_rectY2 = LittleEndian.getInt(data, pos + size3);
        int size4 = size3 + 4;
        int bytesRemaining2 = bytesRemaining - size4;
        if (bytesRemaining2 == 0) {
            return size4 + 8 + bytesRemaining2;
        }
        throw new RecordFormatException("Expected no remaining bytes but got " + bytesRemaining2);
    }

    public int serialize(int offset, byte[] data, EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, getRecordId(), this);
        LittleEndian.putShort(data, offset, getOptions());
        LittleEndian.putShort(data, offset + 2, getRecordId());
        LittleEndian.putInt(data, offset + 4, 16);
        LittleEndian.putInt(data, offset + 8, this.field_1_rectX1);
        LittleEndian.putInt(data, offset + 12, this.field_2_rectY1);
        LittleEndian.putInt(data, offset + 16, this.field_3_rectX2);
        LittleEndian.putInt(data, offset + 20, this.field_4_rectY2);
        listener.afterRecordSerialize(getRecordSize() + offset, getRecordId(), getRecordSize() + offset, this);
        return 24;
    }

    public int getRecordSize() {
        return 24;
    }

    public short getRecordId() {
        return RECORD_ID;
    }

    public String getRecordName() {
        return "Spgr";
    }

    public String toString() {
        return getClass().getName() + ":" + 10 + "  RecordId: 0x" + HexDump.toHex((short) RECORD_ID) + 10 + "  Options: 0x" + HexDump.toHex(getOptions()) + 10 + "  RectX: " + this.field_1_rectX1 + 10 + "  RectY: " + this.field_2_rectY1 + 10 + "  RectWidth: " + this.field_3_rectX2 + 10 + "  RectHeight: " + this.field_4_rectY2 + 10;
    }

    public int getRectX1() {
        return this.field_1_rectX1;
    }

    public void setRectX1(int x1) {
        this.field_1_rectX1 = x1;
    }

    public int getRectY1() {
        return this.field_2_rectY1;
    }

    public void setRectY1(int y1) {
        this.field_2_rectY1 = y1;
    }

    public int getRectX2() {
        return this.field_3_rectX2;
    }

    public void setRectX2(int x2) {
        this.field_3_rectX2 = x2;
    }

    public int getRectY2() {
        return this.field_4_rectY2;
    }

    public void setRectY2(int rectY2) {
        this.field_4_rectY2 = rectY2;
    }
}
