package org.apache.poi.hssf.usermodel;

import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import org.apache.poi.ddf.EscherBSERecord;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.util.ImageUtils;

public final class HSSFPicture extends HSSFSimpleShape implements Picture {
    public static final int PICTURE_TYPE_DIB = 7;
    public static final int PICTURE_TYPE_EMF = 2;
    public static final int PICTURE_TYPE_JPEG = 5;
    public static final int PICTURE_TYPE_PICT = 4;
    public static final int PICTURE_TYPE_PNG = 6;
    public static final int PICTURE_TYPE_WMF = 3;
    private static final float PX_DEFAULT = 32.0f;
    private static final float PX_MODIFIED = 36.56f;
    private static final int PX_ROW = 15;
    HSSFPatriarch _patriarch;
    private int _pictureIndex;

    public HSSFPicture(HSSFShape parent, HSSFAnchor anchor) {
        super(parent, anchor);
        setShapeType(8);
    }

    public int getPictureIndex() {
        return this._pictureIndex;
    }

    public void setPictureIndex(int pictureIndex) {
        this._pictureIndex = pictureIndex;
    }

    public void resize(double scale) {
        HSSFClientAnchor anchor = (HSSFClientAnchor) getAnchor();
        anchor.setAnchorType(2);
        HSSFClientAnchor pref = getPreferredSize(scale);
        int row2 = anchor.getRow1() + (pref.getRow2() - pref.getRow1());
        anchor.setCol2((short) (anchor.getCol1() + (pref.getCol2() - pref.getCol1())));
        anchor.setDx1(0);
        anchor.setDx2(pref.getDx2());
        anchor.setRow2(row2);
        anchor.setDy1(0);
        anchor.setDy2(pref.getDy2());
    }

    public void resize() {
        resize(1.0d);
    }

    public HSSFClientAnchor getPreferredSize() {
        return getPreferredSize(1.0d);
    }

    public HSSFClientAnchor getPreferredSize(double scale) {
        HSSFClientAnchor anchor = (HSSFClientAnchor) getAnchor();
        Dimension size = getImageDimension();
        double scaledWidth = size.getWidth() * scale;
        double scaledHeight = size.getHeight() * scale;
        float w = 0.0f + (getColumnWidthInPixels(anchor.col1) * (1.0f - (((float) anchor.dx1) / 1024.0f)));
        short col2 = (short) (anchor.col1 + 1);
        int dx2 = 0;
        while (((double) w) < scaledWidth) {
            w += getColumnWidthInPixels(col2);
            col2 = (short) (col2 + 1);
        }
        if (((double) w) > scaledWidth) {
            col2 = (short) (col2 - 1);
            double cw = (double) getColumnWidthInPixels(col2);
            double d = (double) w;
            Double.isNaN(d);
            Double.isNaN(cw);
            Double.isNaN(cw);
            double d2 = cw;
            dx2 = (int) (((cw - (d - scaledWidth)) / cw) * 1024.0d);
        }
        anchor.col2 = col2;
        anchor.dx2 = dx2;
        float h = 0.0f + ((1.0f - (((float) anchor.dy1) / 256.0f)) * getRowHeightInPixels(anchor.row1));
        int row2 = anchor.row1 + 1;
        int dy2 = 0;
        while (((double) h) < scaledHeight) {
            h += getRowHeightInPixels(row2);
            row2++;
        }
        if (((double) h) > scaledHeight) {
            row2--;
            double ch = (double) getRowHeightInPixels(row2);
            Dimension dimension = size;
            double d3 = scaledWidth;
            double d4 = (double) h;
            Double.isNaN(d4);
            double delta = d4 - scaledHeight;
            Double.isNaN(ch);
            Double.isNaN(ch);
            double d5 = delta;
            dy2 = (int) (((ch - delta) / ch) * 256.0d);
        } else {
            double d6 = scaledWidth;
        }
        anchor.row2 = row2;
        anchor.dy2 = dy2;
        return anchor;
    }

    private float getColumnWidthInPixels(int column) {
        return ((float) this._patriarch._sheet.getColumnWidth(column)) / getPixelWidth(column);
    }

    private float getRowHeightInPixels(int i) {
        float height;
        HSSFRow row = this._patriarch._sheet.getRow(i);
        if (row != null) {
            height = (float) row.getHeight();
        } else {
            height = (float) this._patriarch._sheet.getDefaultRowHeight();
        }
        return height / 15.0f;
    }

    private float getPixelWidth(int column) {
        return this._patriarch._sheet.getColumnWidth(column) == this._patriarch._sheet.getDefaultColumnWidth() * 256 ? PX_DEFAULT : PX_MODIFIED;
    }

    public Dimension getImageDimension() {
        EscherBSERecord bse = this._patriarch._sheet._book.getBSERecord(this._pictureIndex);
        byte[] data = bse.getBlipRecord().getPicturedata();
        return ImageUtils.getImageDimension(new ByteArrayInputStream(data), bse.getBlipTypeWin32());
    }
}
