package org.apache.poi.hssf.usermodel;

public class HSSFPolygon extends HSSFShape {
    int drawAreaHeight = 100;
    int drawAreaWidth = 100;
    int[] xPoints;
    int[] yPoints;

    HSSFPolygon(HSSFShape parent, HSSFAnchor anchor) {
        super(parent, anchor);
    }

    public int[] getXPoints() {
        return this.xPoints;
    }

    public int[] getYPoints() {
        return this.yPoints;
    }

    public void setPoints(int[] xPoints2, int[] yPoints2) {
        this.xPoints = cloneArray(xPoints2);
        this.yPoints = cloneArray(yPoints2);
    }

    private int[] cloneArray(int[] a) {
        int[] result = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i];
        }
        return result;
    }

    public void setPolygonDrawArea(int width, int height) {
        this.drawAreaWidth = width;
        this.drawAreaHeight = height;
    }

    public int getDrawAreaWidth() {
        return this.drawAreaWidth;
    }

    public int getDrawAreaHeight() {
        return this.drawAreaHeight;
    }
}
