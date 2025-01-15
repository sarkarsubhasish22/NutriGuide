package org.apache.poi.hssf.record.pivottable;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.StringUtil;

public final class ViewDefinitionRecord extends StandardRecord {
    public static final short sid = 176;
    private int cCol;
    private int cDim;
    private int cDimCol;
    private int cDimData;
    private int cDimPg;
    private int cDimRw;
    private int cRw;
    private int colFirst;
    private int colFirstData;
    private int colLast;
    private String dataField;
    private int grbit;
    private int iCache;
    private int ipos4Data;
    private int itblAutoFmt;
    private String name;
    private int reserved;
    private int rwFirst;
    private int rwFirstData;
    private int rwFirstHead;
    private int rwLast;
    private int sxaxis4Data;

    public ViewDefinitionRecord(RecordInputStream in) {
        this.rwFirst = in.readUShort();
        this.rwLast = in.readUShort();
        this.colFirst = in.readUShort();
        this.colLast = in.readUShort();
        this.rwFirstHead = in.readUShort();
        this.rwFirstData = in.readUShort();
        this.colFirstData = in.readUShort();
        this.iCache = in.readUShort();
        this.reserved = in.readUShort();
        this.sxaxis4Data = in.readUShort();
        this.ipos4Data = in.readUShort();
        this.cDim = in.readUShort();
        this.cDimRw = in.readUShort();
        this.cDimCol = in.readUShort();
        this.cDimPg = in.readUShort();
        this.cDimData = in.readUShort();
        this.cRw = in.readUShort();
        this.cCol = in.readUShort();
        this.grbit = in.readUShort();
        this.itblAutoFmt = in.readUShort();
        int cchName = in.readUShort();
        int cchData = in.readUShort();
        this.name = StringUtil.readUnicodeString(in, cchName);
        this.dataField = StringUtil.readUnicodeString(in, cchData);
    }

    /* access modifiers changed from: protected */
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.rwFirst);
        out.writeShort(this.rwLast);
        out.writeShort(this.colFirst);
        out.writeShort(this.colLast);
        out.writeShort(this.rwFirstHead);
        out.writeShort(this.rwFirstData);
        out.writeShort(this.colFirstData);
        out.writeShort(this.iCache);
        out.writeShort(this.reserved);
        out.writeShort(this.sxaxis4Data);
        out.writeShort(this.ipos4Data);
        out.writeShort(this.cDim);
        out.writeShort(this.cDimRw);
        out.writeShort(this.cDimCol);
        out.writeShort(this.cDimPg);
        out.writeShort(this.cDimData);
        out.writeShort(this.cRw);
        out.writeShort(this.cCol);
        out.writeShort(this.grbit);
        out.writeShort(this.itblAutoFmt);
        out.writeShort(this.name.length());
        out.writeShort(this.dataField.length());
        StringUtil.writeUnicodeStringFlagAndData(out, this.name);
        StringUtil.writeUnicodeStringFlagAndData(out, this.dataField);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return StringUtil.getEncodedSize(this.name) + 40 + StringUtil.getEncodedSize(this.dataField);
    }

    public short getSid() {
        return 176;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[SXVIEW]\n");
        buffer.append("    .rwFirst      =");
        buffer.append(HexDump.shortToHex(this.rwFirst));
        buffer.append(10);
        buffer.append("    .rwLast       =");
        buffer.append(HexDump.shortToHex(this.rwLast));
        buffer.append(10);
        buffer.append("    .colFirst     =");
        buffer.append(HexDump.shortToHex(this.colFirst));
        buffer.append(10);
        buffer.append("    .colLast      =");
        buffer.append(HexDump.shortToHex(this.colLast));
        buffer.append(10);
        buffer.append("    .rwFirstHead  =");
        buffer.append(HexDump.shortToHex(this.rwFirstHead));
        buffer.append(10);
        buffer.append("    .rwFirstData  =");
        buffer.append(HexDump.shortToHex(this.rwFirstData));
        buffer.append(10);
        buffer.append("    .colFirstData =");
        buffer.append(HexDump.shortToHex(this.colFirstData));
        buffer.append(10);
        buffer.append("    .iCache       =");
        buffer.append(HexDump.shortToHex(this.iCache));
        buffer.append(10);
        buffer.append("    .reserved     =");
        buffer.append(HexDump.shortToHex(this.reserved));
        buffer.append(10);
        buffer.append("    .sxaxis4Data  =");
        buffer.append(HexDump.shortToHex(this.sxaxis4Data));
        buffer.append(10);
        buffer.append("    .ipos4Data    =");
        buffer.append(HexDump.shortToHex(this.ipos4Data));
        buffer.append(10);
        buffer.append("    .cDim         =");
        buffer.append(HexDump.shortToHex(this.cDim));
        buffer.append(10);
        buffer.append("    .cDimRw       =");
        buffer.append(HexDump.shortToHex(this.cDimRw));
        buffer.append(10);
        buffer.append("    .cDimCol      =");
        buffer.append(HexDump.shortToHex(this.cDimCol));
        buffer.append(10);
        buffer.append("    .cDimPg       =");
        buffer.append(HexDump.shortToHex(this.cDimPg));
        buffer.append(10);
        buffer.append("    .cDimData     =");
        buffer.append(HexDump.shortToHex(this.cDimData));
        buffer.append(10);
        buffer.append("    .cRw          =");
        buffer.append(HexDump.shortToHex(this.cRw));
        buffer.append(10);
        buffer.append("    .cCol         =");
        buffer.append(HexDump.shortToHex(this.cCol));
        buffer.append(10);
        buffer.append("    .grbit        =");
        buffer.append(HexDump.shortToHex(this.grbit));
        buffer.append(10);
        buffer.append("    .itblAutoFmt  =");
        buffer.append(HexDump.shortToHex(this.itblAutoFmt));
        buffer.append(10);
        buffer.append("    .name         =");
        buffer.append(this.name);
        buffer.append(10);
        buffer.append("    .dataField    =");
        buffer.append(this.dataField);
        buffer.append(10);
        buffer.append("[/SXVIEW]\n");
        return buffer.toString();
    }
}
