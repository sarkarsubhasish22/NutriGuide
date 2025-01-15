package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class SCLRecord extends StandardRecord {
    public static final short sid = 160;
    private short field_1_numerator;
    private short field_2_denominator;

    public SCLRecord() {
    }

    public SCLRecord(RecordInputStream in) {
        this.field_1_numerator = in.readShort();
        this.field_2_denominator = in.readShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[SCL]\n");
        buffer.append("    .numerator            = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getNumerator()));
        buffer.append(" (");
        buffer.append(getNumerator());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .denominator          = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getDenominator()));
        buffer.append(" (");
        buffer.append(getDenominator());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("[/SCL]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_numerator);
        out.writeShort(this.field_2_denominator);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 4;
    }

    public short getSid() {
        return 160;
    }

    public Object clone() {
        SCLRecord rec = new SCLRecord();
        rec.field_1_numerator = this.field_1_numerator;
        rec.field_2_denominator = this.field_2_denominator;
        return rec;
    }

    public short getNumerator() {
        return this.field_1_numerator;
    }

    public void setNumerator(short field_1_numerator2) {
        this.field_1_numerator = field_1_numerator2;
    }

    public short getDenominator() {
        return this.field_2_denominator;
    }

    public void setDenominator(short field_2_denominator2) {
        this.field_2_denominator = field_2_denominator2;
    }
}
