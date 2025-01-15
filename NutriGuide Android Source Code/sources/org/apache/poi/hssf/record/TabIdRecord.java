package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndianOutput;

public final class TabIdRecord extends StandardRecord {
    private static final short[] EMPTY_SHORT_ARRAY = new short[0];
    public static final short sid = 317;
    public short[] _tabids;

    public TabIdRecord() {
        this._tabids = EMPTY_SHORT_ARRAY;
    }

    public TabIdRecord(RecordInputStream in) {
        this._tabids = new short[(in.remaining() / 2)];
        int i = 0;
        while (true) {
            short[] sArr = this._tabids;
            if (i < sArr.length) {
                sArr[i] = in.readShort();
                i++;
            } else {
                return;
            }
        }
    }

    public void setTabIdArray(short[] array) {
        this._tabids = array;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[TABID]\n");
        buffer.append("    .elements        = ");
        buffer.append(this._tabids.length);
        buffer.append("\n");
        for (int i = 0; i < this._tabids.length; i++) {
            buffer.append("    .element_");
            buffer.append(i);
            buffer.append(" = ");
            buffer.append(this._tabids[i]);
            buffer.append("\n");
        }
        buffer.append("[/TABID]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        short[] tabids = this._tabids;
        for (short writeShort : tabids) {
            out.writeShort(writeShort);
        }
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return this._tabids.length * 2;
    }

    public short getSid() {
        return 317;
    }
}
