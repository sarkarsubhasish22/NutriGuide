package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndianOutput;

public final class HCenterRecord extends StandardRecord {
    public static final short sid = 131;
    private short field_1_hcenter;

    public HCenterRecord() {
    }

    public HCenterRecord(RecordInputStream in) {
        this.field_1_hcenter = in.readShort();
    }

    public void setHCenter(boolean hc) {
        if (hc) {
            this.field_1_hcenter = 1;
        } else {
            this.field_1_hcenter = 0;
        }
    }

    public boolean getHCenter() {
        return this.field_1_hcenter == 1;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[HCENTER]\n");
        buffer.append("    .hcenter        = ");
        buffer.append(getHCenter());
        buffer.append("\n");
        buffer.append("[/HCENTER]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_hcenter);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 2;
    }

    public short getSid() {
        return 131;
    }

    public Object clone() {
        HCenterRecord rec = new HCenterRecord();
        rec.field_1_hcenter = this.field_1_hcenter;
        return rec;
    }
}
