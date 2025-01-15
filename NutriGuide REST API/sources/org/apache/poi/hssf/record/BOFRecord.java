package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class BOFRecord extends StandardRecord {
    public static final int BUILD = 4307;
    public static final int BUILD_YEAR = 1996;
    public static final int HISTORY_MASK = 65;
    public static final int TYPE_CHART = 32;
    public static final int TYPE_EXCEL_4_MACRO = 64;
    public static final int TYPE_VB_MODULE = 6;
    public static final int TYPE_WORKBOOK = 5;
    public static final int TYPE_WORKSHEET = 16;
    public static final int TYPE_WORKSPACE_FILE = 256;
    public static final int VERSION = 1536;
    public static final short sid = 2057;
    private int field_1_version;
    private int field_2_type;
    private int field_3_build;
    private int field_4_year;
    private int field_5_history;
    private int field_6_rversion;

    public BOFRecord() {
    }

    private BOFRecord(int type) {
        this.field_1_version = VERSION;
        this.field_2_type = type;
        this.field_3_build = BUILD;
        this.field_4_year = BUILD_YEAR;
        this.field_5_history = 1;
        this.field_6_rversion = VERSION;
    }

    public static BOFRecord createSheetBOF() {
        return new BOFRecord(16);
    }

    public BOFRecord(RecordInputStream in) {
        this.field_1_version = in.readShort();
        this.field_2_type = in.readShort();
        if (in.remaining() >= 2) {
            this.field_3_build = in.readShort();
        }
        if (in.remaining() >= 2) {
            this.field_4_year = in.readShort();
        }
        if (in.remaining() >= 4) {
            this.field_5_history = in.readInt();
        }
        if (in.remaining() >= 4) {
            this.field_6_rversion = in.readInt();
        }
    }

    public void setVersion(int version) {
        this.field_1_version = version;
    }

    public void setType(int type) {
        this.field_2_type = type;
    }

    public void setBuild(int build) {
        this.field_3_build = build;
    }

    public void setBuildYear(int year) {
        this.field_4_year = year;
    }

    public void setHistoryBitMask(int bitmask) {
        this.field_5_history = bitmask;
    }

    public void setRequiredVersion(int version) {
        this.field_6_rversion = version;
    }

    public int getVersion() {
        return this.field_1_version;
    }

    public int getType() {
        return this.field_2_type;
    }

    public int getBuild() {
        return this.field_3_build;
    }

    public int getBuildYear() {
        return this.field_4_year;
    }

    public int getHistoryBitMask() {
        return this.field_5_history;
    }

    public int getRequiredVersion() {
        return this.field_6_rversion;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[BOF RECORD]\n");
        buffer.append("    .version  = ");
        buffer.append(HexDump.shortToHex(getVersion()));
        buffer.append("\n");
        buffer.append("    .type     = ");
        buffer.append(HexDump.shortToHex(getType()));
        buffer.append(" (");
        buffer.append(getTypeName());
        buffer.append(")");
        buffer.append("\n");
        buffer.append("    .build    = ");
        buffer.append(HexDump.shortToHex(getBuild()));
        buffer.append("\n");
        buffer.append("    .buildyear= ");
        buffer.append(getBuildYear());
        buffer.append("\n");
        buffer.append("    .history  = ");
        buffer.append(HexDump.intToHex(getHistoryBitMask()));
        buffer.append("\n");
        buffer.append("    .reqver   = ");
        buffer.append(HexDump.intToHex(getRequiredVersion()));
        buffer.append("\n");
        buffer.append("[/BOF RECORD]\n");
        return buffer.toString();
    }

    private String getTypeName() {
        int i = this.field_2_type;
        if (i == 5) {
            return "workbook";
        }
        if (i == 6) {
            return "vb module";
        }
        if (i == 16) {
            return "worksheet";
        }
        if (i == 32) {
            return "chart";
        }
        if (i == 64) {
            return "excel 4 macro";
        }
        if (i != 256) {
            return "#error unknown type#";
        }
        return "workspace file";
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(getVersion());
        out.writeShort(getType());
        out.writeShort(getBuild());
        out.writeShort(getBuildYear());
        out.writeInt(getHistoryBitMask());
        out.writeInt(getRequiredVersion());
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 16;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        BOFRecord rec = new BOFRecord();
        rec.field_1_version = this.field_1_version;
        rec.field_2_type = this.field_2_type;
        rec.field_3_build = this.field_3_build;
        rec.field_4_year = this.field_4_year;
        rec.field_5_history = this.field_5_history;
        rec.field_6_rversion = this.field_6_rversion;
        return rec;
    }
}
