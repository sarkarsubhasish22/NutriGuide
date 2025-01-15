package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class FontBasisRecord extends StandardRecord {
    public static final short sid = 4192;
    private short field_1_xBasis;
    private short field_2_yBasis;
    private short field_3_heightBasis;
    private short field_4_scale;
    private short field_5_indexToFontTable;

    public FontBasisRecord() {
    }

    public FontBasisRecord(RecordInputStream in) {
        this.field_1_xBasis = in.readShort();
        this.field_2_yBasis = in.readShort();
        this.field_3_heightBasis = in.readShort();
        this.field_4_scale = in.readShort();
        this.field_5_indexToFontTable = in.readShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[FBI]\n");
        buffer.append("    .xBasis               = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getXBasis()));
        buffer.append(" (");
        buffer.append(getXBasis());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .yBasis               = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getYBasis()));
        buffer.append(" (");
        buffer.append(getYBasis());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .heightBasis          = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getHeightBasis()));
        buffer.append(" (");
        buffer.append(getHeightBasis());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .scale                = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getScale()));
        buffer.append(" (");
        buffer.append(getScale());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .indexToFontTable     = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getIndexToFontTable()));
        buffer.append(" (");
        buffer.append(getIndexToFontTable());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("[/FBI]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_xBasis);
        out.writeShort(this.field_2_yBasis);
        out.writeShort(this.field_3_heightBasis);
        out.writeShort(this.field_4_scale);
        out.writeShort(this.field_5_indexToFontTable);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 10;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        FontBasisRecord rec = new FontBasisRecord();
        rec.field_1_xBasis = this.field_1_xBasis;
        rec.field_2_yBasis = this.field_2_yBasis;
        rec.field_3_heightBasis = this.field_3_heightBasis;
        rec.field_4_scale = this.field_4_scale;
        rec.field_5_indexToFontTable = this.field_5_indexToFontTable;
        return rec;
    }

    public short getXBasis() {
        return this.field_1_xBasis;
    }

    public void setXBasis(short field_1_xBasis2) {
        this.field_1_xBasis = field_1_xBasis2;
    }

    public short getYBasis() {
        return this.field_2_yBasis;
    }

    public void setYBasis(short field_2_yBasis2) {
        this.field_2_yBasis = field_2_yBasis2;
    }

    public short getHeightBasis() {
        return this.field_3_heightBasis;
    }

    public void setHeightBasis(short field_3_heightBasis2) {
        this.field_3_heightBasis = field_3_heightBasis2;
    }

    public short getScale() {
        return this.field_4_scale;
    }

    public void setScale(short field_4_scale2) {
        this.field_4_scale = field_4_scale2;
    }

    public short getIndexToFontTable() {
        return this.field_5_indexToFontTable;
    }

    public void setIndexToFontTable(short field_5_indexToFontTable2) {
        this.field_5_indexToFontTable = field_5_indexToFontTable2;
    }
}
