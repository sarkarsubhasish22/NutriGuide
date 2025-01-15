package org.apache.poi.hssf.util;

public class PaneInformation {
    public static final byte PANE_LOWER_LEFT = 2;
    public static final byte PANE_LOWER_RIGHT = 0;
    public static final byte PANE_UPPER_LEFT = 3;
    public static final byte PANE_UPPER_RIGHT = 1;
    private byte activePane;
    private boolean frozen = false;
    private short leftColumn;
    private short topRow;
    private short x;
    private short y;

    public PaneInformation(short x2, short y2, short top, short left, byte active, boolean frozen2) {
        this.x = x2;
        this.y = y2;
        this.topRow = top;
        this.leftColumn = left;
        this.activePane = active;
        this.frozen = frozen2;
    }

    public short getVerticalSplitPosition() {
        return this.x;
    }

    public short getHorizontalSplitPosition() {
        return this.y;
    }

    public short getHorizontalSplitTopRow() {
        return this.topRow;
    }

    public short getVerticalSplitLeftColumn() {
        return this.leftColumn;
    }

    public byte getActivePane() {
        return this.activePane;
    }

    public boolean isFreezePane() {
        return this.frozen;
    }
}
