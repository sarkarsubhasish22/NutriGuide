package org.apache.poi.hssf.record.pivottable;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.StringUtil;

public final class DataItemRecord extends StandardRecord {
    public static final short sid = 197;
    private int df;
    private int ifmt;
    private int iiftab;
    private int isxvd;
    private int isxvdData;
    private int isxvi;
    private String name;

    public DataItemRecord(RecordInputStream in) {
        this.isxvdData = in.readUShort();
        this.iiftab = in.readUShort();
        this.df = in.readUShort();
        this.isxvd = in.readUShort();
        this.isxvi = in.readUShort();
        this.ifmt = in.readUShort();
        this.name = in.readString();
    }

    /* access modifiers changed from: protected */
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.isxvdData);
        out.writeShort(this.iiftab);
        out.writeShort(this.df);
        out.writeShort(this.isxvd);
        out.writeShort(this.isxvi);
        out.writeShort(this.ifmt);
        StringUtil.writeUnicodeString(out, this.name);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return StringUtil.getEncodedSize(this.name) + 12;
    }

    public short getSid() {
        return 197;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[SXDI]\n");
        buffer.append("  .isxvdData = ");
        buffer.append(HexDump.shortToHex(this.isxvdData));
        buffer.append("\n");
        buffer.append("  .iiftab = ");
        buffer.append(HexDump.shortToHex(this.iiftab));
        buffer.append("\n");
        buffer.append("  .df = ");
        buffer.append(HexDump.shortToHex(this.df));
        buffer.append("\n");
        buffer.append("  .isxvd = ");
        buffer.append(HexDump.shortToHex(this.isxvd));
        buffer.append("\n");
        buffer.append("  .isxvi = ");
        buffer.append(HexDump.shortToHex(this.isxvi));
        buffer.append("\n");
        buffer.append("  .ifmt = ");
        buffer.append(HexDump.shortToHex(this.ifmt));
        buffer.append("\n");
        buffer.append("[/SXDI]\n");
        return buffer.toString();
    }
}
