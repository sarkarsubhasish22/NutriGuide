package org.apache.poi.hssf.record;

import java.io.ByteArrayInputStream;
import org.apache.poi.hssf.record.formula.Area3DPtg;
import org.apache.poi.hssf.record.formula.AreaPtg;
import org.apache.poi.hssf.record.formula.MissingArgPtg;
import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.hssf.record.formula.Ref3DPtg;
import org.apache.poi.hssf.record.formula.RefPtg;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianInputStream;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.StringUtil;

public final class EmbeddedObjectRefSubRecord extends SubRecord {
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    public static final short sid = 9;
    private int field_1_unknown_int;
    private Ptg field_2_refPtg;
    private byte[] field_2_unknownFormulaData;
    private boolean field_3_unicode_flag;
    private String field_4_ole_classname;
    private Byte field_4_unknownByte;
    private Integer field_5_stream_id;
    private byte[] field_6_unknown;

    EmbeddedObjectRefSubRecord() {
        this.field_2_unknownFormulaData = new byte[]{2, 108, 106, MissingArgPtg.sid, 1};
        this.field_6_unknown = EMPTY_BYTE_ARRAY;
        this.field_4_ole_classname = null;
    }

    public short getSid() {
        return 9;
    }

    public EmbeddedObjectRefSubRecord(LittleEndianInput in, int size) {
        int stringByteCount;
        int remaining = size - 2;
        int dataLenAfterFormula = remaining - in.readShort();
        int formulaSize = in.readUShort();
        this.field_1_unknown_int = in.readInt();
        byte[] formulaRawBytes = readRawData(in, formulaSize);
        int remaining2 = ((remaining - 2) - 4) - formulaSize;
        Ptg readRefPtg = readRefPtg(formulaRawBytes);
        this.field_2_refPtg = readRefPtg;
        if (readRefPtg == null) {
            this.field_2_unknownFormulaData = formulaRawBytes;
        } else {
            this.field_2_unknownFormulaData = null;
        }
        if (remaining2 < dataLenAfterFormula + 3) {
            this.field_4_ole_classname = null;
            stringByteCount = 0;
        } else if (in.readByte() == 3) {
            int nChars = in.readUShort();
            stringByteCount = 1 + 2;
            if (nChars > 0) {
                boolean z = (in.readByte() & 1) == 0 ? false : true;
                this.field_3_unicode_flag = z;
                int stringByteCount2 = stringByteCount + 1;
                if (z) {
                    this.field_4_ole_classname = StringUtil.readUnicodeLE(in, nChars);
                    stringByteCount = stringByteCount2 + (nChars * 2);
                } else {
                    this.field_4_ole_classname = StringUtil.readCompressedUnicode(in, nChars);
                    stringByteCount = stringByteCount2 + nChars;
                }
            } else {
                this.field_4_ole_classname = "";
            }
        } else {
            throw new RecordFormatException("Expected byte 0x03 here");
        }
        int remaining3 = remaining2 - stringByteCount;
        if ((stringByteCount + formulaSize) % 2 != 0) {
            int b = in.readByte();
            remaining3--;
            if (this.field_2_refPtg != null && this.field_4_ole_classname == null) {
                this.field_4_unknownByte = Byte.valueOf((byte) b);
            }
        }
        int nUnexpectedPadding = remaining3 - dataLenAfterFormula;
        if (nUnexpectedPadding > 0) {
            System.err.println("Discarding " + nUnexpectedPadding + " unexpected padding bytes ");
            readRawData(in, nUnexpectedPadding);
            remaining3 -= nUnexpectedPadding;
        }
        if (dataLenAfterFormula >= 4) {
            this.field_5_stream_id = Integer.valueOf(in.readInt());
            remaining3 -= 4;
        } else {
            this.field_5_stream_id = null;
        }
        this.field_6_unknown = readRawData(in, remaining3);
    }

    private static Ptg readRefPtg(byte[] formulaRawBytes) {
        LittleEndianInput in = new LittleEndianInputStream(new ByteArrayInputStream(formulaRawBytes));
        byte ptgSid = in.readByte();
        if (ptgSid == 36) {
            return new RefPtg(in);
        }
        if (ptgSid == 37) {
            return new AreaPtg(in);
        }
        if (ptgSid == 58) {
            return new Ref3DPtg(in);
        }
        if (ptgSid != 59) {
            return null;
        }
        return new Area3DPtg(in);
    }

    private static byte[] readRawData(LittleEndianInput in, int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Negative size (" + size + ")");
        } else if (size == 0) {
            return EMPTY_BYTE_ARRAY;
        } else {
            byte[] result = new byte[size];
            in.readFully(result);
            return result;
        }
    }

    private int getStreamIDOffset(int formulaSize) {
        int result = 6 + formulaSize;
        String str = this.field_4_ole_classname;
        if (str != null) {
            result += 3;
            int stringLen = str.length();
            if (stringLen > 0) {
                int result2 = result + 1;
                if (this.field_3_unicode_flag) {
                    result = result2 + (stringLen * 2);
                } else {
                    result = result2 + stringLen;
                }
            }
        }
        if (result % 2 != 0) {
            return result + 1;
        }
        return result;
    }

    private int getDataSize(int idOffset) {
        int result = idOffset + 2;
        if (this.field_5_stream_id != null) {
            result += 4;
        }
        return this.field_6_unknown.length + result;
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        Ptg ptg = this.field_2_refPtg;
        return getDataSize(getStreamIDOffset(ptg == null ? this.field_2_unknownFormulaData.length : ptg.getSize()));
    }

    public void serialize(LittleEndianOutput out) {
        Ptg ptg = this.field_2_refPtg;
        int formulaSize = ptg == null ? this.field_2_unknownFormulaData.length : ptg.getSize();
        int idOffset = getStreamIDOffset(formulaSize);
        int dataSize = getDataSize(idOffset);
        out.writeShort(9);
        out.writeShort(dataSize);
        out.writeShort(idOffset);
        out.writeShort(formulaSize);
        out.writeInt(this.field_1_unknown_int);
        Ptg ptg2 = this.field_2_refPtg;
        if (ptg2 == null) {
            out.write(this.field_2_unknownFormulaData);
        } else {
            ptg2.write(out);
        }
        int pos = 12 + formulaSize;
        if (this.field_4_ole_classname != null) {
            out.writeByte(3);
            int stringLen = this.field_4_ole_classname.length();
            out.writeShort(stringLen);
            pos = pos + 1 + 2;
            if (stringLen > 0) {
                out.writeByte(this.field_3_unicode_flag ? 1 : 0);
                int pos2 = pos + 1;
                if (this.field_3_unicode_flag) {
                    StringUtil.putUnicodeLE(this.field_4_ole_classname, out);
                    pos = pos2 + (stringLen * 2);
                } else {
                    StringUtil.putCompressedUnicode(this.field_4_ole_classname, out);
                    pos = pos2 + stringLen;
                }
            }
        }
        int i = idOffset - (pos - 6);
        if (i != 0) {
            if (i == 1) {
                Byte b = this.field_4_unknownByte;
                out.writeByte(b == null ? 0 : b.intValue());
                pos++;
            } else {
                throw new IllegalStateException("Bad padding calculation (" + idOffset + ", " + pos + ")");
            }
        }
        Integer num = this.field_5_stream_id;
        if (num != null) {
            out.writeInt(num.intValue());
            int pos3 = pos + 4;
        }
        out.write(this.field_6_unknown);
    }

    public Integer getStreamId() {
        return this.field_5_stream_id;
    }

    public String getOLEClassName() {
        return this.field_4_ole_classname;
    }

    public byte[] getObjectData() {
        return this.field_6_unknown;
    }

    public Object clone() {
        return this;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[ftPictFmla]\n");
        sb.append("    .f2unknown     = ");
        sb.append(HexDump.intToHex(this.field_1_unknown_int));
        sb.append("\n");
        if (this.field_2_refPtg == null) {
            sb.append("    .f3unknown     = ");
            sb.append(HexDump.toHex(this.field_2_unknownFormulaData));
            sb.append("\n");
        } else {
            sb.append("    .formula       = ");
            sb.append(this.field_2_refPtg.toString());
            sb.append("\n");
        }
        if (this.field_4_ole_classname != null) {
            sb.append("    .unicodeFlag   = ");
            sb.append(this.field_3_unicode_flag);
            sb.append("\n");
            sb.append("    .oleClassname  = ");
            sb.append(this.field_4_ole_classname);
            sb.append("\n");
        }
        if (this.field_4_unknownByte != null) {
            sb.append("    .f4unknown   = ");
            sb.append(HexDump.byteToHex(this.field_4_unknownByte.intValue()));
            sb.append("\n");
        }
        if (this.field_5_stream_id != null) {
            sb.append("    .streamId      = ");
            sb.append(HexDump.intToHex(this.field_5_stream_id.intValue()));
            sb.append("\n");
        }
        if (this.field_6_unknown.length > 0) {
            sb.append("    .f7unknown     = ");
            sb.append(HexDump.toHex(this.field_6_unknown));
            sb.append("\n");
        }
        sb.append("[/ftPictFmla]");
        return sb.toString();
    }
}
