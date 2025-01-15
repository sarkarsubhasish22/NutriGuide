package org.apache.poi.ddf;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

public class EscherDgRecord extends EscherRecord {
    public static final String RECORD_DESCRIPTION = "MsofbtDg";
    public static final short RECORD_ID = -4088;
    private int field_1_numShapes;
    private int field_2_lastMSOSPID;

    public int fillFields(byte[] data, int offset, EscherRecordFactory recordFactory) {
        int readHeader = readHeader(data, offset);
        int pos = offset + 8;
        this.field_1_numShapes = LittleEndian.getInt(data, pos + 0);
        int size = 0 + 4;
        this.field_2_lastMSOSPID = LittleEndian.getInt(data, pos + size);
        int size2 = size + 4;
        return getRecordSize();
    }

    public int serialize(int offset, byte[] data, EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, getRecordId(), this);
        LittleEndian.putShort(data, offset, getOptions());
        LittleEndian.putShort(data, offset + 2, getRecordId());
        LittleEndian.putInt(data, offset + 4, 8);
        LittleEndian.putInt(data, offset + 8, this.field_1_numShapes);
        LittleEndian.putInt(data, offset + 12, this.field_2_lastMSOSPID);
        listener.afterRecordSerialize(offset + 16, getRecordId(), getRecordSize(), this);
        return getRecordSize();
    }

    public int getRecordSize() {
        return 16;
    }

    public short getRecordId() {
        return RECORD_ID;
    }

    public String getRecordName() {
        return "Dg";
    }

    public String toString() {
        return getClass().getName() + ":" + 10 + "  RecordId: 0x" + HexDump.toHex((short) RECORD_ID) + 10 + "  Options: 0x" + HexDump.toHex(getOptions()) + 10 + "  NumShapes: " + this.field_1_numShapes + 10 + "  LastMSOSPID: " + this.field_2_lastMSOSPID + 10;
    }

    public int getNumShapes() {
        return this.field_1_numShapes;
    }

    public void setNumShapes(int field_1_numShapes2) {
        this.field_1_numShapes = field_1_numShapes2;
    }

    public int getLastMSOSPID() {
        return this.field_2_lastMSOSPID;
    }

    public void setLastMSOSPID(int field_2_lastMSOSPID2) {
        this.field_2_lastMSOSPID = field_2_lastMSOSPID2;
    }

    public short getDrawingGroupId() {
        return (short) (getOptions() >> 4);
    }

    public void incrementShapeCount() {
        this.field_1_numShapes++;
    }
}
