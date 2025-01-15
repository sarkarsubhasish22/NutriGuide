package org.apache.poi.hssf.record;

import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class RowRecord extends StandardRecord {
    private static final int DEFAULT_HEIGHT_BIT = 32768;
    public static final int ENCODED_SIZE = 20;
    private static final int OPTION_BITS_ALWAYS_SET = 256;
    private static final BitField badFontHeight = BitFieldFactory.getInstance(64);
    private static final BitField colapsed = BitFieldFactory.getInstance(16);
    private static final BitField formatted = BitFieldFactory.getInstance(128);
    private static final BitField outlineLevel = BitFieldFactory.getInstance(7);
    public static final short sid = 520;
    private static final BitField zeroHeight = BitFieldFactory.getInstance(32);
    private int field_1_row_number;
    private int field_2_first_col;
    private int field_3_last_col;
    private short field_4_height;
    private short field_5_optimize;
    private short field_6_reserved;
    private int field_7_option_flags;
    private short field_8_xf_index;

    public RowRecord(int rowNumber) {
        this.field_1_row_number = rowNumber;
        this.field_4_height = 255;
        this.field_5_optimize = 0;
        this.field_6_reserved = 0;
        this.field_7_option_flags = 256;
        this.field_8_xf_index = 15;
        setEmpty();
    }

    public RowRecord(RecordInputStream in) {
        this.field_1_row_number = in.readUShort();
        this.field_2_first_col = in.readShort();
        this.field_3_last_col = in.readShort();
        this.field_4_height = in.readShort();
        this.field_5_optimize = in.readShort();
        this.field_6_reserved = in.readShort();
        this.field_7_option_flags = in.readShort();
        this.field_8_xf_index = in.readShort();
    }

    public void setEmpty() {
        this.field_2_first_col = 0;
        this.field_3_last_col = 0;
    }

    public boolean isEmpty() {
        return (this.field_2_first_col | this.field_3_last_col) == 0;
    }

    public void setRowNumber(int row) {
        this.field_1_row_number = row;
    }

    public void setFirstCol(int col) {
        this.field_2_first_col = col;
    }

    public void setLastCol(int col) {
        this.field_3_last_col = col;
    }

    public void setHeight(short height) {
        this.field_4_height = height;
    }

    public void setOptimize(short optimize) {
        this.field_5_optimize = optimize;
    }

    public void setOutlineLevel(short ol) {
        this.field_7_option_flags = outlineLevel.setValue(this.field_7_option_flags, ol);
    }

    public void setColapsed(boolean c) {
        this.field_7_option_flags = colapsed.setBoolean(this.field_7_option_flags, c);
    }

    public void setZeroHeight(boolean z) {
        this.field_7_option_flags = zeroHeight.setBoolean(this.field_7_option_flags, z);
    }

    public void setBadFontHeight(boolean f) {
        this.field_7_option_flags = badFontHeight.setBoolean(this.field_7_option_flags, f);
    }

    public void setFormatted(boolean f) {
        this.field_7_option_flags = formatted.setBoolean(this.field_7_option_flags, f);
    }

    public void setXFIndex(short index) {
        this.field_8_xf_index = index;
    }

    public int getRowNumber() {
        return this.field_1_row_number;
    }

    public int getFirstCol() {
        return this.field_2_first_col;
    }

    public int getLastCol() {
        return this.field_3_last_col;
    }

    public short getHeight() {
        return this.field_4_height;
    }

    public short getOptimize() {
        return this.field_5_optimize;
    }

    public short getOptionFlags() {
        return (short) this.field_7_option_flags;
    }

    public short getOutlineLevel() {
        return (short) outlineLevel.getValue(this.field_7_option_flags);
    }

    public boolean getColapsed() {
        return colapsed.isSet(this.field_7_option_flags);
    }

    public boolean getZeroHeight() {
        return zeroHeight.isSet(this.field_7_option_flags);
    }

    public boolean getBadFontHeight() {
        return badFontHeight.isSet(this.field_7_option_flags);
    }

    public boolean getFormatted() {
        return formatted.isSet(this.field_7_option_flags);
    }

    public short getXFIndex() {
        return this.field_8_xf_index;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[ROW]\n");
        sb.append("    .rownumber      = ");
        sb.append(Integer.toHexString(getRowNumber()));
        sb.append("\n");
        sb.append("    .firstcol       = ");
        sb.append(HexDump.shortToHex(getFirstCol()));
        sb.append("\n");
        sb.append("    .lastcol        = ");
        sb.append(HexDump.shortToHex(getLastCol()));
        sb.append("\n");
        sb.append("    .height         = ");
        sb.append(HexDump.shortToHex(getHeight()));
        sb.append("\n");
        sb.append("    .optimize       = ");
        sb.append(HexDump.shortToHex(getOptimize()));
        sb.append("\n");
        sb.append("    .reserved       = ");
        sb.append(HexDump.shortToHex(this.field_6_reserved));
        sb.append("\n");
        sb.append("    .optionflags    = ");
        sb.append(HexDump.shortToHex(getOptionFlags()));
        sb.append("\n");
        sb.append("        .outlinelvl = ");
        sb.append(Integer.toHexString(getOutlineLevel()));
        sb.append("\n");
        sb.append("        .colapsed   = ");
        sb.append(getColapsed());
        sb.append("\n");
        sb.append("        .zeroheight = ");
        sb.append(getZeroHeight());
        sb.append("\n");
        sb.append("        .badfontheig= ");
        sb.append(getBadFontHeight());
        sb.append("\n");
        sb.append("        .formatted  = ");
        sb.append(getFormatted());
        sb.append("\n");
        sb.append("    .xfindex        = ");
        sb.append(Integer.toHexString(getXFIndex()));
        sb.append("\n");
        sb.append("[/ROW]\n");
        return sb.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(getRowNumber());
        int i = 0;
        out.writeShort(getFirstCol() == -1 ? 0 : getFirstCol());
        if (getLastCol() != -1) {
            i = getLastCol();
        }
        out.writeShort(i);
        out.writeShort(getHeight());
        out.writeShort(getOptimize());
        out.writeShort(this.field_6_reserved);
        out.writeShort(getOptionFlags());
        out.writeShort(getXFIndex());
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 16;
    }

    public short getSid() {
        return 520;
    }

    public Object clone() {
        RowRecord rec = new RowRecord(this.field_1_row_number);
        rec.field_2_first_col = this.field_2_first_col;
        rec.field_3_last_col = this.field_3_last_col;
        rec.field_4_height = this.field_4_height;
        rec.field_5_optimize = this.field_5_optimize;
        rec.field_6_reserved = this.field_6_reserved;
        rec.field_7_option_flags = this.field_7_option_flags;
        rec.field_8_xf_index = this.field_8_xf_index;
        return rec;
    }
}
