package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

public final class FtCblsSubRecord extends SubRecord {
    private static final int ENCODED_SIZE = 20;
    public static final short sid = 12;
    private byte[] reserved;

    public FtCblsSubRecord() {
        this.reserved = new byte[20];
    }

    public FtCblsSubRecord(LittleEndianInput in, int size) {
        if (size == 20) {
            byte[] buf = new byte[size];
            in.readFully(buf);
            this.reserved = buf;
            return;
        }
        throw new RecordFormatException("Unexpected size (" + size + ")");
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[FtCbls ]");
        buffer.append("\n");
        buffer.append("  size     = ");
        buffer.append(getDataSize());
        buffer.append("\n");
        buffer.append("  reserved = ");
        buffer.append(HexDump.toHex(this.reserved));
        buffer.append("\n");
        buffer.append("[/FtCbls ]");
        buffer.append("\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(12);
        out.writeShort(this.reserved.length);
        out.write(this.reserved);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return this.reserved.length;
    }

    public short getSid() {
        return 12;
    }

    public Object clone() {
        FtCblsSubRecord rec = new FtCblsSubRecord();
        byte[] bArr = this.reserved;
        byte[] recdata = new byte[bArr.length];
        System.arraycopy(bArr, 0, recdata, 0, recdata.length);
        rec.reserved = recdata;
        return rec;
    }
}
