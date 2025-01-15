package org.apache.poi.hssf.record;

import org.apache.poi.hssf.util.RKUtil;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class MulRKRecord extends StandardRecord {
    public static final short sid = 189;
    private int field_1_row;
    private short field_2_first_col;
    private RkRec[] field_3_rks;
    private short field_4_last_col;

    public int getRow() {
        return this.field_1_row;
    }

    public short getFirstColumn() {
        return this.field_2_first_col;
    }

    public short getLastColumn() {
        return this.field_4_last_col;
    }

    public int getNumColumns() {
        return (this.field_4_last_col - this.field_2_first_col) + 1;
    }

    public short getXFAt(int coffset) {
        return this.field_3_rks[coffset].xf;
    }

    public double getRKNumberAt(int coffset) {
        return RKUtil.decodeNumber(this.field_3_rks[coffset].rk);
    }

    public MulRKRecord(RecordInputStream in) {
        this.field_1_row = in.readUShort();
        this.field_2_first_col = in.readShort();
        this.field_3_rks = RkRec.parseRKs(in);
        this.field_4_last_col = in.readShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[MULRK]\n");
        buffer.append("\t.row\t = ");
        buffer.append(HexDump.shortToHex(getRow()));
        buffer.append("\n");
        buffer.append("\t.firstcol= ");
        buffer.append(HexDump.shortToHex(getFirstColumn()));
        buffer.append("\n");
        buffer.append("\t.lastcol = ");
        buffer.append(HexDump.shortToHex(getLastColumn()));
        buffer.append("\n");
        for (int k = 0; k < getNumColumns(); k++) {
            buffer.append("\txf[");
            buffer.append(k);
            buffer.append("] = ");
            buffer.append(HexDump.shortToHex(getXFAt(k)));
            buffer.append("\n");
            buffer.append("\trk[");
            buffer.append(k);
            buffer.append("] = ");
            buffer.append(getRKNumberAt(k));
            buffer.append("\n");
        }
        buffer.append("[/MULRK]\n");
        return buffer.toString();
    }

    public short getSid() {
        return 189;
    }

    public void serialize(LittleEndianOutput out) {
        throw new RecordFormatException("Sorry, you can't serialize MulRK in this release");
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        throw new RecordFormatException("Sorry, you can't serialize MulRK in this release");
    }

    private static final class RkRec {
        public static final int ENCODED_SIZE = 6;
        public final int rk;
        public final short xf;

        private RkRec(RecordInputStream in) {
            this.xf = in.readShort();
            this.rk = in.readInt();
        }

        public static RkRec[] parseRKs(RecordInputStream in) {
            int nItems = (in.remaining() - 2) / 6;
            RkRec[] retval = new RkRec[nItems];
            for (int i = 0; i < nItems; i++) {
                retval[i] = new RkRec(in);
            }
            return retval;
        }
    }
}
