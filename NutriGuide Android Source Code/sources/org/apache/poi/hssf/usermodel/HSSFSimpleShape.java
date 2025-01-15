package org.apache.poi.hssf.usermodel;

public class HSSFSimpleShape extends HSSFShape {
    public static final short OBJECT_TYPE_COMBO_BOX = 20;
    public static final short OBJECT_TYPE_COMMENT = 25;
    public static final short OBJECT_TYPE_LINE = 1;
    public static final short OBJECT_TYPE_OVAL = 3;
    public static final short OBJECT_TYPE_PICTURE = 8;
    public static final short OBJECT_TYPE_RECTANGLE = 2;
    int shapeType = 1;

    HSSFSimpleShape(HSSFShape parent, HSSFAnchor anchor) {
        super(parent, anchor);
    }

    public int getShapeType() {
        return this.shapeType;
    }

    public void setShapeType(int shapeType2) {
        this.shapeType = shapeType2;
    }
}
