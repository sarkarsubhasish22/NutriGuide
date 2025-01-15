package org.apache.poi.hssf.record;

import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.StringUtil;

public final class StyleRecord extends StandardRecord {
    private static final BitField isBuiltinFlag = BitFieldFactory.getInstance(32768);
    public static final short sid = 659;
    private static final BitField styleIndexMask = BitFieldFactory.getInstance(4095);
    private int field_1_xf_index;
    private int field_2_builtin_style;
    private int field_3_outline_style_level;
    private boolean field_3_stringHasMultibyte;
    private String field_4_name;

    public StyleRecord() {
        this.field_1_xf_index = isBuiltinFlag.set(this.field_1_xf_index);
    }

    public StyleRecord(RecordInputStream in) {
        this.field_1_xf_index = in.readShort();
        if (isBuiltin()) {
            this.field_2_builtin_style = in.readByte();
            this.field_3_outline_style_level = in.readByte();
            return;
        }
        int field_2_name_length = in.readShort();
        boolean z = true;
        if (in.remaining() >= 1) {
            z = in.readByte() == 0 ? false : z;
            this.field_3_stringHasMultibyte = z;
            if (z) {
                this.field_4_name = StringUtil.readUnicodeLE(in, field_2_name_length);
            } else {
                this.field_4_name = StringUtil.readCompressedUnicode(in, field_2_name_length);
            }
        } else if (field_2_name_length == 0) {
            this.field_4_name = "";
        } else {
            throw new RecordFormatException("Ran out of data reading style record");
        }
    }

    public void setXFIndex(int xfIndex) {
        this.field_1_xf_index = styleIndexMask.setValue(this.field_1_xf_index, xfIndex);
    }

    public int getXFIndex() {
        return styleIndexMask.getValue(this.field_1_xf_index);
    }

    public void setName(String name) {
        this.field_4_name = name;
        this.field_3_stringHasMultibyte = StringUtil.hasMultibyte(name);
        this.field_1_xf_index = isBuiltinFlag.clear(this.field_1_xf_index);
    }

    public void setBuiltinStyle(int builtinStyleId) {
        this.field_1_xf_index = isBuiltinFlag.set(this.field_1_xf_index);
        this.field_2_builtin_style = builtinStyleId;
    }

    public void setOutlineStyleLevel(int level) {
        this.field_3_outline_style_level = level & 255;
    }

    public boolean isBuiltin() {
        return isBuiltinFlag.isSet(this.field_1_xf_index);
    }

    public String getName() {
        return this.field_4_name;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[STYLE]\n");
        sb.append("    .xf_index_raw =");
        sb.append(HexDump.shortToHex(this.field_1_xf_index));
        sb.append("\n");
        sb.append("        .type     =");
        sb.append(isBuiltin() ? "built-in" : "user-defined");
        sb.append("\n");
        sb.append("        .xf_index =");
        sb.append(HexDump.shortToHex(getXFIndex()));
        sb.append("\n");
        if (isBuiltin()) {
            sb.append("    .builtin_style=");
            sb.append(HexDump.byteToHex(this.field_2_builtin_style));
            sb.append("\n");
            sb.append("    .outline_level=");
            sb.append(HexDump.byteToHex(this.field_3_outline_style_level));
            sb.append("\n");
        } else {
            sb.append("    .name        =");
            sb.append(getName());
            sb.append("\n");
        }
        sb.append("[/STYLE]\n");
        return sb.toString();
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        if (isBuiltin()) {
            return 4;
        }
        return (this.field_4_name.length() * (this.field_3_stringHasMultibyte ? 2 : 1)) + 5;
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_xf_index);
        if (isBuiltin()) {
            out.writeByte(this.field_2_builtin_style);
            out.writeByte(this.field_3_outline_style_level);
            return;
        }
        out.writeShort(this.field_4_name.length());
        out.writeByte(this.field_3_stringHasMultibyte ? 1 : 0);
        if (this.field_3_stringHasMultibyte) {
            StringUtil.putUnicodeLE(getName(), out);
        } else {
            StringUtil.putCompressedUnicode(getName(), out);
        }
    }

    public short getSid() {
        return sid;
    }
}
