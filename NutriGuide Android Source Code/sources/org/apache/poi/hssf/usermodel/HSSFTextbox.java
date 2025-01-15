package org.apache.poi.hssf.usermodel;

import org.apache.poi.ss.usermodel.RichTextString;

public class HSSFTextbox extends HSSFSimpleShape {
    public static final short HORIZONTAL_ALIGNMENT_CENTERED = 2;
    public static final short HORIZONTAL_ALIGNMENT_DISTRIBUTED = 7;
    public static final short HORIZONTAL_ALIGNMENT_JUSTIFIED = 4;
    public static final short HORIZONTAL_ALIGNMENT_LEFT = 1;
    public static final short HORIZONTAL_ALIGNMENT_RIGHT = 3;
    public static final short OBJECT_TYPE_TEXT = 6;
    public static final short VERTICAL_ALIGNMENT_BOTTOM = 3;
    public static final short VERTICAL_ALIGNMENT_CENTER = 2;
    public static final short VERTICAL_ALIGNMENT_DISTRIBUTED = 7;
    public static final short VERTICAL_ALIGNMENT_JUSTIFY = 4;
    public static final short VERTICAL_ALIGNMENT_TOP = 1;
    short halign;
    int marginBottom;
    int marginLeft;
    int marginRight;
    int marginTop;
    HSSFRichTextString string = new HSSFRichTextString("");
    short valign;

    public HSSFTextbox(HSSFShape parent, HSSFAnchor anchor) {
        super(parent, anchor);
        setShapeType(6);
        this.halign = 1;
        this.valign = 1;
    }

    public HSSFRichTextString getString() {
        return this.string;
    }

    public void setString(RichTextString string2) {
        HSSFRichTextString rtr = (HSSFRichTextString) string2;
        if (rtr.numFormattingRuns() == 0) {
            rtr.applyFont(0);
        }
        this.string = rtr;
    }

    public int getMarginLeft() {
        return this.marginLeft;
    }

    public void setMarginLeft(int marginLeft2) {
        this.marginLeft = marginLeft2;
    }

    public int getMarginRight() {
        return this.marginRight;
    }

    public void setMarginRight(int marginRight2) {
        this.marginRight = marginRight2;
    }

    public int getMarginTop() {
        return this.marginTop;
    }

    public void setMarginTop(int marginTop2) {
        this.marginTop = marginTop2;
    }

    public int getMarginBottom() {
        return this.marginBottom;
    }

    public void setMarginBottom(int marginBottom2) {
        this.marginBottom = marginBottom2;
    }

    public short getHorizontalAlignment() {
        return this.halign;
    }

    public void setHorizontalAlignment(short align) {
        this.halign = align;
    }

    public short getVerticalAlignment() {
        return this.valign;
    }

    public void setVerticalAlignment(short align) {
        this.valign = align;
    }
}
