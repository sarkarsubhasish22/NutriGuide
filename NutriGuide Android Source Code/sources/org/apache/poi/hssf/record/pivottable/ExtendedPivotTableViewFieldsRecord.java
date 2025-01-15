package org.apache.poi.hssf.record.pivottable;

import org.apache.poi.hssf.record.RecordFormatException;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.StringUtil;

public final class ExtendedPivotTableViewFieldsRecord extends StandardRecord {
    private static final int STRING_NOT_PRESENT_LEN = 65535;
    public static final short sid = 256;
    private int _citmShow;
    private int _grbit1;
    private int _grbit2;
    private int _isxdiShow;
    private int _isxdiSort;
    private int _reserved1;
    private int _reserved2;
    private String _subtotalName;

    public ExtendedPivotTableViewFieldsRecord(RecordInputStream in) {
        this._grbit1 = in.readInt();
        this._grbit2 = in.readUByte();
        this._citmShow = in.readUByte();
        this._isxdiSort = in.readUShort();
        this._isxdiShow = in.readUShort();
        int remaining = in.remaining();
        if (remaining == 0) {
            this._reserved1 = 0;
            this._reserved2 = 0;
            this._subtotalName = null;
        } else if (remaining == 10) {
            int cchSubName = in.readUShort();
            this._reserved1 = in.readInt();
            this._reserved2 = in.readInt();
            if (cchSubName != 65535) {
                this._subtotalName = in.readUnicodeLEString(cchSubName);
            }
        } else {
            throw new RecordFormatException("Unexpected remaining size (" + in.remaining() + ")");
        }
    }

    /* access modifiers changed from: protected */
    public void serialize(LittleEndianOutput out) {
        out.writeInt(this._grbit1);
        out.writeByte(this._grbit2);
        out.writeByte(this._citmShow);
        out.writeShort(this._isxdiSort);
        out.writeShort(this._isxdiShow);
        String str = this._subtotalName;
        if (str == null) {
            out.writeShort(65535);
        } else {
            out.writeShort(str.length());
        }
        out.writeInt(this._reserved1);
        out.writeInt(this._reserved2);
        String str2 = this._subtotalName;
        if (str2 != null) {
            StringUtil.putUnicodeLE(str2, out);
        }
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        String str = this._subtotalName;
        return (str == null ? 0 : str.length() * 2) + 20;
    }

    public short getSid() {
        return 256;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[SXVDEX]\n");
        buffer.append("    .grbit1 =");
        buffer.append(HexDump.intToHex(this._grbit1));
        buffer.append("\n");
        buffer.append("    .grbit2 =");
        buffer.append(HexDump.byteToHex(this._grbit2));
        buffer.append("\n");
        buffer.append("    .citmShow =");
        buffer.append(HexDump.byteToHex(this._citmShow));
        buffer.append("\n");
        buffer.append("    .isxdiSort =");
        buffer.append(HexDump.shortToHex(this._isxdiSort));
        buffer.append("\n");
        buffer.append("    .isxdiShow =");
        buffer.append(HexDump.shortToHex(this._isxdiShow));
        buffer.append("\n");
        buffer.append("    .subtotalName =");
        buffer.append(this._subtotalName);
        buffer.append("\n");
        buffer.append("[/SXVDEX]\n");
        return buffer.toString();
    }
}
