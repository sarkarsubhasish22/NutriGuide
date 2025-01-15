package org.apache.poi.hssf.usermodel;

public abstract class HSSFShape {
    public static final int LINESTYLE_DASHDOTDOTSYS = 4;
    public static final int LINESTYLE_DASHDOTGEL = 8;
    public static final int LINESTYLE_DASHDOTSYS = 3;
    public static final int LINESTYLE_DASHGEL = 6;
    public static final int LINESTYLE_DASHSYS = 1;
    public static final int LINESTYLE_DOTGEL = 5;
    public static final int LINESTYLE_DOTSYS = 2;
    public static final int LINESTYLE_LONGDASHDOTDOTGEL = 10;
    public static final int LINESTYLE_LONGDASHDOTGEL = 9;
    public static final int LINESTYLE_LONGDASHGEL = 7;
    public static final int LINESTYLE_NONE = -1;
    public static final int LINESTYLE_SOLID = 0;
    public static final int LINEWIDTH_DEFAULT = 9525;
    public static final int LINEWIDTH_ONE_PT = 12700;
    int _fillColor = 134217737;
    private int _lineStyle = 0;
    private int _lineStyleColor = 134217792;
    private int _lineWidth = LINEWIDTH_DEFAULT;
    private boolean _noFill = false;
    HSSFAnchor anchor;
    final HSSFShape parent;

    HSSFShape(HSSFShape parent2, HSSFAnchor anchor2) {
        this.parent = parent2;
        this.anchor = anchor2;
    }

    public HSSFShape getParent() {
        return this.parent;
    }

    public HSSFAnchor getAnchor() {
        return this.anchor;
    }

    public void setAnchor(HSSFAnchor anchor2) {
        if (this.parent == null) {
            if (anchor2 instanceof HSSFChildAnchor) {
                throw new IllegalArgumentException("Must use client anchors for shapes directly attached to sheet.");
            }
        } else if (anchor2 instanceof HSSFClientAnchor) {
            throw new IllegalArgumentException("Must use child anchors for shapes attached to groups.");
        }
        this.anchor = anchor2;
    }

    public int getLineStyleColor() {
        return this._lineStyleColor;
    }

    public void setLineStyleColor(int lineStyleColor) {
        this._lineStyleColor = lineStyleColor;
    }

    public void setLineStyleColor(int red, int green, int blue) {
        this._lineStyleColor = (blue << 16) | (green << 8) | red;
    }

    public int getFillColor() {
        return this._fillColor;
    }

    public void setFillColor(int fillColor) {
        this._fillColor = fillColor;
    }

    public void setFillColor(int red, int green, int blue) {
        this._fillColor = (blue << 16) | (green << 8) | red;
    }

    public int getLineWidth() {
        return this._lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this._lineWidth = lineWidth;
    }

    public int getLineStyle() {
        return this._lineStyle;
    }

    public void setLineStyle(int lineStyle) {
        this._lineStyle = lineStyle;
    }

    public boolean isNoFill() {
        return this._noFill;
    }

    public void setNoFill(boolean noFill) {
        this._noFill = noFill;
    }

    public int countOfAllChildren() {
        return 1;
    }
}
