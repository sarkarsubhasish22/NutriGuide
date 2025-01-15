package org.apache.poi.hssf.record.common;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.util.LittleEndianOutput;

public final class FtrHeader {
    private short grbitFrt;
    private short recordType;
    private byte[] reserved;

    public FtrHeader() {
        this.reserved = new byte[8];
    }

    public FtrHeader(RecordInputStream in) {
        this.recordType = in.readShort();
        this.grbitFrt = in.readShort();
        byte[] bArr = new byte[8];
        this.reserved = bArr;
        in.read(bArr, 0, 8);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(" [FUTURE HEADER]\n");
        buffer.append("   Type " + this.recordType);
        buffer.append("   Flags " + this.grbitFrt);
        buffer.append(" [/FUTURE HEADER]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.recordType);
        out.writeShort(this.grbitFrt);
        out.write(this.reserved);
    }

    public static int getDataSize() {
        return 12;
    }

    public short getRecordType() {
        return this.recordType;
    }

    public void setRecordType(short recordType2) {
        this.recordType = recordType2;
    }

    public short getGrbitFrt() {
        return this.grbitFrt;
    }

    public void setGrbitFrt(short grbitFrt2) {
        this.grbitFrt = grbitFrt2;
    }

    public byte[] getReserved() {
        return this.reserved;
    }

    public void setReserved(byte[] reserved2) {
        this.reserved = reserved2;
    }
}
