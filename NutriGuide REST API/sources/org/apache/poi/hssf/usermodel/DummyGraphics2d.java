package org.apache.poi.hssf.usermodel;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.PrintStream;
import java.text.AttributedCharacterIterator;
import java.util.Map;

public class DummyGraphics2d extends Graphics2D {
    private Graphics2D g2D;
    BufferedImage img;

    public DummyGraphics2d() {
        BufferedImage bufferedImage = new BufferedImage(1000, 1000, 2);
        this.img = bufferedImage;
        this.g2D = bufferedImage.getGraphics();
    }

    public void addRenderingHints(Map hints) {
        System.out.println("addRenderingHinds(Map):");
        PrintStream printStream = System.out;
        printStream.println("  hints = " + hints);
        this.g2D.addRenderingHints(hints);
    }

    public void clip(Shape s) {
        System.out.println("clip(Shape):");
        PrintStream printStream = System.out;
        printStream.println("  s = " + s);
        this.g2D.clip(s);
    }

    public void draw(Shape s) {
        System.out.println("draw(Shape):");
        PrintStream printStream = System.out;
        printStream.println("s = " + s);
        this.g2D.draw(s);
    }

    public void drawGlyphVector(GlyphVector g, float x, float y) {
        System.out.println("drawGlyphVector(GlyphVector, float, float):");
        PrintStream printStream = System.out;
        printStream.println("g = " + g);
        PrintStream printStream2 = System.out;
        printStream2.println("x = " + x);
        PrintStream printStream3 = System.out;
        printStream3.println("y = " + y);
        this.g2D.drawGlyphVector(g, x, y);
    }

    public void drawImage(BufferedImage img2, BufferedImageOp op, int x, int y) {
        System.out.println("drawImage(BufferedImage, BufferedImageOp, x, y):");
        PrintStream printStream = System.out;
        printStream.println("img = " + img2);
        PrintStream printStream2 = System.out;
        printStream2.println("op = " + op);
        PrintStream printStream3 = System.out;
        printStream3.println("x = " + x);
        PrintStream printStream4 = System.out;
        printStream4.println("y = " + y);
        this.g2D.drawImage(img2, op, x, y);
    }

    public boolean drawImage(Image img2, AffineTransform xform, ImageObserver obs) {
        System.out.println("drawImage(Image,AfflineTransform,ImageObserver):");
        PrintStream printStream = System.out;
        printStream.println("img = " + img2);
        PrintStream printStream2 = System.out;
        printStream2.println("xform = " + xform);
        PrintStream printStream3 = System.out;
        printStream3.println("obs = " + obs);
        return this.g2D.drawImage(img2, xform, obs);
    }

    public void drawRenderableImage(RenderableImage img2, AffineTransform xform) {
        System.out.println("drawRenderableImage(RenderableImage, AfflineTransform):");
        PrintStream printStream = System.out;
        printStream.println("img = " + img2);
        PrintStream printStream2 = System.out;
        printStream2.println("xform = " + xform);
        this.g2D.drawRenderableImage(img2, xform);
    }

    public void drawRenderedImage(RenderedImage img2, AffineTransform xform) {
        System.out.println("drawRenderedImage(RenderedImage, AffineTransform):");
        PrintStream printStream = System.out;
        printStream.println("img = " + img2);
        PrintStream printStream2 = System.out;
        printStream2.println("xform = " + xform);
        this.g2D.drawRenderedImage(img2, xform);
    }

    public void drawString(AttributedCharacterIterator iterator, float x, float y) {
        System.out.println("drawString(AttributedCharacterIterator):");
        PrintStream printStream = System.out;
        printStream.println("iterator = " + iterator);
        PrintStream printStream2 = System.out;
        printStream2.println("x = " + x);
        PrintStream printStream3 = System.out;
        printStream3.println("y = " + y);
        this.g2D.drawString(iterator, x, y);
    }

    public void drawString(String s, float x, float y) {
        System.out.println("drawString(s,x,y):");
        PrintStream printStream = System.out;
        printStream.println("s = " + s);
        PrintStream printStream2 = System.out;
        printStream2.println("x = " + x);
        PrintStream printStream3 = System.out;
        printStream3.println("y = " + y);
        this.g2D.drawString(s, x, y);
    }

    public void fill(Shape s) {
        System.out.println("fill(Shape):");
        PrintStream printStream = System.out;
        printStream.println("s = " + s);
        this.g2D.fill(s);
    }

    public Color getBackground() {
        System.out.println("getBackground():");
        return this.g2D.getBackground();
    }

    public Composite getComposite() {
        System.out.println("getComposite():");
        return this.g2D.getComposite();
    }

    public GraphicsConfiguration getDeviceConfiguration() {
        System.out.println("getDeviceConfiguration():");
        return this.g2D.getDeviceConfiguration();
    }

    public FontRenderContext getFontRenderContext() {
        System.out.println("getFontRenderContext():");
        return this.g2D.getFontRenderContext();
    }

    public Paint getPaint() {
        System.out.println("getPaint():");
        return this.g2D.getPaint();
    }

    public Object getRenderingHint(RenderingHints.Key hintKey) {
        System.out.println("getRenderingHint(RenderingHints.Key):");
        PrintStream printStream = System.out;
        printStream.println("hintKey = " + hintKey);
        return this.g2D.getRenderingHint(hintKey);
    }

    public RenderingHints getRenderingHints() {
        System.out.println("getRenderingHints():");
        return this.g2D.getRenderingHints();
    }

    public Stroke getStroke() {
        System.out.println("getStroke():");
        return this.g2D.getStroke();
    }

    public AffineTransform getTransform() {
        System.out.println("getTransform():");
        return this.g2D.getTransform();
    }

    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        System.out.println("hit(Rectangle, Shape, onStroke):");
        PrintStream printStream = System.out;
        printStream.println("rect = " + rect);
        PrintStream printStream2 = System.out;
        printStream2.println("s = " + s);
        PrintStream printStream3 = System.out;
        printStream3.println("onStroke = " + onStroke);
        return this.g2D.hit(rect, s, onStroke);
    }

    public void rotate(double theta) {
        System.out.println("rotate(theta):");
        PrintStream printStream = System.out;
        printStream.println("theta = " + theta);
        this.g2D.rotate(theta);
    }

    public void rotate(double theta, double x, double y) {
        System.out.println("rotate(double,double,double):");
        PrintStream printStream = System.out;
        printStream.println("theta = " + theta);
        PrintStream printStream2 = System.out;
        printStream2.println("x = " + x);
        PrintStream printStream3 = System.out;
        printStream3.println("y = " + y);
        this.g2D.rotate(theta, x, y);
    }

    public void scale(double sx, double sy) {
        System.out.println("scale(double,double):");
        PrintStream printStream = System.out;
        printStream.println("sx = " + sx);
        System.out.println("sy");
        this.g2D.scale(sx, sy);
    }

    public void setBackground(Color color) {
        System.out.println("setBackground(Color):");
        PrintStream printStream = System.out;
        printStream.println("color = " + color);
        this.g2D.setBackground(color);
    }

    public void setComposite(Composite comp) {
        System.out.println("setComposite(Composite):");
        PrintStream printStream = System.out;
        printStream.println("comp = " + comp);
        this.g2D.setComposite(comp);
    }

    public void setPaint(Paint paint) {
        System.out.println("setPain(Paint):");
        PrintStream printStream = System.out;
        printStream.println("paint = " + paint);
        this.g2D.setPaint(paint);
    }

    public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
        System.out.println("setRenderingHint(RenderingHints.Key, Object):");
        PrintStream printStream = System.out;
        printStream.println("hintKey = " + hintKey);
        PrintStream printStream2 = System.out;
        printStream2.println("hintValue = " + hintValue);
        this.g2D.setRenderingHint(hintKey, hintValue);
    }

    public void setRenderingHints(Map hints) {
        System.out.println("setRenderingHints(Map):");
        PrintStream printStream = System.out;
        printStream.println("hints = " + hints);
        this.g2D.setRenderingHints(hints);
    }

    public void setStroke(Stroke s) {
        System.out.println("setStroke(Stoke):");
        PrintStream printStream = System.out;
        printStream.println("s = " + s);
        this.g2D.setStroke(s);
    }

    public void setTransform(AffineTransform Tx) {
        System.out.println("setTransform():");
        PrintStream printStream = System.out;
        printStream.println("Tx = " + Tx);
        this.g2D.setTransform(Tx);
    }

    public void shear(double shx, double shy) {
        System.out.println("shear(shx, dhy):");
        PrintStream printStream = System.out;
        printStream.println("shx = " + shx);
        PrintStream printStream2 = System.out;
        printStream2.println("shy = " + shy);
        this.g2D.shear(shx, shy);
    }

    public void transform(AffineTransform Tx) {
        System.out.println("transform(AffineTransform):");
        PrintStream printStream = System.out;
        printStream.println("Tx = " + Tx);
        this.g2D.transform(Tx);
    }

    public void translate(double tx, double ty) {
        System.out.println("translate(double, double):");
        PrintStream printStream = System.out;
        printStream.println("tx = " + tx);
        PrintStream printStream2 = System.out;
        printStream2.println("ty = " + ty);
        this.g2D.translate(tx, ty);
    }

    public void clearRect(int x, int y, int width, int height) {
        System.out.println("clearRect(int,int,int,int):");
        PrintStream printStream = System.out;
        printStream.println("x = " + x);
        PrintStream printStream2 = System.out;
        printStream2.println("y = " + y);
        PrintStream printStream3 = System.out;
        printStream3.println("width = " + width);
        PrintStream printStream4 = System.out;
        printStream4.println("height = " + height);
        this.g2D.clearRect(x, y, width, height);
    }

    public void clipRect(int x, int y, int width, int height) {
        System.out.println("clipRect(int, int, int, int):");
        PrintStream printStream = System.out;
        printStream.println("x = " + x);
        PrintStream printStream2 = System.out;
        printStream2.println("y = " + y);
        PrintStream printStream3 = System.out;
        printStream3.println("width = " + width);
        PrintStream printStream4 = System.out;
        printStream4.println("height = " + height);
        this.g2D.clipRect(x, y, width, height);
    }

    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        System.out.println("copyArea(int,int,int,int):");
        PrintStream printStream = System.out;
        printStream.println("x = " + x);
        PrintStream printStream2 = System.out;
        printStream2.println("y = " + y);
        PrintStream printStream3 = System.out;
        printStream3.println("width = " + width);
        PrintStream printStream4 = System.out;
        printStream4.println("height = " + height);
        this.g2D.copyArea(x, y, width, height, dx, dy);
    }

    public Graphics create() {
        System.out.println("create():");
        return this.g2D.create();
    }

    public Graphics create(int x, int y, int width, int height) {
        System.out.println("create(int,int,int,int):");
        PrintStream printStream = System.out;
        printStream.println("x = " + x);
        PrintStream printStream2 = System.out;
        printStream2.println("y = " + y);
        PrintStream printStream3 = System.out;
        printStream3.println("width = " + width);
        PrintStream printStream4 = System.out;
        printStream4.println("height = " + height);
        return this.g2D.create(x, y, width, height);
    }

    public void dispose() {
        System.out.println("dispose():");
        this.g2D.dispose();
    }

    public void draw3DRect(int x, int y, int width, int height, boolean raised) {
        System.out.println("draw3DRect(int,int,int,int,boolean):");
        PrintStream printStream = System.out;
        printStream.println("x = " + x);
        PrintStream printStream2 = System.out;
        printStream2.println("y = " + y);
        PrintStream printStream3 = System.out;
        printStream3.println("width = " + width);
        PrintStream printStream4 = System.out;
        printStream4.println("height = " + height);
        PrintStream printStream5 = System.out;
        printStream5.println("raised = " + raised);
        this.g2D.draw3DRect(x, y, width, height, raised);
    }

    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        System.out.println("drawArc(int,int,int,int,int,int):");
        PrintStream printStream = System.out;
        printStream.println("x = " + x);
        PrintStream printStream2 = System.out;
        printStream2.println("y = " + y);
        PrintStream printStream3 = System.out;
        printStream3.println("width = " + width);
        PrintStream printStream4 = System.out;
        printStream4.println("height = " + height);
        PrintStream printStream5 = System.out;
        printStream5.println("startAngle = " + startAngle);
        PrintStream printStream6 = System.out;
        printStream6.println("arcAngle = " + arcAngle);
        this.g2D.drawArc(x, y, width, height, startAngle, arcAngle);
    }

    public void drawBytes(byte[] data, int offset, int length, int x, int y) {
        System.out.println("drawBytes(byte[],int,int,int,int):");
        PrintStream printStream = System.out;
        printStream.println("data = " + data);
        PrintStream printStream2 = System.out;
        printStream2.println("offset = " + offset);
        PrintStream printStream3 = System.out;
        printStream3.println("length = " + length);
        PrintStream printStream4 = System.out;
        printStream4.println("x = " + x);
        PrintStream printStream5 = System.out;
        printStream5.println("y = " + y);
        this.g2D.drawBytes(data, offset, length, x, y);
    }

    public void drawChars(char[] data, int offset, int length, int x, int y) {
        System.out.println("drawChars(data,int,int,int,int):");
        PrintStream printStream = System.out;
        printStream.println("data = " + data.toString());
        PrintStream printStream2 = System.out;
        printStream2.println("offset = " + offset);
        PrintStream printStream3 = System.out;
        printStream3.println("length = " + length);
        PrintStream printStream4 = System.out;
        printStream4.println("x = " + x);
        PrintStream printStream5 = System.out;
        printStream5.println("y = " + y);
        this.g2D.drawChars(data, offset, length, x, y);
    }

    public boolean drawImage(Image img2, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        System.out.println("drawImage(Image,int,int,int,int,int,int,int,int,ImageObserver):");
        PrintStream printStream = System.out;
        printStream.println("img = " + img2);
        PrintStream printStream2 = System.out;
        printStream2.println("dx1 = " + dx1);
        PrintStream printStream3 = System.out;
        printStream3.println("dy1 = " + dy1);
        PrintStream printStream4 = System.out;
        printStream4.println("dx2 = " + dx2);
        PrintStream printStream5 = System.out;
        printStream5.println("dy2 = " + dy2);
        PrintStream printStream6 = System.out;
        printStream6.println("sx1 = " + sx1);
        PrintStream printStream7 = System.out;
        StringBuilder sb = new StringBuilder();
        sb.append("sy1 = ");
        int i = sy1;
        sb.append(i);
        printStream7.println(sb.toString());
        PrintStream printStream8 = System.out;
        printStream8.println("sx2 = " + sx2);
        PrintStream printStream9 = System.out;
        printStream9.println("sy2 = " + sy2);
        PrintStream printStream10 = System.out;
        printStream10.println("observer = " + observer);
        return this.g2D.drawImage(img2, dx1, dy1, dx2, dy2, sx1, i, sx2, sy2, observer);
    }

    public boolean drawImage(Image img2, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
        System.out.println("drawImage(Image,int,int,int,int,int,int,int,int,Color,ImageObserver):");
        PrintStream printStream = System.out;
        printStream.println("img = " + img2);
        PrintStream printStream2 = System.out;
        printStream2.println("dx1 = " + dx1);
        PrintStream printStream3 = System.out;
        printStream3.println("dy1 = " + dy1);
        PrintStream printStream4 = System.out;
        printStream4.println("dx2 = " + dx2);
        PrintStream printStream5 = System.out;
        printStream5.println("dy2 = " + dy2);
        PrintStream printStream6 = System.out;
        printStream6.println("sx1 = " + sx1);
        PrintStream printStream7 = System.out;
        StringBuilder sb = new StringBuilder();
        sb.append("sy1 = ");
        int i = sy1;
        sb.append(i);
        printStream7.println(sb.toString());
        PrintStream printStream8 = System.out;
        printStream8.println("sx2 = " + sx2);
        PrintStream printStream9 = System.out;
        printStream9.println("sy2 = " + sy2);
        PrintStream printStream10 = System.out;
        printStream10.println("bgcolor = " + bgcolor);
        PrintStream printStream11 = System.out;
        printStream11.println("observer = " + observer);
        return this.g2D.drawImage(img2, dx1, dy1, dx2, dy2, sx1, i, sx2, sy2, bgcolor, observer);
    }

    public boolean drawImage(Image img2, int x, int y, Color bgcolor, ImageObserver observer) {
        System.out.println("drawImage(Image,int,int,Color,ImageObserver):");
        PrintStream printStream = System.out;
        printStream.println("img = " + img2);
        PrintStream printStream2 = System.out;
        printStream2.println("x = " + x);
        PrintStream printStream3 = System.out;
        printStream3.println("y = " + y);
        PrintStream printStream4 = System.out;
        printStream4.println("bgcolor = " + bgcolor);
        PrintStream printStream5 = System.out;
        printStream5.println("observer = " + observer);
        return this.g2D.drawImage(img2, x, y, bgcolor, observer);
    }

    public boolean drawImage(Image img2, int x, int y, ImageObserver observer) {
        System.out.println("drawImage(Image,int,int,observer):");
        PrintStream printStream = System.out;
        printStream.println("img = " + img2);
        PrintStream printStream2 = System.out;
        printStream2.println("x = " + x);
        PrintStream printStream3 = System.out;
        printStream3.println("y = " + y);
        PrintStream printStream4 = System.out;
        printStream4.println("observer = " + observer);
        return this.g2D.drawImage(img2, x, y, observer);
    }

    public boolean drawImage(Image img2, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
        System.out.println("drawImage(Image,int,int,int,int,Color,ImageObserver):");
        PrintStream printStream = System.out;
        printStream.println("img = " + img2);
        PrintStream printStream2 = System.out;
        printStream2.println("x = " + x);
        PrintStream printStream3 = System.out;
        printStream3.println("y = " + y);
        PrintStream printStream4 = System.out;
        printStream4.println("width = " + width);
        PrintStream printStream5 = System.out;
        printStream5.println("height = " + height);
        PrintStream printStream6 = System.out;
        printStream6.println("bgcolor = " + bgcolor);
        PrintStream printStream7 = System.out;
        StringBuilder sb = new StringBuilder();
        sb.append("observer = ");
        ImageObserver imageObserver = observer;
        sb.append(imageObserver);
        printStream7.println(sb.toString());
        return this.g2D.drawImage(img2, x, y, width, height, bgcolor, imageObserver);
    }

    public boolean drawImage(Image img2, int x, int y, int width, int height, ImageObserver observer) {
        System.out.println("drawImage(Image,int,int,width,height,observer):");
        PrintStream printStream = System.out;
        printStream.println("img = " + img2);
        PrintStream printStream2 = System.out;
        printStream2.println("x = " + x);
        PrintStream printStream3 = System.out;
        printStream3.println("y = " + y);
        PrintStream printStream4 = System.out;
        printStream4.println("width = " + width);
        PrintStream printStream5 = System.out;
        printStream5.println("height = " + height);
        PrintStream printStream6 = System.out;
        printStream6.println("observer = " + observer);
        return this.g2D.drawImage(img2, x, y, width, height, observer);
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        System.out.println("drawLine(int,int,int,int):");
        PrintStream printStream = System.out;
        printStream.println("x1 = " + x1);
        PrintStream printStream2 = System.out;
        printStream2.println("y1 = " + y1);
        PrintStream printStream3 = System.out;
        printStream3.println("x2 = " + x2);
        PrintStream printStream4 = System.out;
        printStream4.println("y2 = " + y2);
        this.g2D.drawLine(x1, y1, x2, y2);
    }

    public void drawOval(int x, int y, int width, int height) {
        System.out.println("drawOval(int,int,int,int):");
        PrintStream printStream = System.out;
        printStream.println("x = " + x);
        PrintStream printStream2 = System.out;
        printStream2.println("y = " + y);
        PrintStream printStream3 = System.out;
        printStream3.println("width = " + width);
        PrintStream printStream4 = System.out;
        printStream4.println("height = " + height);
        this.g2D.drawOval(x, y, width, height);
    }

    public void drawPolygon(Polygon p) {
        System.out.println("drawPolygon(Polygon):");
        PrintStream printStream = System.out;
        printStream.println("p = " + p);
        this.g2D.drawPolygon(p);
    }

    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        System.out.println("drawPolygon(int[],int[],int):");
        PrintStream printStream = System.out;
        printStream.println("xPoints = " + xPoints);
        PrintStream printStream2 = System.out;
        printStream2.println("yPoints = " + yPoints);
        PrintStream printStream3 = System.out;
        printStream3.println("nPoints = " + nPoints);
        this.g2D.drawPolygon(xPoints, yPoints, nPoints);
    }

    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        System.out.println("drawPolyline(int[],int[],int):");
        PrintStream printStream = System.out;
        printStream.println("xPoints = " + xPoints);
        PrintStream printStream2 = System.out;
        printStream2.println("yPoints = " + yPoints);
        PrintStream printStream3 = System.out;
        printStream3.println("nPoints = " + nPoints);
        this.g2D.drawPolyline(xPoints, yPoints, nPoints);
    }

    public void drawRect(int x, int y, int width, int height) {
        System.out.println("drawRect(int,int,int,int):");
        PrintStream printStream = System.out;
        printStream.println("x = " + x);
        PrintStream printStream2 = System.out;
        printStream2.println("y = " + y);
        PrintStream printStream3 = System.out;
        printStream3.println("width = " + width);
        PrintStream printStream4 = System.out;
        printStream4.println("height = " + height);
        this.g2D.drawRect(x, y, width, height);
    }

    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        System.out.println("drawRoundRect(int,int,int,int,int,int):");
        PrintStream printStream = System.out;
        printStream.println("x = " + x);
        PrintStream printStream2 = System.out;
        printStream2.println("y = " + y);
        PrintStream printStream3 = System.out;
        printStream3.println("width = " + width);
        PrintStream printStream4 = System.out;
        printStream4.println("height = " + height);
        PrintStream printStream5 = System.out;
        printStream5.println("arcWidth = " + arcWidth);
        PrintStream printStream6 = System.out;
        printStream6.println("arcHeight = " + arcHeight);
        this.g2D.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        System.out.println("drawString(AttributedCharacterIterator,int,int):");
        PrintStream printStream = System.out;
        printStream.println("iterator = " + iterator);
        PrintStream printStream2 = System.out;
        printStream2.println("x = " + x);
        PrintStream printStream3 = System.out;
        printStream3.println("y = " + y);
        this.g2D.drawString(iterator, x, y);
    }

    public void drawString(String str, int x, int y) {
        System.out.println("drawString(str,int,int):");
        PrintStream printStream = System.out;
        printStream.println("str = " + str);
        PrintStream printStream2 = System.out;
        printStream2.println("x = " + x);
        PrintStream printStream3 = System.out;
        printStream3.println("y = " + y);
        this.g2D.drawString(str, x, y);
    }

    public void fill3DRect(int x, int y, int width, int height, boolean raised) {
        System.out.println("fill3DRect(int,int,int,int,boolean):");
        PrintStream printStream = System.out;
        printStream.println("x = " + x);
        PrintStream printStream2 = System.out;
        printStream2.println("y = " + y);
        PrintStream printStream3 = System.out;
        printStream3.println("width = " + width);
        PrintStream printStream4 = System.out;
        printStream4.println("height = " + height);
        PrintStream printStream5 = System.out;
        printStream5.println("raised = " + raised);
        this.g2D.fill3DRect(x, y, width, height, raised);
    }

    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        System.out.println("fillArc(int,int,int,int,int,int):");
        PrintStream printStream = System.out;
        printStream.println("x = " + x);
        PrintStream printStream2 = System.out;
        printStream2.println("y = " + y);
        PrintStream printStream3 = System.out;
        printStream3.println("width = " + width);
        PrintStream printStream4 = System.out;
        printStream4.println("height = " + height);
        PrintStream printStream5 = System.out;
        printStream5.println("startAngle = " + startAngle);
        PrintStream printStream6 = System.out;
        printStream6.println("arcAngle = " + arcAngle);
        this.g2D.fillArc(x, y, width, height, startAngle, arcAngle);
    }

    public void fillOval(int x, int y, int width, int height) {
        System.out.println("fillOval(int,int,int,int):");
        PrintStream printStream = System.out;
        printStream.println("x = " + x);
        PrintStream printStream2 = System.out;
        printStream2.println("y = " + y);
        PrintStream printStream3 = System.out;
        printStream3.println("width = " + width);
        PrintStream printStream4 = System.out;
        printStream4.println("height = " + height);
        this.g2D.fillOval(x, y, width, height);
    }

    public void fillPolygon(Polygon p) {
        System.out.println("fillPolygon(Polygon):");
        PrintStream printStream = System.out;
        printStream.println("p = " + p);
        this.g2D.fillPolygon(p);
    }

    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        System.out.println("fillPolygon(int[],int[],int):");
        PrintStream printStream = System.out;
        printStream.println("xPoints = " + xPoints);
        PrintStream printStream2 = System.out;
        printStream2.println("yPoints = " + yPoints);
        PrintStream printStream3 = System.out;
        printStream3.println("nPoints = " + nPoints);
        this.g2D.fillPolygon(xPoints, yPoints, nPoints);
    }

    public void fillRect(int x, int y, int width, int height) {
        System.out.println("fillRect(int,int,int,int):");
        PrintStream printStream = System.out;
        printStream.println("x = " + x);
        PrintStream printStream2 = System.out;
        printStream2.println("y = " + y);
        PrintStream printStream3 = System.out;
        printStream3.println("width = " + width);
        PrintStream printStream4 = System.out;
        printStream4.println("height = " + height);
        this.g2D.fillRect(x, y, width, height);
    }

    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        System.out.println("fillRoundRect(int,int,int,int,int,int):");
        PrintStream printStream = System.out;
        printStream.println("x = " + x);
        PrintStream printStream2 = System.out;
        printStream2.println("y = " + y);
        PrintStream printStream3 = System.out;
        printStream3.println("width = " + width);
        PrintStream printStream4 = System.out;
        printStream4.println("height = " + height);
        this.g2D.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    public void finalize() {
        System.out.println("finalize():");
        this.g2D.finalize();
    }

    public Shape getClip() {
        System.out.println("getClip():");
        return this.g2D.getClip();
    }

    public Rectangle getClipBounds() {
        System.out.println("getClipBounds():");
        return this.g2D.getClipBounds();
    }

    public Rectangle getClipBounds(Rectangle r) {
        System.out.println("getClipBounds(Rectangle):");
        PrintStream printStream = System.out;
        printStream.println("r = " + r);
        return this.g2D.getClipBounds(r);
    }

    public Rectangle getClipRect() {
        System.out.println("getClipRect():");
        return this.g2D.getClipRect();
    }

    public Color getColor() {
        System.out.println("getColor():");
        return this.g2D.getColor();
    }

    public Font getFont() {
        System.out.println("getFont():");
        return this.g2D.getFont();
    }

    public FontMetrics getFontMetrics() {
        System.out.println("getFontMetrics():");
        return this.g2D.getFontMetrics();
    }

    public FontMetrics getFontMetrics(Font f) {
        System.out.println("getFontMetrics():");
        return this.g2D.getFontMetrics(f);
    }

    public boolean hitClip(int x, int y, int width, int height) {
        System.out.println("hitClip(int,int,int,int):");
        PrintStream printStream = System.out;
        printStream.println("x = " + x);
        PrintStream printStream2 = System.out;
        printStream2.println("y = " + y);
        PrintStream printStream3 = System.out;
        printStream3.println("width = " + width);
        PrintStream printStream4 = System.out;
        printStream4.println("height = " + height);
        return this.g2D.hitClip(x, y, width, height);
    }

    public void setClip(Shape clip) {
        System.out.println("setClip(Shape):");
        PrintStream printStream = System.out;
        printStream.println("clip = " + clip);
        this.g2D.setClip(clip);
    }

    public void setClip(int x, int y, int width, int height) {
        System.out.println("setClip(int,int,int,int):");
        PrintStream printStream = System.out;
        printStream.println("x = " + x);
        PrintStream printStream2 = System.out;
        printStream2.println("y = " + y);
        PrintStream printStream3 = System.out;
        printStream3.println("width = " + width);
        PrintStream printStream4 = System.out;
        printStream4.println("height = " + height);
        this.g2D.setClip(x, y, width, height);
    }

    public void setColor(Color c) {
        System.out.println("setColor():");
        PrintStream printStream = System.out;
        printStream.println("c = " + c);
        this.g2D.setColor(c);
    }

    public void setFont(Font font) {
        System.out.println("setFont(Font):");
        PrintStream printStream = System.out;
        printStream.println("font = " + font);
        this.g2D.setFont(font);
    }

    public void setPaintMode() {
        System.out.println("setPaintMode():");
        this.g2D.setPaintMode();
    }

    public void setXORMode(Color c1) {
        System.out.println("setXORMode(Color):");
        PrintStream printStream = System.out;
        printStream.println("c1 = " + c1);
        this.g2D.setXORMode(c1);
    }

    public String toString() {
        System.out.println("toString():");
        return this.g2D.toString();
    }

    public void translate(int x, int y) {
        System.out.println("translate(int,int):");
        PrintStream printStream = System.out;
        printStream.println("x = " + x);
        PrintStream printStream2 = System.out;
        printStream2.println("y = " + y);
        this.g2D.translate(x, y);
    }
}
