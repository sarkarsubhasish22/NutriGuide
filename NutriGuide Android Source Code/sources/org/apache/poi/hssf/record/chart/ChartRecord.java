package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.LittleEndianOutput;

public final class ChartRecord extends StandardRecord {
    public static final short sid = 4098;
    private int field_1_x;
    private int field_2_y;
    private int field_3_width;
    private int field_4_height;

    public ChartRecord() {
    }

    public ChartRecord(RecordInputStream in) {
        this.field_1_x = in.readInt();
        this.field_2_y = in.readInt();
        this.field_3_width = in.readInt();
        this.field_4_height = in.readInt();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[CHART]\n");
        sb.append("    .x     = ");
        sb.append(getX());
        sb.append(10);
        sb.append("    .y     = ");
        sb.append(getY());
        sb.append(10);
        sb.append("    .width = ");
        sb.append(getWidth());
        sb.append(10);
        sb.append("    .height= ");
        sb.append(getHeight());
        sb.append(10);
        sb.append("[/CHART]\n");
        return sb.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeInt(this.field_1_x);
        out.writeInt(this.field_2_y);
        out.writeInt(this.field_3_width);
        out.writeInt(this.field_4_height);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 16;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        ChartRecord rec = new ChartRecord();
        rec.field_1_x = this.field_1_x;
        rec.field_2_y = this.field_2_y;
        rec.field_3_width = this.field_3_width;
        rec.field_4_height = this.field_4_height;
        return rec;
    }

    public int getX() {
        return this.field_1_x;
    }

    public void setX(int x) {
        this.field_1_x = x;
    }

    public int getY() {
        return this.field_2_y;
    }

    public void setY(int y) {
        this.field_2_y = y;
    }

    public int getWidth() {
        return this.field_3_width;
    }

    public void setWidth(int width) {
        this.field_3_width = width;
    }

    public int getHeight() {
        return this.field_4_height;
    }

    public void setHeight(int height) {
        this.field_4_height = height;
    }
}
