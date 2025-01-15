package org.apache.poi.hssf.record.cf;

import org.apache.poi.ss.usermodel.ShapeTypes;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

public final class PatternFormatting implements Cloneable {
    public static final short ALT_BARS = 3;
    public static final short BIG_SPOTS = 9;
    public static final short BRICKS = 10;
    public static final short DIAMONDS = 16;
    public static final short FINE_DOTS = 2;
    public static final short LEAST_DOTS = 18;
    public static final short LESS_DOTS = 17;
    public static final short NO_FILL = 0;
    public static final short SOLID_FOREGROUND = 1;
    public static final short SPARSE_DOTS = 4;
    public static final short SQUARES = 15;
    public static final short THICK_BACKWARD_DIAG = 7;
    public static final short THICK_FORWARD_DIAG = 8;
    public static final short THICK_HORZ_BANDS = 5;
    public static final short THICK_VERT_BANDS = 6;
    public static final short THIN_BACKWARD_DIAG = 13;
    public static final short THIN_FORWARD_DIAG = 14;
    public static final short THIN_HORZ_BANDS = 11;
    public static final short THIN_VERT_BANDS = 12;
    private static final BitField fillPatternStyle = BitFieldFactory.getInstance(64512);
    private static final BitField patternBackgroundColorIndex = BitFieldFactory.getInstance(16256);
    private static final BitField patternColorIndex = BitFieldFactory.getInstance(ShapeTypes.VERTICAL_SCROLL);
    private int field_15_pattern_style;
    private int field_16_pattern_color_indexes;

    public PatternFormatting() {
        this.field_15_pattern_style = 0;
        this.field_16_pattern_color_indexes = 0;
    }

    public PatternFormatting(LittleEndianInput in) {
        this.field_15_pattern_style = in.readUShort();
        this.field_16_pattern_color_indexes = in.readUShort();
    }

    public void setFillPattern(int fp) {
        this.field_15_pattern_style = fillPatternStyle.setValue(this.field_15_pattern_style, fp);
    }

    public int getFillPattern() {
        return fillPatternStyle.getValue(this.field_15_pattern_style);
    }

    public void setFillBackgroundColor(int bg) {
        this.field_16_pattern_color_indexes = patternBackgroundColorIndex.setValue(this.field_16_pattern_color_indexes, bg);
    }

    public int getFillBackgroundColor() {
        return patternBackgroundColorIndex.getValue(this.field_16_pattern_color_indexes);
    }

    public void setFillForegroundColor(int fg) {
        this.field_16_pattern_color_indexes = patternColorIndex.setValue(this.field_16_pattern_color_indexes, fg);
    }

    public int getFillForegroundColor() {
        return patternColorIndex.getValue(this.field_16_pattern_color_indexes);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("    [Pattern Formatting]\n");
        buffer.append("          .fillpattern= ");
        buffer.append(Integer.toHexString(getFillPattern()));
        buffer.append("\n");
        buffer.append("          .fgcoloridx= ");
        buffer.append(Integer.toHexString(getFillForegroundColor()));
        buffer.append("\n");
        buffer.append("          .bgcoloridx= ");
        buffer.append(Integer.toHexString(getFillBackgroundColor()));
        buffer.append("\n");
        buffer.append("    [/Pattern Formatting]\n");
        return buffer.toString();
    }

    public Object clone() {
        PatternFormatting rec = new PatternFormatting();
        rec.field_15_pattern_style = this.field_15_pattern_style;
        rec.field_16_pattern_color_indexes = this.field_16_pattern_color_indexes;
        return rec;
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_15_pattern_style);
        out.writeShort(this.field_16_pattern_color_indexes);
    }
}
