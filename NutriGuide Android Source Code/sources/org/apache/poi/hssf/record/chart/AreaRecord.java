package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class AreaRecord extends StandardRecord {
    private static final BitField displayAsPercentage = BitFieldFactory.getInstance(2);
    private static final BitField shadow = BitFieldFactory.getInstance(4);
    public static final short sid = 4122;
    private static final BitField stacked = BitFieldFactory.getInstance(1);
    private short field_1_formatFlags;

    public AreaRecord() {
    }

    public AreaRecord(RecordInputStream in) {
        this.field_1_formatFlags = in.readShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[AREA]\n");
        buffer.append("    .formatFlags          = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getFormatFlags()));
        buffer.append(" (");
        buffer.append(getFormatFlags());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("         .stacked                  = ");
        buffer.append(isStacked());
        buffer.append(10);
        buffer.append("         .displayAsPercentage      = ");
        buffer.append(isDisplayAsPercentage());
        buffer.append(10);
        buffer.append("         .shadow                   = ");
        buffer.append(isShadow());
        buffer.append(10);
        buffer.append("[/AREA]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_formatFlags);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 2;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        AreaRecord rec = new AreaRecord();
        rec.field_1_formatFlags = this.field_1_formatFlags;
        return rec;
    }

    public short getFormatFlags() {
        return this.field_1_formatFlags;
    }

    public void setFormatFlags(short field_1_formatFlags2) {
        this.field_1_formatFlags = field_1_formatFlags2;
    }

    public void setStacked(boolean value) {
        this.field_1_formatFlags = stacked.setShortBoolean(this.field_1_formatFlags, value);
    }

    public boolean isStacked() {
        return stacked.isSet(this.field_1_formatFlags);
    }

    public void setDisplayAsPercentage(boolean value) {
        this.field_1_formatFlags = displayAsPercentage.setShortBoolean(this.field_1_formatFlags, value);
    }

    public boolean isDisplayAsPercentage() {
        return displayAsPercentage.isSet(this.field_1_formatFlags);
    }

    public void setShadow(boolean value) {
        this.field_1_formatFlags = shadow.setShortBoolean(this.field_1_formatFlags, value);
    }

    public boolean isShadow() {
        return shadow.isSet(this.field_1_formatFlags);
    }
}
