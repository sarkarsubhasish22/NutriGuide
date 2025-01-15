package org.apache.poi.hssf.usermodel;

import androidx.core.view.MotionEventCompat;
import org.apache.poi.ss.usermodel.ClientAnchor;

public final class HSSFClientAnchor extends HSSFAnchor implements ClientAnchor {
    int anchorType;
    short col1;
    short col2;
    int row1;
    int row2;

    public HSSFClientAnchor() {
    }

    public HSSFClientAnchor(int dx1, int dy1, int dx2, int dy2, short col12, int row12, short col22, int row22) {
        super(dx1, dy1, dx2, dy2);
        checkRange(dx1, 0, IEEEDouble.EXPONENT_BIAS, "dx1");
        checkRange(dx2, 0, IEEEDouble.EXPONENT_BIAS, "dx2");
        checkRange(dy1, 0, 255, "dy1");
        checkRange(dy2, 0, 255, "dy2");
        checkRange(col12, 0, 255, "col1");
        checkRange(col22, 0, 255, "col2");
        checkRange(row12, 0, MotionEventCompat.ACTION_POINTER_INDEX_MASK, "row1");
        checkRange(row22, 0, MotionEventCompat.ACTION_POINTER_INDEX_MASK, "row2");
        this.col1 = col12;
        this.row1 = row12;
        this.col2 = col22;
        this.row2 = row22;
    }

    public float getAnchorHeightInPoints(HSSFSheet sheet) {
        int y1 = getDy1();
        int y2 = getDy2();
        int row12 = Math.min(getRow1(), getRow2());
        int row22 = Math.max(getRow1(), getRow2());
        if (row12 == row22) {
            return (((float) (y2 - y1)) / 256.0f) * getRowHeightInPoints(sheet, row22);
        }
        float points = 0.0f + (((256.0f - ((float) y1)) / 256.0f) * getRowHeightInPoints(sheet, row12));
        for (int i = row12 + 1; i < row22; i++) {
            points += getRowHeightInPoints(sheet, i);
        }
        return ((((float) y2) / 256.0f) * getRowHeightInPoints(sheet, row22)) + points;
    }

    private float getRowHeightInPoints(HSSFSheet sheet, int rowNum) {
        HSSFRow row = sheet.getRow(rowNum);
        if (row == null) {
            return sheet.getDefaultRowHeightInPoints();
        }
        return row.getHeightInPoints();
    }

    public short getCol1() {
        return this.col1;
    }

    public void setCol1(short col12) {
        checkRange(col12, 0, 255, "col1");
        this.col1 = col12;
    }

    public void setCol1(int col12) {
        setCol1((short) col12);
    }

    public short getCol2() {
        return this.col2;
    }

    public void setCol2(short col22) {
        checkRange(col22, 0, 255, "col2");
        this.col2 = col22;
    }

    public void setCol2(int col22) {
        setCol2((short) col22);
    }

    public int getRow1() {
        return this.row1;
    }

    public void setRow1(int row12) {
        checkRange(row12, 0, 65536, "row1");
        this.row1 = row12;
    }

    public int getRow2() {
        return this.row2;
    }

    public void setRow2(int row22) {
        checkRange(row22, 0, 65536, "row2");
        this.row2 = row22;
    }

    public void setAnchor(short col12, int row12, int x1, int y1, short col22, int row22, int x2, int y2) {
        checkRange(this.dx1, 0, IEEEDouble.EXPONENT_BIAS, "dx1");
        checkRange(this.dx2, 0, IEEEDouble.EXPONENT_BIAS, "dx2");
        checkRange(this.dy1, 0, 255, "dy1");
        checkRange(this.dy2, 0, 255, "dy2");
        checkRange(col12, 0, 255, "col1");
        checkRange(col22, 0, 255, "col2");
        checkRange(row12, 0, MotionEventCompat.ACTION_POINTER_INDEX_MASK, "row1");
        checkRange(row22, 0, MotionEventCompat.ACTION_POINTER_INDEX_MASK, "row2");
        this.col1 = col12;
        this.row1 = row12;
        this.dx1 = x1;
        this.dy1 = y1;
        this.col2 = col22;
        this.row2 = row22;
        this.dx2 = x2;
        this.dy2 = y2;
    }

    public boolean isHorizontallyFlipped() {
        short s = this.col1;
        short s2 = this.col2;
        if (s == s2) {
            if (this.dx1 > this.dx2) {
                return true;
            }
            return false;
        } else if (s > s2) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isVerticallyFlipped() {
        int i = this.row1;
        int i2 = this.row2;
        if (i == i2) {
            if (this.dy1 > this.dy2) {
                return true;
            }
            return false;
        } else if (i > i2) {
            return true;
        } else {
            return false;
        }
    }

    public int getAnchorType() {
        return this.anchorType;
    }

    public void setAnchorType(int anchorType2) {
        this.anchorType = anchorType2;
    }

    private void checkRange(int value, int minRange, int maxRange, String varName) {
        if (value < minRange || value > maxRange) {
            throw new IllegalArgumentException(varName + " must be between " + minRange + " and " + maxRange);
        }
    }
}
