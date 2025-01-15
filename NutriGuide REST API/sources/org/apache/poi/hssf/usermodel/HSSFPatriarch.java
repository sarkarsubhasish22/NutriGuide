package org.apache.poi.hssf.usermodel;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ddf.EscherComplexProperty;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.ddf.EscherProperty;
import org.apache.poi.hssf.record.EscherAggregate;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.util.StringUtil;

public final class HSSFPatriarch implements HSSFShapeContainer, Drawing {
    private EscherAggregate _boundAggregate;
    private final List<HSSFShape> _shapes = new ArrayList();
    final HSSFSheet _sheet;
    private int _x1 = 0;
    private int _x2 = IEEEDouble.EXPONENT_BIAS;
    private int _y1 = 0;
    private int _y2 = 255;

    HSSFPatriarch(HSSFSheet sheet, EscherAggregate boundAggregate) {
        this._sheet = sheet;
        this._boundAggregate = boundAggregate;
    }

    public HSSFShapeGroup createGroup(HSSFClientAnchor anchor) {
        HSSFShapeGroup group = new HSSFShapeGroup((HSSFShape) null, anchor);
        group.anchor = anchor;
        this._shapes.add(group);
        return group;
    }

    public HSSFSimpleShape createSimpleShape(HSSFClientAnchor anchor) {
        HSSFSimpleShape shape = new HSSFSimpleShape((HSSFShape) null, anchor);
        shape.anchor = anchor;
        this._shapes.add(shape);
        return shape;
    }

    public HSSFPicture createPicture(HSSFClientAnchor anchor, int pictureIndex) {
        HSSFPicture shape = new HSSFPicture((HSSFShape) null, anchor);
        shape.setPictureIndex(pictureIndex);
        shape.anchor = anchor;
        shape._patriarch = this;
        this._shapes.add(shape);
        return shape;
    }

    public HSSFPicture createPicture(ClientAnchor anchor, int pictureIndex) {
        return createPicture((HSSFClientAnchor) anchor, pictureIndex);
    }

    public HSSFPolygon createPolygon(HSSFClientAnchor anchor) {
        HSSFPolygon shape = new HSSFPolygon((HSSFShape) null, anchor);
        shape.anchor = anchor;
        this._shapes.add(shape);
        return shape;
    }

    public HSSFTextbox createTextbox(HSSFClientAnchor anchor) {
        HSSFTextbox shape = new HSSFTextbox((HSSFShape) null, anchor);
        shape.anchor = anchor;
        this._shapes.add(shape);
        return shape;
    }

    public HSSFComment createComment(HSSFAnchor anchor) {
        HSSFComment shape = new HSSFComment((HSSFShape) null, anchor);
        shape.anchor = anchor;
        this._shapes.add(shape);
        return shape;
    }

    /* access modifiers changed from: package-private */
    public HSSFSimpleShape createComboBox(HSSFAnchor anchor) {
        HSSFSimpleShape shape = new HSSFSimpleShape((HSSFShape) null, anchor);
        shape.setShapeType(20);
        shape.anchor = anchor;
        this._shapes.add(shape);
        return shape;
    }

    public HSSFComment createCellComment(ClientAnchor anchor) {
        return createComment((HSSFAnchor) anchor);
    }

    public List<HSSFShape> getChildren() {
        return this._shapes;
    }

    public int countOfAllChildren() {
        int count = this._shapes.size();
        for (HSSFShape shape : this._shapes) {
            count += shape.countOfAllChildren();
        }
        return count;
    }

    public void setCoordinates(int x1, int y1, int x2, int y2) {
        this._x1 = x1;
        this._y1 = y1;
        this._x2 = x2;
        this._y2 = y2;
    }

    public boolean containsChart() {
        EscherOptRecord optRecord = (EscherOptRecord) this._boundAggregate.findFirstWithId(EscherOptRecord.RECORD_ID);
        if (optRecord == null) {
            return false;
        }
        for (EscherProperty prop : optRecord.getEscherProperties()) {
            if (prop.getPropertyNumber() == 896 && prop.isComplex() && StringUtil.getFromUnicodeLE(((EscherComplexProperty) prop).getComplexData()).equals("Chart 1\u0000")) {
                return true;
            }
        }
        return false;
    }

    public int getX1() {
        return this._x1;
    }

    public int getY1() {
        return this._y1;
    }

    public int getX2() {
        return this._x2;
    }

    public int getY2() {
        return this._y2;
    }

    /* access modifiers changed from: protected */
    public EscherAggregate _getBoundAggregate() {
        return this._boundAggregate;
    }
}
