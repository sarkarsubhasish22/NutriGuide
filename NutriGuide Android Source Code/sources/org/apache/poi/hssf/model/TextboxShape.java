package org.apache.poi.hssf.model;

import org.apache.poi.ddf.EscherClientAnchorRecord;
import org.apache.poi.ddf.EscherClientDataRecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.ddf.EscherProperties;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.ddf.EscherSpRecord;
import org.apache.poi.ddf.EscherTextboxRecord;
import org.apache.poi.hssf.record.CommonObjectDataSubRecord;
import org.apache.poi.hssf.record.EndSubRecord;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.hssf.record.TextObjectRecord;
import org.apache.poi.hssf.usermodel.HSSFTextbox;

public class TextboxShape extends AbstractShape {
    private EscherTextboxRecord escherTextbox;
    private ObjRecord objRecord;
    private EscherContainerRecord spContainer;
    private TextObjectRecord textObjectRecord;

    TextboxShape(HSSFTextbox hssfShape, int shapeId) {
        this.spContainer = createSpContainer(hssfShape, shapeId);
        this.objRecord = createObjRecord(hssfShape, shapeId);
        this.textObjectRecord = createTextObjectRecord(hssfShape, shapeId);
    }

    private ObjRecord createObjRecord(HSSFTextbox hssfShape, int shapeId) {
        ObjRecord obj = new ObjRecord();
        CommonObjectDataSubRecord c = new CommonObjectDataSubRecord();
        c.setObjectType((short) hssfShape.getShapeType());
        c.setObjectId(shapeId);
        c.setLocked(true);
        c.setPrintable(true);
        c.setAutofill(true);
        c.setAutoline(true);
        EndSubRecord e = new EndSubRecord();
        obj.addSubRecord(c);
        obj.addSubRecord(e);
        return obj;
    }

    private EscherContainerRecord createSpContainer(HSSFTextbox hssfShape, int shapeId) {
        HSSFTextbox shape = hssfShape;
        EscherContainerRecord spContainer2 = new EscherContainerRecord();
        EscherSpRecord sp = new EscherSpRecord();
        EscherOptRecord opt = new EscherOptRecord();
        new EscherClientAnchorRecord();
        EscherClientDataRecord clientData = new EscherClientDataRecord();
        this.escherTextbox = new EscherTextboxRecord();
        spContainer2.setRecordId(EscherContainerRecord.SP_CONTAINER);
        spContainer2.setOptions(15);
        sp.setRecordId(EscherSpRecord.RECORD_ID);
        sp.setOptions(3234);
        sp.setShapeId(shapeId);
        sp.setFlags(2560);
        opt.setRecordId(EscherOptRecord.RECORD_ID);
        opt.addEscherProperty(new EscherSimpleProperty(128, 0));
        opt.addEscherProperty(new EscherSimpleProperty(129, shape.getMarginLeft()));
        opt.addEscherProperty(new EscherSimpleProperty(131, shape.getMarginRight()));
        opt.addEscherProperty(new EscherSimpleProperty(132, shape.getMarginBottom()));
        opt.addEscherProperty(new EscherSimpleProperty(130, shape.getMarginTop()));
        opt.addEscherProperty(new EscherSimpleProperty(133, 0));
        opt.addEscherProperty(new EscherSimpleProperty(135, 0));
        opt.addEscherProperty(new EscherSimpleProperty(EscherProperties.GROUPSHAPE__PRINT, 524288));
        addStandardOptions(shape, opt);
        EscherRecord anchor = createAnchor(shape.getAnchor());
        clientData.setRecordId(EscherClientDataRecord.RECORD_ID);
        clientData.setOptions(0);
        this.escherTextbox.setRecordId(EscherTextboxRecord.RECORD_ID);
        this.escherTextbox.setOptions(0);
        spContainer2.addChildRecord(sp);
        spContainer2.addChildRecord(opt);
        spContainer2.addChildRecord(anchor);
        spContainer2.addChildRecord(clientData);
        spContainer2.addChildRecord(this.escherTextbox);
        return spContainer2;
    }

    private TextObjectRecord createTextObjectRecord(HSSFTextbox hssfShape, int shapeId) {
        TextObjectRecord obj = new TextObjectRecord();
        obj.setHorizontalTextAlignment(hssfShape.getHorizontalAlignment());
        obj.setVerticalTextAlignment(hssfShape.getVerticalAlignment());
        obj.setTextLocked(true);
        obj.setTextOrientation(0);
        obj.setStr(hssfShape.getString());
        return obj;
    }

    public EscherContainerRecord getSpContainer() {
        return this.spContainer;
    }

    public ObjRecord getObjRecord() {
        return this.objRecord;
    }

    public TextObjectRecord getTextObjectRecord() {
        return this.textObjectRecord;
    }

    public EscherRecord getEscherTextbox() {
        return this.escherTextbox;
    }
}
