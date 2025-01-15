package org.apache.poi.hssf.record;

import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class ProtectRecord extends StandardRecord {
    private static final BitField protectFlag = BitFieldFactory.getInstance(1);
    public static final short sid = 18;
    private int _options;

    private ProtectRecord(int options) {
        this._options = options;
    }

    public ProtectRecord(boolean isProtected) {
        this(0);
        setProtect(isProtected);
    }

    public ProtectRecord(RecordInputStream in) {
        this((int) in.readShort());
    }

    public void setProtect(boolean protect) {
        this._options = protectFlag.setBoolean(this._options, protect);
    }

    public boolean getProtect() {
        return protectFlag.isSet(this._options);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[PROTECT]\n");
        buffer.append("    .options = ");
        buffer.append(HexDump.shortToHex(this._options));
        buffer.append("\n");
        buffer.append("[/PROTECT]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this._options);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 2;
    }

    public short getSid() {
        return 18;
    }

    public Object clone() {
        return new ProtectRecord(this._options);
    }
}
