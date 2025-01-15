package org.apache.poi.hssf.usermodel;

public abstract class HSSFAnchor {
    int dx1;
    int dx2;
    int dy1;
    int dy2;

    public abstract boolean isHorizontallyFlipped();

    public abstract boolean isVerticallyFlipped();

    public HSSFAnchor() {
    }

    public HSSFAnchor(int dx12, int dy12, int dx22, int dy22) {
        this.dx1 = dx12;
        this.dy1 = dy12;
        this.dx2 = dx22;
        this.dy2 = dy22;
    }

    public int getDx1() {
        return this.dx1;
    }

    public void setDx1(int dx12) {
        this.dx1 = dx12;
    }

    public int getDy1() {
        return this.dy1;
    }

    public void setDy1(int dy12) {
        this.dy1 = dy12;
    }

    public int getDy2() {
        return this.dy2;
    }

    public void setDy2(int dy22) {
        this.dy2 = dy22;
    }

    public int getDx2() {
        return this.dx2;
    }

    public void setDx2(int dx22) {
        this.dx2 = dx22;
    }
}
