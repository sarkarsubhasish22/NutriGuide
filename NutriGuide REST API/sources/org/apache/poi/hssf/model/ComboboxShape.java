package org.apache.poi.hssf.model;

import org.apache.poi.ddf.EscherBoolProperty;
import org.apache.poi.ddf.EscherClientDataRecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.ddf.EscherProperties;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.ddf.EscherSpRecord;
import org.apache.poi.hssf.record.CommonObjectDataSubRecord;
import org.apache.poi.hssf.record.EndSubRecord;
import org.apache.poi.hssf.record.LbsDataSubRecord;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFSimpleShape;

public class ComboboxShape extends AbstractShape {
    private ObjRecord objRecord;
    private EscherContainerRecord spContainer;

    ComboboxShape(HSSFSimpleShape hssfShape, int shapeId) {
        this.spContainer = createSpContainer(hssfShape, shapeId);
        this.objRecord = createObjRecord(hssfShape, shapeId);
    }

    private ObjRecord createObjRecord(HSSFSimpleShape shape, int shapeId) {
        ObjRecord obj = new ObjRecord();
        CommonObjectDataSubRecord c = new CommonObjectDataSubRecord();
        c.setObjectType(20);
        c.setObjectId(shapeId);
        c.setLocked(true);
        c.setPrintable(false);
        c.setAutofill(true);
        c.setAutoline(false);
        LbsDataSubRecord l = LbsDataSubRecord.newAutoFilterInstance();
        EndSubRecord e = new EndSubRecord();
        obj.addSubRecord(c);
        obj.addSubRecord(l);
        obj.addSubRecord(e);
        return obj;
    }

    private EscherContainerRecord createSpContainer(HSSFSimpleShape shape, int shapeId) {
        EscherContainerRecord spContainer2 = new EscherContainerRecord();
        EscherSpRecord sp = new EscherSpRecord();
        EscherOptRecord opt = new EscherOptRecord();
        EscherClientDataRecord clientData = new EscherClientDataRecord();
        spContainer2.setRecordId(EscherContainerRecord.SP_CONTAINER);
        spContainer2.setOptions(15);
        sp.setRecordId(EscherSpRecord.RECORD_ID);
        sp.setOptions(3218);
        sp.setShapeId(shapeId);
        sp.setFlags(2560);
        opt.setRecordId(EscherOptRecord.RECORD_ID);
        opt.addEscherProperty(new EscherBoolProperty(127, 17039620));
        opt.addEscherProperty(new EscherBoolProperty(191, 524296));
        opt.addEscherProperty(new EscherBoolProperty(EscherProperties.LINESTYLE__NOLINEDRAWDASH, 524288));
        opt.addEscherProperty(new EscherSimpleProperty(EscherProperties.GROUPSHAPE__PRINT, 131072));
        HSSFClientAnchor userAnchor = (HSSFClientAnchor) shape.getAnchor();
        userAnchor.setAnchorType(1);
        EscherRecord anchor = createAnchor(userAnchor);
        clientData.setRecordId(EscherClientDataRecord.RECORD_ID);
        clientData.setOptions(0);
        spContainer2.addChildRecord(sp);
        spContainer2.addChildRecord(opt);
        spContainer2.addChildRecord(anchor);
        spContainer2.addChildRecord(clientData);
        return spContainer2;
    }

    public EscherContainerRecord getSpContainer() {
        return this.spContainer;
    }

    public ObjRecord getObjRecord() {
        return this.objRecord;
    }
}
