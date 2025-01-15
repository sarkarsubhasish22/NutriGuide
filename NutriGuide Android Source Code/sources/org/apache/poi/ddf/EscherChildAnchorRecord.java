package org.apache.poi.ddf;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

public class EscherChildAnchorRecord extends EscherRecord {
    public static final String RECORD_DESCRIPTION = "MsofbtChildAnchor";
    public static final short RECORD_ID = -4081;
    private int field_1_dx1;
    private int field_2_dy1;
    private int field_3_dx2;
    private int field_4_dy2;

    public int fillFields(byte[] data, int offset, EscherRecordFactory recordFactory) {
        int readHeader = readHeader(data, offset);
        int pos = offset + 8;
        this.field_1_dx1 = LittleEndian.getInt(data, pos + 0);
        int size = 0 + 4;
        this.field_2_dy1 = LittleEndian.getInt(data, pos + size);
        int size2 = size + 4;
        this.field_3_dx2 = LittleEndian.getInt(data, pos + size2);
        int size3 = size2 + 4;
        this.field_4_dy2 = LittleEndian.getInt(data, pos + size3);
        return size3 + 4 + 8;
    }

    public int serialize(int offset, byte[] data, EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, getRecordId(), this);
        int pos = offset;
        LittleEndian.putShort(data, pos, getOptions());
        int pos2 = pos + 2;
        LittleEndian.putShort(data, pos2, getRecordId());
        int pos3 = pos2 + 2;
        LittleEndian.putInt(data, pos3, getRecordSize() - 8);
        int pos4 = pos3 + 4;
        LittleEndian.putInt(data, pos4, this.field_1_dx1);
        int pos5 = pos4 + 4;
        LittleEndian.putInt(data, pos5, this.field_2_dy1);
        int pos6 = pos5 + 4;
        LittleEndian.putInt(data, pos6, this.field_3_dx2);
        int pos7 = pos6 + 4;
        LittleEndian.putInt(data, pos7, this.field_4_dy2);
        int pos8 = pos7 + 4;
        listener.afterRecordSerialize(pos8, getRecordId(), pos8 - offset, this);
        return pos8 - offset;
    }

    public int getRecordSize() {
        return 24;
    }

    public short getRecordId() {
        return RECORD_ID;
    }

    public String getRecordName() {
        return "ChildAnchor";
    }

    public String toString() {
        String nl = System.getProperty("line.separator");
        return getClass().getName() + ":" + nl + "  RecordId: 0x" + HexDump.toHex((short) RECORD_ID) + nl + "  Options: 0x" + HexDump.toHex(getOptions()) + nl + "  X1: " + this.field_1_dx1 + nl + "  Y1: " + this.field_2_dy1 + nl + "  X2: " + this.field_3_dx2 + nl + "  Y2: " + this.field_4_dy2 + nl;
    }

    public int getDx1() {
        return this.field_1_dx1;
    }

    public void setDx1(int field_1_dx12) {
        this.field_1_dx1 = field_1_dx12;
    }

    public int getDy1() {
        return this.field_2_dy1;
    }

    public void setDy1(int field_2_dy12) {
        this.field_2_dy1 = field_2_dy12;
    }

    public int getDx2() {
        return this.field_3_dx2;
    }

    public void setDx2(int field_3_dx22) {
        this.field_3_dx2 = field_3_dx22;
    }

    public int getDy2() {
        return this.field_4_dy2;
    }

    public void setDy2(int field_4_dy22) {
        this.field_4_dy2 = field_4_dy22;
    }
}
