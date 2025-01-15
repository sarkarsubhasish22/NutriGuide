package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class ObjectLinkRecord extends StandardRecord {
    public static final short ANCHOR_ID_CHART_TITLE = 1;
    public static final short ANCHOR_ID_SERIES_OR_POINT = 4;
    public static final short ANCHOR_ID_X_AXIS = 3;
    public static final short ANCHOR_ID_Y_AXIS = 2;
    public static final short ANCHOR_ID_Z_AXIS = 7;
    public static final short sid = 4135;
    private short field_1_anchorId;
    private short field_2_link1;
    private short field_3_link2;

    public ObjectLinkRecord() {
    }

    public ObjectLinkRecord(RecordInputStream in) {
        this.field_1_anchorId = in.readShort();
        this.field_2_link1 = in.readShort();
        this.field_3_link2 = in.readShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[OBJECTLINK]\n");
        buffer.append("    .anchorId             = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getAnchorId()));
        buffer.append(" (");
        buffer.append(getAnchorId());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .link1                = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getLink1()));
        buffer.append(" (");
        buffer.append(getLink1());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .link2                = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getLink2()));
        buffer.append(" (");
        buffer.append(getLink2());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("[/OBJECTLINK]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_anchorId);
        out.writeShort(this.field_2_link1);
        out.writeShort(this.field_3_link2);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 6;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        ObjectLinkRecord rec = new ObjectLinkRecord();
        rec.field_1_anchorId = this.field_1_anchorId;
        rec.field_2_link1 = this.field_2_link1;
        rec.field_3_link2 = this.field_3_link2;
        return rec;
    }

    public short getAnchorId() {
        return this.field_1_anchorId;
    }

    public void setAnchorId(short field_1_anchorId2) {
        this.field_1_anchorId = field_1_anchorId2;
    }

    public short getLink1() {
        return this.field_2_link1;
    }

    public void setLink1(short field_2_link12) {
        this.field_2_link1 = field_2_link12;
    }

    public short getLink2() {
        return this.field_3_link2;
    }

    public void setLink2(short field_3_link22) {
        this.field_3_link2 = field_3_link22;
    }
}
