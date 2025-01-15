package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class FontIndexRecord extends StandardRecord {
    public static final short sid = 4134;
    private short field_1_fontIndex;

    public FontIndexRecord() {
    }

    public FontIndexRecord(RecordInputStream in) {
        this.field_1_fontIndex = in.readShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[FONTX]\n");
        buffer.append("    .fontIndex            = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getFontIndex()));
        buffer.append(" (");
        buffer.append(getFontIndex());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("[/FONTX]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_fontIndex);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 2;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        FontIndexRecord rec = new FontIndexRecord();
        rec.field_1_fontIndex = this.field_1_fontIndex;
        return rec;
    }

    public short getFontIndex() {
        return this.field_1_fontIndex;
    }

    public void setFontIndex(short field_1_fontIndex2) {
        this.field_1_fontIndex = field_1_fontIndex2;
    }
}
