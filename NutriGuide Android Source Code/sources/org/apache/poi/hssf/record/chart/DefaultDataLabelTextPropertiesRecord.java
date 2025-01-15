package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class DefaultDataLabelTextPropertiesRecord extends StandardRecord {
    public static final short CATEGORY_DATA_TYPE_ALL_TEXT_CHARACTERISTIC = 2;
    public static final short CATEGORY_DATA_TYPE_SHOW_LABELS_CHARACTERISTIC = 0;
    public static final short CATEGORY_DATA_TYPE_VALUE_AND_PERCENTAGE_CHARACTERISTIC = 1;
    public static final short sid = 4132;
    private short field_1_categoryDataType;

    public DefaultDataLabelTextPropertiesRecord() {
    }

    public DefaultDataLabelTextPropertiesRecord(RecordInputStream in) {
        this.field_1_categoryDataType = in.readShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[DEFAULTTEXT]\n");
        buffer.append("    .categoryDataType     = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getCategoryDataType()));
        buffer.append(" (");
        buffer.append(getCategoryDataType());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("[/DEFAULTTEXT]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_categoryDataType);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 2;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        DefaultDataLabelTextPropertiesRecord rec = new DefaultDataLabelTextPropertiesRecord();
        rec.field_1_categoryDataType = this.field_1_categoryDataType;
        return rec;
    }

    public short getCategoryDataType() {
        return this.field_1_categoryDataType;
    }

    public void setCategoryDataType(short field_1_categoryDataType2) {
        this.field_1_categoryDataType = field_1_categoryDataType2;
    }
}
