package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class LabelSSTRecord extends CellRecord {
    public static final short sid = 253;
    private int field_4_sst_index;

    public LabelSSTRecord() {
    }

    public LabelSSTRecord(RecordInputStream in) {
        super(in);
        this.field_4_sst_index = in.readInt();
    }

    public void setSSTIndex(int index) {
        this.field_4_sst_index = index;
    }

    public int getSSTIndex() {
        return this.field_4_sst_index;
    }

    /* access modifiers changed from: protected */
    public String getRecordName() {
        return "LABELSST";
    }

    /* access modifiers changed from: protected */
    public void appendValueText(StringBuilder sb) {
        sb.append("  .sstIndex = ");
        sb.append(HexDump.shortToHex(getXFIndex()));
    }

    /* access modifiers changed from: protected */
    public void serializeValue(LittleEndianOutput out) {
        out.writeInt(getSSTIndex());
    }

    /* access modifiers changed from: protected */
    public int getValueDataSize() {
        return 4;
    }

    public short getSid() {
        return 253;
    }

    public Object clone() {
        LabelSSTRecord rec = new LabelSSTRecord();
        copyBaseFields(rec);
        rec.field_4_sst_index = this.field_4_sst_index;
        return rec;
    }
}
