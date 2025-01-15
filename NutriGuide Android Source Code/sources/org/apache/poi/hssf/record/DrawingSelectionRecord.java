package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

public final class DrawingSelectionRecord extends StandardRecord {
    public static final short sid = 237;
    private int _cpsp;
    private int _dgslk;
    private OfficeArtRecordHeader _header;
    private int[] _shapeIds;
    private int _spidFocus;

    private static final class OfficeArtRecordHeader {
        public static final int ENCODED_SIZE = 8;
        private final int _length;
        private final int _type;
        private final int _verAndInstance;

        public OfficeArtRecordHeader(LittleEndianInput in) {
            this._verAndInstance = in.readUShort();
            this._type = in.readUShort();
            this._length = in.readInt();
        }

        public void serialize(LittleEndianOutput out) {
            out.writeShort(this._verAndInstance);
            out.writeShort(this._type);
            out.writeInt(this._length);
        }

        public String debugFormatAsString() {
            StringBuffer sb = new StringBuffer(32);
            sb.append("ver+inst=");
            sb.append(HexDump.shortToHex(this._verAndInstance));
            sb.append(" type=");
            sb.append(HexDump.shortToHex(this._type));
            sb.append(" len=");
            sb.append(HexDump.intToHex(this._length));
            return sb.toString();
        }
    }

    public DrawingSelectionRecord(RecordInputStream in) {
        this._header = new OfficeArtRecordHeader(in);
        this._cpsp = in.readInt();
        this._dgslk = in.readInt();
        this._spidFocus = in.readInt();
        int nShapes = in.available() / 4;
        int[] shapeIds = new int[nShapes];
        for (int i = 0; i < nShapes; i++) {
            shapeIds[i] = in.readInt();
        }
        this._shapeIds = shapeIds;
    }

    public short getSid() {
        return sid;
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return (this._shapeIds.length * 4) + 20;
    }

    public void serialize(LittleEndianOutput out) {
        this._header.serialize(out);
        out.writeInt(this._cpsp);
        out.writeInt(this._dgslk);
        out.writeInt(this._spidFocus);
        int i = 0;
        while (true) {
            int[] iArr = this._shapeIds;
            if (i < iArr.length) {
                out.writeInt(iArr[i]);
                i++;
            } else {
                return;
            }
        }
    }

    public Object clone() {
        return this;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[MSODRAWINGSELECTION]\n");
        sb.append("    .rh       =(");
        sb.append(this._header.debugFormatAsString());
        sb.append(")\n");
        sb.append("    .cpsp     =");
        sb.append(HexDump.intToHex(this._cpsp));
        sb.append(10);
        sb.append("    .dgslk    =");
        sb.append(HexDump.intToHex(this._dgslk));
        sb.append(10);
        sb.append("    .spidFocus=");
        sb.append(HexDump.intToHex(this._spidFocus));
        sb.append(10);
        sb.append("    .shapeIds =(");
        for (int i = 0; i < this._shapeIds.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(HexDump.intToHex(this._shapeIds[i]));
        }
        sb.append(")\n");
        sb.append("[/MSODRAWINGSELECTION]\n");
        return sb.toString();
    }
}
