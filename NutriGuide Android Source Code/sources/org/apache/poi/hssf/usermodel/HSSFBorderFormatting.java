package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.CFRuleRecord;
import org.apache.poi.hssf.record.cf.BorderFormatting;

public final class HSSFBorderFormatting {
    public static final short BORDER_DASHED = 3;
    public static final short BORDER_DASH_DOT = 9;
    public static final short BORDER_DASH_DOT_DOT = 11;
    public static final short BORDER_DOTTED = 7;
    public static final short BORDER_DOUBLE = 6;
    public static final short BORDER_HAIR = 4;
    public static final short BORDER_MEDIUM = 2;
    public static final short BORDER_MEDIUM_DASHED = 8;
    public static final short BORDER_MEDIUM_DASH_DOT = 10;
    public static final short BORDER_MEDIUM_DASH_DOT_DOT = 12;
    public static final short BORDER_NONE = 0;
    public static final short BORDER_SLANTED_DASH_DOT = 13;
    public static final short BORDER_THICK = 5;
    public static final short BORDER_THIN = 1;
    private final BorderFormatting borderFormatting;
    private final CFRuleRecord cfRuleRecord;

    protected HSSFBorderFormatting(CFRuleRecord cfRuleRecord2) {
        this.cfRuleRecord = cfRuleRecord2;
        this.borderFormatting = cfRuleRecord2.getBorderFormatting();
    }

    /* access modifiers changed from: protected */
    public BorderFormatting getBorderFormattingBlock() {
        return this.borderFormatting;
    }

    public short getBorderBottom() {
        return (short) this.borderFormatting.getBorderBottom();
    }

    public short getBorderDiagonal() {
        return (short) this.borderFormatting.getBorderDiagonal();
    }

    public short getBorderLeft() {
        return (short) this.borderFormatting.getBorderLeft();
    }

    public short getBorderRight() {
        return (short) this.borderFormatting.getBorderRight();
    }

    public short getBorderTop() {
        return (short) this.borderFormatting.getBorderTop();
    }

    public short getBottomBorderColor() {
        return (short) this.borderFormatting.getBottomBorderColor();
    }

    public short getDiagonalBorderColor() {
        return (short) this.borderFormatting.getDiagonalBorderColor();
    }

    public short getLeftBorderColor() {
        return (short) this.borderFormatting.getLeftBorderColor();
    }

    public short getRightBorderColor() {
        return (short) this.borderFormatting.getRightBorderColor();
    }

    public short getTopBorderColor() {
        return (short) this.borderFormatting.getTopBorderColor();
    }

    public boolean isBackwardDiagonalOn() {
        return this.borderFormatting.isBackwardDiagonalOn();
    }

    public boolean isForwardDiagonalOn() {
        return this.borderFormatting.isForwardDiagonalOn();
    }

    public void setBackwardDiagonalOn(boolean on) {
        this.borderFormatting.setBackwardDiagonalOn(on);
        if (on) {
            this.cfRuleRecord.setTopLeftBottomRightBorderModified(on);
        }
    }

    public void setBorderBottom(short border) {
        this.borderFormatting.setBorderBottom(border);
        if (border != 0) {
            this.cfRuleRecord.setBottomBorderModified(true);
        }
    }

    public void setBorderDiagonal(short border) {
        this.borderFormatting.setBorderDiagonal(border);
        if (border != 0) {
            this.cfRuleRecord.setBottomLeftTopRightBorderModified(true);
            this.cfRuleRecord.setTopLeftBottomRightBorderModified(true);
        }
    }

    public void setBorderLeft(short border) {
        this.borderFormatting.setBorderLeft(border);
        if (border != 0) {
            this.cfRuleRecord.setLeftBorderModified(true);
        }
    }

    public void setBorderRight(short border) {
        this.borderFormatting.setBorderRight(border);
        if (border != 0) {
            this.cfRuleRecord.setRightBorderModified(true);
        }
    }

    public void setBorderTop(short border) {
        this.borderFormatting.setBorderTop(border);
        if (border != 0) {
            this.cfRuleRecord.setTopBorderModified(true);
        }
    }

    public void setBottomBorderColor(short color) {
        this.borderFormatting.setBottomBorderColor(color);
        if (color != 0) {
            this.cfRuleRecord.setBottomBorderModified(true);
        }
    }

    public void setDiagonalBorderColor(short color) {
        this.borderFormatting.setDiagonalBorderColor(color);
        if (color != 0) {
            this.cfRuleRecord.setBottomLeftTopRightBorderModified(true);
            this.cfRuleRecord.setTopLeftBottomRightBorderModified(true);
        }
    }

    public void setForwardDiagonalOn(boolean on) {
        this.borderFormatting.setForwardDiagonalOn(on);
        if (on) {
            this.cfRuleRecord.setBottomLeftTopRightBorderModified(on);
        }
    }

    public void setLeftBorderColor(short color) {
        this.borderFormatting.setLeftBorderColor(color);
        if (color != 0) {
            this.cfRuleRecord.setLeftBorderModified(true);
        }
    }

    public void setRightBorderColor(short color) {
        this.borderFormatting.setRightBorderColor(color);
        if (color != 0) {
            this.cfRuleRecord.setRightBorderModified(true);
        }
    }

    public void setTopBorderColor(short color) {
        this.borderFormatting.setTopBorderColor(color);
        if (color != 0) {
            this.cfRuleRecord.setTopBorderModified(true);
        }
    }
}
