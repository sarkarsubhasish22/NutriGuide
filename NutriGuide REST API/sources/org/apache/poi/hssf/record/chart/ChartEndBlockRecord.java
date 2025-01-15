package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class ChartEndBlockRecord extends StandardRecord {
    public static final short sid = 2131;
    private short grbitFrt;
    private short iObjectKind;
    private short rt;
    private byte[] unused;

    public ChartEndBlockRecord() {
    }

    public ChartEndBlockRecord(RecordInputStream in) {
        this.rt = in.readShort();
        this.grbitFrt = in.readShort();
        this.iObjectKind = in.readShort();
        if (in.available() == 0) {
            this.unused = new byte[0];
            return;
        }
        byte[] bArr = new byte[6];
        this.unused = bArr;
        in.readFully(bArr);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return this.unused.length + 6;
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
        buffer.append("[ENDBLOCK]\n");
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
        buffer.append("[/ENDBLOCK]\n");
        return buffer.toString();
    }

    public ChartEndBlockRecord clone() {
        ChartEndBlockRecord record = new ChartEndBlockRecord();
        record.rt = this.rt;
        record.grbitFrt = this.grbitFrt;
        record.iObjectKind = this.iObjectKind;
        record.unused = (byte[]) this.unused.clone();
        return record;
    }
}
