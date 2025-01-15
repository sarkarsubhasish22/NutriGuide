package org.apache.poi.hssf.usermodel;

public final class HSSFChildAnchor extends HSSFAnchor {
    public HSSFChildAnchor() {
    }

    public HSSFChildAnchor(int dx1, int dy1, int dx2, int dy2) {
        super(dx1, dy1, dx2, dy2);
    }

    public void setAnchor(int dx1, int dy1, int dx2, int dy2) {
        this.dx1 = dx1;
        this.dy1 = dy1;
        this.dx2 = dx2;
        this.dy2 = dy2;
    }

    public boolean isHorizontallyFlipped() {
        return this.dx1 > this.dx2;
    }

    public boolean isVerticallyFlipped() {
        return this.dy1 > this.dy2;
    }
}
