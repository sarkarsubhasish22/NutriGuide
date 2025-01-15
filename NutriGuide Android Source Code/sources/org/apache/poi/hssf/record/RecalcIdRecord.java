package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class RecalcIdRecord extends StandardRecord {
    public static final short sid = 449;
    private final int _engineId;
    private final int _reserved0;

    public RecalcIdRecord(RecordInputStream in) {
        in.readUShort();
        this._reserved0 = in.readUShort();
        this._engineId = in.readInt();
    }

    public boolean isNeeded() {
        return true;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[RECALCID]\n");
        buffer.append("    .reserved = ");
        buffer.append(HexDump.shortToHex(this._reserved0));
        buffer.append("\n");
        buffer.append("    .engineId = ");
        buffer.append(HexDump.intToHex(this._engineId));
        buffer.append("\n");
        buffer.append("[/RECALCID]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(449);
        out.writeShort(this._reserved0);
        out.writeInt(this._engineId);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 8;
    }

    public short getSid() {
        return 449;
    }
}
