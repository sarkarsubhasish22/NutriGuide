package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class ChartEndObjectRecord extends StandardRecord {
    public static final short sid = 2133;
    private short grbitFrt;
    private short iObjectKind;
    private short rt;
    private byte[] unused;

    public ChartEndObjectRecord(RecordInputStream in) {
        this.rt = in.readShort();
        this.grbitFrt = in.readShort();
        this.iObjectKind = in.readShort();
        byte[] bArr = new byte[6];
        this.unused = bArr;
        in.readFully(bArr);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 12;
    }

    public short getSid() {
        return sid;
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.rt);
        out.writeShort(this.grbitFrt);
        out.writeShort(this.iObjectKind);
        out.write(this.unused);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[ENDOBJECT]\n");
        buffer.append("    .rt         =");
        buffer.append(HexDump.shortToHex(this.rt));
        buffer.append(10);
        buffer.append("    .grbitFrt   =");
        buffer.append(HexDump.shortToHex(this.grbitFrt));
        buffer.append(10);
        buffer.append("    .iObjectKind=");
        buffer.append(HexDump.shortToHex(this.iObjectKind));
        buffer.append(10);
        buffer.append("    .unused     =");
        buffer.append(HexDump.toHex(this.unused));
        buffer.append(10);
        buffer.append("[/ENDOBJECT]\n");
        return buffer.toString();
    }
}
