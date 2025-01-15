package org.apache.poi.hssf.record;

import org.apache.poi.hssf.util.RKUtil;
import org.apache.poi.util.LittleEndianOutput;

public final class RKRecord extends CellRecord {
    public static final short RK_IEEE_NUMBER = 0;
    public static final short RK_IEEE_NUMBER_TIMES_100 = 1;
    public static final short RK_INTEGER = 2;
    public static final short RK_INTEGER_TIMES_100 = 3;
    public static final short sid = 638;
    private int field_4_rk_number;

    private RKRecord() {
    }

    public RKRecord(RecordInputStream in) {
        super(in);
        this.field_4_rk_number = in.readInt();
    }

    public double getRKNumber() {
        return RKUtil.decodeNumber(this.field_4_rk_number);
    }

    /* access modifiers changed from: protected */
    public String getRecordName() {
        return "RK";
    }

    /* access modifiers changed from: protected */
    public void appendValueText(StringBuilder sb) {
        sb.append("  .value= ");
        sb.append(getRKNumber());
    }

    /* access modifiers changed from: protected */
    public void serializeValue(LittleEndianOutput out) {
        out.writeInt(this.field_4_rk_number);
    }

    /* access modifiers changed from: protected */
    public int getValueDataSize() {
        return 4;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        RKRecord rec = new RKRecord();
        copyBaseFields(rec);
        rec.field_4_rk_number = this.field_4_rk_number;
        return rec;
    }
}
