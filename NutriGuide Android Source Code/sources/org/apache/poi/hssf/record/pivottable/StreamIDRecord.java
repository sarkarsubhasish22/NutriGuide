package org.apache.poi.hssf.record.pivottable;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class StreamIDRecord extends StandardRecord {
    public static final short sid = 213;
    private int idstm;

    public StreamIDRecord(RecordInputStream in) {
        this.idstm = in.readShort();
    }

    /* access modifiers changed from: protected */
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.idstm);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 2;
    }

    public short getSid() {
        return sid;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[SXIDSTM]\n");
        buffer.append("    .idstm      =");
        buffer.append(HexDump.shortToHex(this.idstm));
        buffer.append(10);
        buffer.append("[/SXIDSTM]\n");
        return buffer.toString();
    }
}
