package org.apache.poi.hssf.usermodel;

import java.util.ArrayList;
import java.util.List;

public class HSSFShapeGroup extends HSSFShape implements HSSFShapeContainer {
    List<HSSFShape> shapes = new ArrayList();
    int x1 = 0;
    int x2 = IEEEDouble.EXPONENT_BIAS;
    int y1 = 0;
    int y2 = 255;

    public HSSFShapeGroup(HSSFShape parent, HSSFAnchor anchor) {
        super(parent, anchor);
    }

    public HSSFShapeGroup createGroup(HSSFChildAnchor anchor) {
        HSSFShapeGroup group = new HSSFShapeGroup(this, anchor);
        group.anchor = anchor;
        this.shapes.add(group);
        return group;
    }

    public HSSFSimpleShape createShape(HSSFChildAnchor anchor) {
        HSSFSimpleShape shape = new HSSFSimpleShape(this, anchor);
        shape.anchor = anchor;
        this.shapes.add(shape);
        return shape;
    }

    public HSSFTextbox createTextbox(HSSFChildAnchor anchor) {
        HSSFTextbox shape = new HSSFTextbox(this, anchor);
        shape.anchor = anchor;
        this.shapes.add(shape);
        return shape;
    }

    public HSSFPolygon createPolygon(HSSFChildAnchor anchor) {
        HSSFPolygon shape = new HSSFPolygon(this, anchor);
        shape.anchor = anchor;
        this.shapes.add(shape);
        return shape;
    }

    public HSSFPicture createPicture(HSSFChildAnchor anchor, int pictureIndex) {
        HSSFPicture shape = new HSSFPicture(this, anchor);
        shape.anchor = anchor;
        shape.setPictureIndex(pictureIndex);
        this.shapes.add(shape);
        return shape;
    }

    public List<HSSFShape> getChildren() {
        return this.shapes;
    }

    public void setCoordinates(int x12, int y12, int x22, int y22) {
        this.x1 = x12;
        this.y1 = y12;
        this.x2 = x22;
        this.y2 = y22;
    }

    public int getX1() {
        return this.x1;
    }

    public int getY1() {
        return this.y1;
    }

    public int getX2() {
        return this.x2;
    }

    public int getY2() {
        return this.y2;
    }

    public int countOfAllChildren() {
        int count = this.shapes.size();
        for (HSSFShape shape : this.shapes) {
            count += shape.countOfAllChildren();
        }
        return count;
    }
}
