package org.apache.poi.hssf.usermodel;

import androidx.appcompat.widget.ActivityChooserView;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

public class EscherGraphics extends Graphics {
    private static POILogger logger = POILogFactory.getLogger(EscherGraphics.class);
    private Color background = Color.white;
    private HSSFShapeGroup escherGroup;
    private Font font;
    private Color foreground;
    private float verticalPixelsPerPoint;
    private float verticalPointsPerPixel = 1.0f;
    private HSSFWorkbook workbook;

    public EscherGraphics(HSSFShapeGroup escherGroup2, HSSFWorkbook workbook2, Color forecolor, float verticalPointsPerPixel2) {
        this.escherGroup = escherGroup2;
        this.workbook = workbook2;
        this.verticalPointsPerPixel = verticalPointsPerPixel2;
        this.verticalPixelsPerPoint = 1.0f / verticalPointsPerPixel2;
        this.font = new Font(HSSFFont.FONT_ARIAL, 0, 10);
        this.foreground = forecolor;
    }

    EscherGraphics(HSSFShapeGroup escherGroup2, HSSFWorkbook workbook2, Color foreground2, Font font2, float verticalPointsPerPixel2) {
        this.escherGroup = escherGroup2;
        this.workbook = workbook2;
        this.foreground = foreground2;
        this.font = font2;
        this.verticalPointsPerPixel = verticalPointsPerPixel2;
        this.verticalPixelsPerPoint = 1.0f / verticalPointsPerPixel2;
    }

    public void clearRect(int x, int y, int width, int height) {
        Color color = this.foreground;
        setColor(this.background);
        fillRect(x, y, width, height);
        setColor(color);
    }

    public void clipRect(int x, int y, int width, int height) {
        if (logger.check(POILogger.WARN)) {
            logger.log(POILogger.WARN, (Object) "clipRect not supported");
        }
    }

    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        if (logger.check(POILogger.WARN)) {
            logger.log(POILogger.WARN, (Object) "copyArea not supported");
        }
    }

    public Graphics create() {
        return new EscherGraphics(this.escherGroup, this.workbook, this.foreground, this.font, this.verticalPointsPerPixel);
    }

    public void dispose() {
    }

    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        if (logger.check(POILogger.WARN)) {
            logger.log(POILogger.WARN, (Object) "drawArc not supported");
        }
    }

    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
        if (!logger.check(POILogger.WARN)) {
            return true;
        }
        logger.log(POILogger.WARN, (Object) "drawImage not supported");
        return true;
    }

    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        if (!logger.check(POILogger.WARN)) {
            return true;
        }
        logger.log(POILogger.WARN, (Object) "drawImage not supported");
        return true;
    }

    public boolean drawImage(Image image, int i, int j, int k, int l, Color color, ImageObserver imageobserver) {
        Image image2 = image;
        ImageObserver imageObserver = imageobserver;
        return drawImage(image, i, j, i + k, j + l, 0, 0, image.getWidth(imageObserver), image.getHeight(imageObserver), color, imageobserver);
    }

    public boolean drawImage(Image image, int i, int j, int k, int l, ImageObserver imageobserver) {
        Image image2 = image;
        ImageObserver imageObserver = imageobserver;
        return drawImage(image, i, j, i + k, j + l, 0, 0, image.getWidth(imageObserver), image.getHeight(imageObserver), imageobserver);
    }

    public boolean drawImage(Image image, int i, int j, Color color, ImageObserver imageobserver) {
        return drawImage(image, i, j, image.getWidth(imageobserver), image.getHeight(imageobserver), color, imageobserver);
    }

    public boolean drawImage(Image image, int i, int j, ImageObserver imageobserver) {
        return drawImage(image, i, j, image.getWidth(imageobserver), image.getHeight(imageobserver), imageobserver);
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        drawLine(x1, y1, x2, y2, 0);
    }

    public void drawLine(int x1, int y1, int x2, int y2, int width) {
        HSSFSimpleShape shape = this.escherGroup.createShape(new HSSFChildAnchor(x1, y1, x2, y2));
        shape.setShapeType(1);
        shape.setLineWidth(width);
        shape.setLineStyleColor(this.foreground.getRed(), this.foreground.getGreen(), this.foreground.getBlue());
    }

    public void drawOval(int x, int y, int width, int height) {
        HSSFSimpleShape shape = this.escherGroup.createShape(new HSSFChildAnchor(x, y, x + width, y + height));
        shape.setShapeType(3);
        shape.setLineWidth(0);
        shape.setLineStyleColor(this.foreground.getRed(), this.foreground.getGreen(), this.foreground.getBlue());
        shape.setNoFill(true);
    }

    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        int right = findBiggest(xPoints);
        int bottom = findBiggest(yPoints);
        int left = findSmallest(xPoints);
        int top = findSmallest(yPoints);
        HSSFPolygon shape = this.escherGroup.createPolygon(new HSSFChildAnchor(left, top, right, bottom));
        shape.setPolygonDrawArea(right - left, bottom - top);
        shape.setPoints(addToAll(xPoints, -left), addToAll(yPoints, -top));
        shape.setLineStyleColor(this.foreground.getRed(), this.foreground.getGreen(), this.foreground.getBlue());
        shape.setLineWidth(0);
        shape.setNoFill(true);
    }

    private int[] addToAll(int[] values, int amount) {
        int[] result = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = values[i] + amount;
        }
        return result;
    }

    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        if (logger.check(POILogger.WARN)) {
            logger.log(POILogger.WARN, (Object) "drawPolyline not supported");
        }
    }

    public void drawRect(int x, int y, int width, int height) {
        if (logger.check(POILogger.WARN)) {
            logger.log(POILogger.WARN, (Object) "drawRect not supported");
        }
    }

    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        if (logger.check(POILogger.WARN)) {
            logger.log(POILogger.WARN, (Object) "drawRoundRect not supported");
        }
    }

    public void drawString(String str, int x, int y) {
        Font excelFont;
        if (str != null && !str.equals("")) {
            Font font2 = this.font;
            if (this.font.getName().equals("SansSerif")) {
                excelFont = new Font(HSSFFont.FONT_ARIAL, this.font.getStyle(), (int) (((float) this.font.getSize()) / this.verticalPixelsPerPoint));
            } else {
                excelFont = new Font(this.font.getName(), this.font.getStyle(), (int) (((float) this.font.getSize()) / this.verticalPixelsPerPoint));
            }
            float f = this.verticalPixelsPerPoint;
            int y2 = (int) (((float) y) - ((((float) this.font.getSize()) / f) + (f * 2.0f)));
            HSSFTextbox textbox = this.escherGroup.createTextbox(new HSSFChildAnchor(x, y2, x + (StaticFontMetrics.getFontDetails(excelFont).getStringWidth(str) * 8) + 12, y2 + (((int) ((((float) this.font.getSize()) / this.verticalPixelsPerPoint) + 6.0f)) * 2)));
            textbox.setNoFill(true);
            textbox.setLineStyle(-1);
            HSSFRichTextString s = new HSSFRichTextString(str);
            s.applyFont((org.apache.poi.ss.usermodel.Font) matchFont(excelFont));
            textbox.setString(s);
        }
    }

    private HSSFFont matchFont(Font font2) {
        HSSFColor hssfColor = this.workbook.getCustomPalette().findColor((byte) this.foreground.getRed(), (byte) this.foreground.getGreen(), (byte) this.foreground.getBlue());
        if (hssfColor == null) {
            hssfColor = this.workbook.getCustomPalette().findSimilarColor((byte) this.foreground.getRed(), (byte) this.foreground.getGreen(), (byte) this.foreground.getBlue());
        }
        boolean italic = true;
        boolean bold = (font2.getStyle() & 1) != 0;
        if ((font2.getStyle() & 2) == 0) {
            italic = false;
        }
        short s = 700;
        HSSFFont hssfFont = this.workbook.findFont(bold ? (short) 700 : 0, hssfColor.getIndex(), (short) (font2.getSize() * 20), font2.getName(), italic, false, 0, (byte) 0);
        if (hssfFont == null) {
            hssfFont = this.workbook.createFont();
            if (!bold) {
                s = 0;
            }
            hssfFont.setBoldweight(s);
            hssfFont.setColor(hssfColor.getIndex());
            hssfFont.setFontHeight((short) (font2.getSize() * 20));
            hssfFont.setFontName(font2.getName());
            hssfFont.setItalic(italic);
            hssfFont.setStrikeout(false);
            hssfFont.setTypeOffset(0);
            hssfFont.setUnderline((byte) 0);
        }
        return hssfFont;
    }

    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        if (logger.check(POILogger.WARN)) {
            logger.log(POILogger.WARN, (Object) "drawString not supported");
        }
    }

    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        if (logger.check(POILogger.WARN)) {
            logger.log(POILogger.WARN, (Object) "fillArc not supported");
        }
    }

    public void fillOval(int x, int y, int width, int height) {
        HSSFSimpleShape shape = this.escherGroup.createShape(new HSSFChildAnchor(x, y, x + width, y + height));
        shape.setShapeType(3);
        shape.setLineStyle(-1);
        shape.setFillColor(this.foreground.getRed(), this.foreground.getGreen(), this.foreground.getBlue());
        shape.setLineStyleColor(this.foreground.getRed(), this.foreground.getGreen(), this.foreground.getBlue());
    }

    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        int right = findBiggest(xPoints);
        int bottom = findBiggest(yPoints);
        int left = findSmallest(xPoints);
        int top = findSmallest(yPoints);
        HSSFPolygon shape = this.escherGroup.createPolygon(new HSSFChildAnchor(left, top, right, bottom));
        shape.setPolygonDrawArea(right - left, bottom - top);
        shape.setPoints(addToAll(xPoints, -left), addToAll(yPoints, -top));
        shape.setLineStyleColor(this.foreground.getRed(), this.foreground.getGreen(), this.foreground.getBlue());
        shape.setFillColor(this.foreground.getRed(), this.foreground.getGreen(), this.foreground.getBlue());
    }

    private int findBiggest(int[] values) {
        int result = Integer.MIN_VALUE;
        for (int i = 0; i < values.length; i++) {
            if (values[i] > result) {
                result = values[i];
            }
        }
        return result;
    }

    private int findSmallest(int[] values) {
        int result = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        for (int i = 0; i < values.length; i++) {
            if (values[i] < result) {
                result = values[i];
            }
        }
        return result;
    }

    public void fillRect(int x, int y, int width, int height) {
        HSSFSimpleShape shape = this.escherGroup.createShape(new HSSFChildAnchor(x, y, x + width, y + height));
        shape.setShapeType(2);
        shape.setLineStyle(-1);
        shape.setFillColor(this.foreground.getRed(), this.foreground.getGreen(), this.foreground.getBlue());
        shape.setLineStyleColor(this.foreground.getRed(), this.foreground.getGreen(), this.foreground.getBlue());
    }

    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        if (logger.check(POILogger.WARN)) {
            logger.log(POILogger.WARN, (Object) "fillRoundRect not supported");
        }
    }

    public Shape getClip() {
        return getClipBounds();
    }

    public Rectangle getClipBounds() {
        return null;
    }

    public Rectangle getClipRect() {
        return getClipBounds();
    }

    public Color getColor() {
        return this.foreground;
    }

    public Font getFont() {
        return this.font;
    }

    public FontMetrics getFontMetrics(Font f) {
        return Toolkit.getDefaultToolkit().getFontMetrics(f);
    }

    public void setClip(int x, int y, int width, int height) {
        setClip(new Rectangle(x, y, width, height));
    }

    public void setClip(Shape shape) {
    }

    public void setColor(Color color) {
        this.foreground = color;
    }

    public void setFont(Font f) {
        this.font = f;
    }

    public void setPaintMode() {
        if (logger.check(POILogger.WARN)) {
            logger.log(POILogger.WARN, (Object) "setPaintMode not supported");
        }
    }

    public void setXORMode(Color color) {
        if (logger.check(POILogger.WARN)) {
            logger.log(POILogger.WARN, (Object) "setXORMode not supported");
        }
    }

    public void translate(int x, int y) {
        if (logger.check(POILogger.WARN)) {
            logger.log(POILogger.WARN, (Object) "translate not supported");
        }
    }

    public Color getBackground() {
        return this.background;
    }

    public void setBackground(Color background2) {
        this.background = background2;
    }

    /* access modifiers changed from: package-private */
    public HSSFShapeGroup getEscherGraphics() {
        return this.escherGroup;
    }
}
