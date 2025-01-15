package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndianOutput;

public final class BottomMarginRecord extends StandardRecord implements Margin {
    public static final short sid = 41;
    private double field_1_margin;

    public BottomMarginRecord() {
    }

    public BottomMarginRecord(RecordInputStream in) {
        this.field_1_margin = in.readDouble();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[BottomMargin]\n");
        buffer.append("    .margin               = ");
        buffer.append(" (");
        buffer.append(getMargin());
        buffer.append(" )\n");
        buffer.append("[/BottomMargin]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeDouble(this.field_1_margin);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 8;
    }

    public short getSid() {
        return 41;
    }

    public double getMargin() {
        return this.field_1_margin;
    }

    public void setMargin(double field_1_margin2) {
        this.field_1_margin = field_1_margin2;
    }

    public Object clone() {
        BottomMarginRecord rec = new BottomMarginRecord();
        rec.field_1_margin = this.field_1_margin;
        return rec;
    }
}
