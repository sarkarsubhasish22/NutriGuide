package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class BarRecord extends StandardRecord {
    private static final BitField displayAsPercentage = BitFieldFactory.getInstance(4);
    private static final BitField horizontal = BitFieldFactory.getInstance(1);
    private static final BitField shadow = BitFieldFactory.getInstance(8);
    public static final short sid = 4119;
    private static final BitField stacked = BitFieldFactory.getInstance(2);
    private short field_1_barSpace;
    private short field_2_categorySpace;
    private short field_3_formatFlags;

    public BarRecord() {
    }

    public BarRecord(RecordInputStream in) {
        this.field_1_barSpace = in.readShort();
        this.field_2_categorySpace = in.readShort();
        this.field_3_formatFlags = in.readShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[BAR]\n");
        buffer.append("    .barSpace             = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getBarSpace()));
        buffer.append(" (");
        buffer.append(getBarSpace());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .categorySpace        = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getCategorySpace()));
        buffer.append(" (");
        buffer.append(getCategorySpace());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .formatFlags          = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getFormatFlags()));
        buffer.append(" (");
        buffer.append(getFormatFlags());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("         .horizontal               = ");
        buffer.append(isHorizontal());
        buffer.append(10);
        buffer.append("         .stacked                  = ");
        buffer.append(isStacked());
        buffer.append(10);
        buffer.append("         .displayAsPercentage      = ");
        buffer.append(isDisplayAsPercentage());
        buffer.append(10);
        buffer.append("         .shadow                   = ");
        buffer.append(isShadow());
        buffer.append(10);
        buffer.append("[/BAR]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_barSpace);
        out.writeShort(this.field_2_categorySpace);
        out.writeShort(this.field_3_formatFlags);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 6;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        BarRecord rec = new BarRecord();
        rec.field_1_barSpace = this.field_1_barSpace;
        rec.field_2_categorySpace = this.field_2_categorySpace;
        rec.field_3_formatFlags = this.field_3_formatFlags;
        return rec;
    }

    public short getBarSpace() {
        return this.field_1_barSpace;
    }

    public void setBarSpace(short field_1_barSpace2) {
        this.field_1_barSpace = field_1_barSpace2;
    }

    public short getCategorySpace() {
        return this.field_2_categorySpace;
    }

    public void setCategorySpace(short field_2_categorySpace2) {
        this.field_2_categorySpace = field_2_categorySpace2;
    }

    public short getFormatFlags() {
        return this.field_3_formatFlags;
    }

    public void setFormatFlags(short field_3_formatFlags2) {
        this.field_3_formatFlags = field_3_formatFlags2;
    }

    public void setHorizontal(boolean value) {
        this.field_3_formatFlags = horizontal.setShortBoolean(this.field_3_formatFlags, value);
    }

    public boolean isHorizontal() {
        return horizontal.isSet(this.field_3_formatFlags);
    }

    public void setStacked(boolean value) {
        this.field_3_formatFlags = stacked.setShortBoolean(this.field_3_formatFlags, value);
    }

    public boolean isStacked() {
        return stacked.isSet(this.field_3_formatFlags);
    }

    public void setDisplayAsPercentage(boolean value) {
        this.field_3_formatFlags = displayAsPercentage.setShortBoolean(this.field_3_formatFlags, value);
    }

    public boolean isDisplayAsPercentage() {
        return displayAsPercentage.isSet(this.field_3_formatFlags);
    }

    public void setShadow(boolean value) {
        this.field_3_formatFlags = shadow.setShortBoolean(this.field_3_formatFlags, value);
    }

    public boolean isShadow() {
        return shadow.isSet(this.field_3_formatFlags);
    }
}
