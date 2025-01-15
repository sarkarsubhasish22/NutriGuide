package org.apache.poi.ddf;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.RecordFormatException;

public class EscherSplitMenuColorsRecord extends EscherRecord {
    public static final String RECORD_DESCRIPTION = "MsofbtSplitMenuColors";
    public static final short RECORD_ID = -3810;
    private int field_1_color1;
    private int field_2_color2;
    private int field_3_color3;
    private int field_4_color4;

    public int fillFields(byte[] data, int offset, EscherRecordFactory recordFactory) {
        int bytesRemaining = readHeader(data, offset);
        int pos = offset + 8;
        this.field_1_color1 = LittleEndian.getInt(data, pos + 0);
        int size = 0 + 4;
        this.field_2_color2 = LittleEndian.getInt(data, pos + size);
        int size2 = size + 4;
        this.field_3_color3 = LittleEndian.getInt(data, pos + size2);
        int size3 = size2 + 4;
        this.field_4_color4 = LittleEndian.getInt(data, pos + size3);
        int size4 = size3 + 4;
        int bytesRemaining2 = bytesRemaining - size4;
        if (bytesRemaining2 == 0) {
            return size4 + 8 + bytesRemaining2;
        }
        throw new RecordFormatException("Expecting no remaining data but got " + bytesRemaining2 + " byte(s).");
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
        LittleEndian.putInt(data, pos4, this.field_1_color1);
        int pos5 = pos4 + 4;
        LittleEndian.putInt(data, pos5, this.field_2_color2);
        int pos6 = pos5 + 4;
        LittleEndian.putInt(data, pos6, this.field_3_color3);
        int pos7 = pos6 + 4;
        LittleEndian.putInt(data, pos7, this.field_4_color4);
        int pos8 = pos7 + 4;
        listener.afterRecordSerialize(pos8, getRecordId(), pos8 - offset, this);
        return getRecordSize();
    }

    public int getRecordSize() {
        return 24;
    }

    public short getRecordId() {
        return RECORD_ID;
    }

    public String getRecordName() {
        return "SplitMenuColors";
    }

    public String toString() {
        return getClass().getName() + ":" + 10 + "  RecordId: 0x" + HexDump.toHex((short) RECORD_ID) + 10 + "  Options: 0x" + HexDump.toHex(getOptions()) + 10 + "  Color1: 0x" + HexDump.toHex(this.field_1_color1) + 10 + "  Color2: 0x" + HexDump.toHex(this.field_2_color2) + 10 + "  Color3: 0x" + HexDump.toHex(this.field_3_color3) + 10 + "  Color4: 0x" + HexDump.toHex(this.field_4_color4) + 10 + "";
    }

    public int getColor1() {
        return this.field_1_color1;
    }

    public void setColor1(int field_1_color12) {
        this.field_1_color1 = field_1_color12;
    }

    public int getColor2() {
        return this.field_2_color2;
    }

    public void setColor2(int field_2_color22) {
        this.field_2_color2 = field_2_color22;
    }

    public int getColor3() {
        return this.field_3_color3;
    }

    public void setColor3(int field_3_color32) {
        this.field_3_color3 = field_3_color32;
    }

    public int getColor4() {
        return this.field_4_color4;
    }

    public void setColor4(int field_4_color42) {
        this.field_4_color4 = field_4_color42;
    }
}
