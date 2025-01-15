package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.CFRuleRecord;
import org.apache.poi.hssf.record.cf.PatternFormatting;

public class HSSFPatternFormatting {
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
    private final CFRuleRecord cfRuleRecord;
    private final PatternFormatting patternFormatting;

    protected HSSFPatternFormatting(CFRuleRecord cfRuleRecord2) {
        this.cfRuleRecord = cfRuleRecord2;
        this.patternFormatting = cfRuleRecord2.getPatternFormatting();
    }

    /* access modifiers changed from: protected */
    public PatternFormatting getPatternFormattingBlock() {
        return this.patternFormatting;
    }

    public short getFillBackgroundColor() {
        return (short) this.patternFormatting.getFillBackgroundColor();
    }

    public short getFillForegroundColor() {
        return (short) this.patternFormatting.getFillForegroundColor();
    }

    public short getFillPattern() {
        return (short) this.patternFormatting.getFillPattern();
    }

    public void setFillBackgroundColor(short bg) {
        this.patternFormatting.setFillBackgroundColor(bg);
        if (bg != 0) {
            this.cfRuleRecord.setPatternBackgroundColorModified(true);
        }
    }

    public void setFillForegroundColor(short fg) {
        this.patternFormatting.setFillForegroundColor(fg);
        if (fg != 0) {
            this.cfRuleRecord.setPatternColorModified(true);
        }
    }

    public void setFillPattern(short fp) {
        this.patternFormatting.setFillPattern(fp);
        if (fp != 0) {
            this.cfRuleRecord.setPatternStyleModified(true);
        }
    }
}
