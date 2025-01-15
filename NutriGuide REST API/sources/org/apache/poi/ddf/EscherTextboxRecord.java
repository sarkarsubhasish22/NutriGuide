package org.apache.poi.ddf;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.RecordFormatException;

public class EscherTextboxRecord extends EscherRecord {
    private static final byte[] NO_BYTES = new byte[0];
    public static final String RECORD_DESCRIPTION = "msofbtClientTextbox";
    public static final short RECORD_ID = -4083;
    private byte[] thedata = NO_BYTES;

    public int fillFields(byte[] data, int offset, EscherRecordFactory recordFactory) {
        int bytesRemaining = readHeader(data, offset);
        byte[] bArr = new byte[bytesRemaining];
        this.thedata = bArr;
        System.arraycopy(data, offset + 8, bArr, 0, bytesRemaining);
        return bytesRemaining + 8;
    }

    public int serialize(int offset, byte[] data, EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, getRecordId(), this);
        LittleEndian.putShort(data, offset, getOptions());
        LittleEndian.putShort(data, offset + 2, getRecordId());
        LittleEndian.putInt(data, offset + 4, this.thedata.length);
        byte[] bArr = this.thedata;
        System.arraycopy(bArr, 0, data, offset + 8, bArr.length);
        int pos = offset + 8 + this.thedata.length;
        listener.afterRecordSerialize(pos, getRecordId(), pos - offset, this);
        int size = pos - offset;
        if (size == getRecordSize()) {
            return size;
        }
        throw new RecordFormatException(size + " bytes written but getRecordSize() reports " + getRecordSize());
    }

    public byte[] getData() {
        return this.thedata;
    }

    public void setData(byte[] b, int start, int length) {
        byte[] bArr = new byte[length];
        this.thedata = bArr;
        System.arraycopy(b, start, bArr, 0, length);
    }

    public void setData(byte[] b) {
        setData(b, 0, b.length);
    }

    public int getRecordSize() {
        return this.thedata.length + 8;
    }

    public Object clone() {
        return super.clone();
    }

    public String getRecordName() {
        return "ClientTextbox";
    }

    public String toString() {
        String nl = System.getProperty("line.separator");
        String theDumpHex = "";
        try {
            if (this.thedata.length != 0) {
                theDumpHex = ("  Extra Data:" + nl) + HexDump.dump(this.thedata, 0, 0);
            }
        } catch (Exception e) {
            theDumpHex = "Error!!";
        }
        return getClass().getName() + ":" + nl + "  isContainer: " + isContainerRecord() + nl + "  options: 0x" + HexDump.toHex(getOptions()) + nl + "  recordId: 0x" + HexDump.toHex(getRecordId()) + nl + "  numchildren: " + getChildRecords().size() + nl + theDumpHex;
    }
}
