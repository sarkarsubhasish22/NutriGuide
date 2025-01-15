package org.apache.poi.hssf.model;

import org.apache.poi.ddf.EscherBoolProperty;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.ddf.EscherProperties;
import org.apache.poi.ddf.EscherRGBProperty;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.ddf.EscherSpRecord;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.hssf.usermodel.HSSFAnchor;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFPolygon;
import org.apache.poi.hssf.usermodel.HSSFShape;
import org.apache.poi.hssf.usermodel.HSSFSimpleShape;
import org.apache.poi.hssf.usermodel.HSSFTextbox;

public abstract class AbstractShape {
    public abstract ObjRecord getObjRecord();

    public abstract EscherContainerRecord getSpContainer();

    public static AbstractShape createShape(HSSFShape hssfShape, int shapeId) {
        AbstractShape shape;
        if (hssfShape instanceof HSSFComment) {
            shape = new CommentShape((HSSFComment) hssfShape, shapeId);
        } else if (hssfShape instanceof HSSFTextbox) {
            shape = new TextboxShape((HSSFTextbox) hssfShape, shapeId);
        } else if (hssfShape instanceof HSSFPolygon) {
            shape = new PolygonShape((HSSFPolygon) hssfShape, shapeId);
        } else if (hssfShape instanceof HSSFSimpleShape) {
            HSSFSimpleShape simpleShape = (HSSFSimpleShape) hssfShape;
            int shapeType = simpleShape.getShapeType();
            if (shapeType == 1) {
                shape = new LineShape(simpleShape, shapeId);
            } else if (shapeType == 2 || shapeType == 3) {
                shape = new SimpleFilledShape(simpleShape, shapeId);
            } else if (shapeType == 8) {
                shape = new PictureShape(simpleShape, shapeId);
            } else if (shapeType == 20) {
                shape = new ComboboxShape(simpleShape, shapeId);
            } else {
                throw new IllegalArgumentException("Do not know how to handle this type of shape");
            }
        } else {
            throw new IllegalArgumentException("Unknown shape type");
        }
        EscherSpRecord sp = shape.getSpContainer().getChildById(EscherSpRecord.RECORD_ID);
        if (hssfShape.getParent() != null) {
            sp.setFlags(2 | sp.getFlags());
        }
        return shape;
    }

    protected AbstractShape() {
    }

    /* access modifiers changed from: protected */
    public EscherRecord createAnchor(HSSFAnchor userAnchor) {
        return ConvertAnchor.createAnchor(userAnchor);
    }

    /* access modifiers changed from: protected */
    public int addStandardOptions(HSSFShape shape, EscherOptRecord opt) {
        opt.addEscherProperty(new EscherBoolProperty(191, 524288));
        if (shape.isNoFill()) {
            opt.addEscherProperty(new EscherBoolProperty(EscherProperties.FILL__NOFILLHITTEST, 1114112));
        } else {
            opt.addEscherProperty(new EscherBoolProperty(EscherProperties.FILL__NOFILLHITTEST, 65536));
        }
        opt.addEscherProperty(new EscherRGBProperty(EscherProperties.FILL__FILLCOLOR, shape.getFillColor()));
        opt.addEscherProperty(new EscherBoolProperty(EscherProperties.GROUPSHAPE__PRINT, 524288));
        opt.addEscherProperty(new EscherRGBProperty(EscherProperties.LINESTYLE__COLOR, shape.getLineStyleColor()));
        int options = 5;
        if (shape.getLineWidth() != 9525) {
            opt.addEscherProperty(new EscherSimpleProperty(EscherProperties.LINESTYLE__LINEWIDTH, shape.getLineWidth()));
            options = 5 + 1;
        }
        if (shape.getLineStyle() != 0) {
            opt.addEscherProperty(new EscherSimpleProperty(EscherProperties.LINESTYLE__LINEDASHING, shape.getLineStyle()));
            opt.addEscherProperty(new EscherSimpleProperty(EscherProperties.LINESTYLE__LINEENDCAPSTYLE, 0));
            if (shape.getLineStyle() == -1) {
                opt.addEscherProperty(new EscherBoolProperty(EscherProperties.LINESTYLE__NOLINEDRAWDASH, 524288));
            } else {
                opt.addEscherProperty(new EscherBoolProperty(EscherProperties.LINESTYLE__NOLINEDRAWDASH, 524296));
            }
            options += 3;
        }
        opt.sortProperties();
        return options;
    }
}
