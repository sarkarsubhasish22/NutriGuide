package org.apache.poi.hssf.record;

import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.util.LittleEndianOutput;

public final class NumberRecord extends CellRecord {
    public static final short sid = 515;
    private double field_4_value;

    public NumberRecord() {
    }

    public NumberRecord(RecordInputStream in) {
        super(in);
        this.field_4_value = in.readDouble();
    }

    public void setValue(double value) {
        this.field_4_value = value;
    }

    public double getValue() {
        return this.field_4_value;
    }

    /* access modifiers changed from: protected */
    public String getRecordName() {
        return "NUMBER";
    }

    /* access modifiers changed from: protected */
    public void appendValueText(StringBuilder sb) {
        sb.append("  .value= ");
        sb.append(NumberToTextConverter.toText(this.field_4_value));
    }

    /* access modifiers changed from: protected */
    public void serializeValue(LittleEndianOutput out) {
        out.writeDouble(getValue());
    }

    /* access modifiers changed from: protected */
    public int getValueDataSize() {
        return 8;
    }

    public short getSid() {
        return 515;
    }

    public Object clone() {
        NumberRecord rec = new NumberRecord();
        copyBaseFields(rec);
        rec.field_4_value = this.field_4_value;
        return rec;
    }
}
