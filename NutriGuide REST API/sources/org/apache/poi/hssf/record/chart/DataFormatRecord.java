package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class DataFormatRecord extends StandardRecord {
    public static final short sid = 4102;
    private static final BitField useExcel4Colors = BitFieldFactory.getInstance(1);
    private short field_1_pointNumber;
    private short field_2_seriesIndex;
    private short field_3_seriesNumber;
    private short field_4_formatFlags;

    public DataFormatRecord() {
    }

    public DataFormatRecord(RecordInputStream in) {
        this.field_1_pointNumber = in.readShort();
        this.field_2_seriesIndex = in.readShort();
        this.field_3_seriesNumber = in.readShort();
        this.field_4_formatFlags = in.readShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[DATAFORMAT]\n");
        buffer.append("    .pointNumber          = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getPointNumber()));
        buffer.append(" (");
        buffer.append(getPointNumber());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .seriesIndex          = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getSeriesIndex()));
        buffer.append(" (");
        buffer.append(getSeriesIndex());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .seriesNumber         = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getSeriesNumber()));
        buffer.append(" (");
        buffer.append(getSeriesNumber());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .formatFlags          = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getFormatFlags()));
        buffer.append(" (");
        buffer.append(getFormatFlags());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("         .useExcel4Colors          = ");
        buffer.append(isUseExcel4Colors());
        buffer.append(10);
        buffer.append("[/DATAFORMAT]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_pointNumber);
        out.writeShort(this.field_2_seriesIndex);
        out.writeShort(this.field_3_seriesNumber);
        out.writeShort(this.field_4_formatFlags);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 8;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        DataFormatRecord rec = new DataFormatRecord();
        rec.field_1_pointNumber = this.field_1_pointNumber;
        rec.field_2_seriesIndex = this.field_2_seriesIndex;
        rec.field_3_seriesNumber = this.field_3_seriesNumber;
        rec.field_4_formatFlags = this.field_4_formatFlags;
        return rec;
    }

    public short getPointNumber() {
        return this.field_1_pointNumber;
    }

    public void setPointNumber(short field_1_pointNumber2) {
        this.field_1_pointNumber = field_1_pointNumber2;
    }

    public short getSeriesIndex() {
        return this.field_2_seriesIndex;
    }

    public void setSeriesIndex(short field_2_seriesIndex2) {
        this.field_2_seriesIndex = field_2_seriesIndex2;
    }

    public short getSeriesNumber() {
        return this.field_3_seriesNumber;
    }

    public void setSeriesNumber(short field_3_seriesNumber2) {
        this.field_3_seriesNumber = field_3_seriesNumber2;
    }

    public short getFormatFlags() {
        return this.field_4_formatFlags;
    }

    public void setFormatFlags(short field_4_formatFlags2) {
        this.field_4_formatFlags = field_4_formatFlags2;
    }

    public void setUseExcel4Colors(boolean value) {
        this.field_4_formatFlags = useExcel4Colors.setShortBoolean(this.field_4_formatFlags, value);
    }

    public boolean isUseExcel4Colors() {
        return useExcel4Colors.isSet(this.field_4_formatFlags);
    }
}
