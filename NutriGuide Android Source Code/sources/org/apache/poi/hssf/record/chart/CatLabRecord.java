package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class CatLabRecord extends StandardRecord {
    public static final short sid = 2134;
    private short at;
    private short grbit;
    private short grbitFrt;
    private short rt;
    private Short unused;
    private short wOffset;

    public CatLabRecord(RecordInputStream in) {
        this.rt = in.readShort();
        this.grbitFrt = in.readShort();
        this.wOffset = in.readShort();
        this.at = in.readShort();
        this.grbit = in.readShort();
        if (in.available() == 0) {
            this.unused = null;
        } else {
            this.unused = Short.valueOf(in.readShort());
        }
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return (this.unused == null ? 0 : 2) + 10;
    }

    public short getSid() {
        return sid;
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.rt);
        out.writeShort(this.grbitFrt);
        out.writeShort(this.wOffset);
        out.writeShort(this.at);
        out.writeShort(this.grbit);
        Short sh = this.unused;
        if (sh != null) {
            out.writeShort(sh.shortValue());
        }
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[CATLAB]\n");
        buffer.append("    .rt      =");
        buffer.append(HexDump.shortToHex(this.rt));
        buffer.append(10);
        buffer.append("    .grbitFrt=");
        buffer.append(HexDump.shortToHex(this.grbitFrt));
        buffer.append(10);
        buffer.append("    .wOffset =");
        buffer.append(HexDump.shortToHex(this.wOffset));
        buffer.append(10);
        buffer.append("    .at      =");
        buffer.append(HexDump.shortToHex(this.at));
        buffer.append(10);
        buffer.append("    .grbit   =");
        buffer.append(HexDump.shortToHex(this.grbit));
        buffer.append(10);
        buffer.append("    .unused  =");
        buffer.append(HexDump.shortToHex(this.unused.shortValue()));
        buffer.append(10);
        buffer.append("[/CATLAB]\n");
        return buffer.toString();
    }
}
