package org.apache.poi.hssf.model;

import org.apache.poi.ddf.EscherClientDataRecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherSpRecord;
import org.apache.poi.hssf.record.CommonObjectDataSubRecord;
import org.apache.poi.hssf.record.EndSubRecord;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.hssf.usermodel.HSSFShape;
import org.apache.poi.hssf.usermodel.HSSFSimpleShape;

public class SimpleFilledShape extends AbstractShape {
    private ObjRecord objRecord;
    private EscherContainerRecord spContainer;

    SimpleFilledShape(HSSFSimpleShape hssfShape, int shapeId) {
        this.spContainer = createSpContainer(hssfShape, shapeId);
        this.objRecord = createObjRecord(hssfShape, shapeId);
    }

    private EscherContainerRecord createSpContainer(HSSFSimpleShape hssfShape, int shapeId) {
        HSSFShape shape = hssfShape;
        EscherContainerRecord spContainer2 = new EscherContainerRecord();
        EscherSpRecord sp = new EscherSpRecord();
        EscherOptRecord opt = new EscherOptRecord();
        EscherClientDataRecord clientData = new EscherClientDataRecord();
        spContainer2.setRecordId(EscherContainerRecord.SP_CONTAINER);
        spContainer2.setOptions(15);
        sp.setRecordId(EscherSpRecord.RECORD_ID);
        sp.setOptions((short) ((objTypeToShapeType(hssfShape.getShapeType()) << 4) | 2));
        sp.setShapeId(shapeId);
        sp.setFlags(2560);
        opt.setRecordId(EscherOptRecord.RECORD_ID);
        addStandardOptions(shape, opt);
        EscherRecord anchor = createAnchor(shape.getAnchor());
        clientData.setRecordId(EscherClientDataRecord.RECORD_ID);
        clientData.setOptions(0);
        spContainer2.addChildRecord(sp);
        spContainer2.addChildRecord(opt);
        spContainer2.addChildRecord(anchor);
        spContainer2.addChildRecord(clientData);
        return spContainer2;
    }

    private short objTypeToShapeType(int objType) {
        if (objType == 3) {
            return 3;
        }
        if (objType == 2) {
            return 1;
        }
        throw new IllegalArgumentException("Unable to handle an object of this type");
    }

    private ObjRecord createObjRecord(HSSFShape hssfShape, int shapeId) {
        ObjRecord obj = new ObjRecord();
        CommonObjectDataSubRecord c = new CommonObjectDataSubRecord();
        c.setObjectType((short) ((HSSFSimpleShape) hssfShape).getShapeType());
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

    public EscherContainerRecord getSpContainer() {
        return this.spContainer;
    }

    public ObjRecord getObjRecord() {
        return this.objRecord;
    }
}
