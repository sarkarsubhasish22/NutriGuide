package org.apache.poi.hssf.model;

import org.apache.poi.ddf.EscherClientDataRecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.ddf.EscherProperties;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.ddf.EscherSpRecord;
import org.apache.poi.hssf.record.CommonObjectDataSubRecord;
import org.apache.poi.hssf.record.EndSubRecord;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.hssf.usermodel.HSSFAnchor;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFShape;
import org.apache.poi.hssf.usermodel.HSSFSimpleShape;

public class PictureShape extends AbstractShape {
    private ObjRecord objRecord;
    private EscherContainerRecord spContainer;

    PictureShape(HSSFSimpleShape hssfShape, int shapeId) {
        this.spContainer = createSpContainer(hssfShape, shapeId);
        this.objRecord = createObjRecord(hssfShape, shapeId);
    }

    private EscherContainerRecord createSpContainer(HSSFSimpleShape hssfShape, int shapeId) {
        HSSFPicture shape = (HSSFPicture) hssfShape;
        EscherContainerRecord spContainer2 = new EscherContainerRecord();
        EscherSpRecord sp = new EscherSpRecord();
        EscherOptRecord opt = new EscherOptRecord();
        EscherClientDataRecord clientData = new EscherClientDataRecord();
        spContainer2.setRecordId(EscherContainerRecord.SP_CONTAINER);
        spContainer2.setOptions(15);
        sp.setRecordId(EscherSpRecord.RECORD_ID);
        sp.setOptions(1202);
        sp.setShapeId(shapeId);
        sp.setFlags(2560);
        opt.setRecordId(EscherOptRecord.RECORD_ID);
        opt.addEscherProperty(new EscherSimpleProperty(EscherProperties.BLIP__BLIPTODISPLAY, false, true, shape.getPictureIndex()));
        addStandardOptions(shape, opt);
        HSSFAnchor userAnchor = shape.getAnchor();
        if (userAnchor.isHorizontallyFlipped()) {
            sp.setFlags(sp.getFlags() | 64);
        }
        if (userAnchor.isVerticallyFlipped()) {
            sp.setFlags(sp.getFlags() | 128);
        }
        EscherRecord anchor = createAnchor(userAnchor);
        clientData.setRecordId(EscherClientDataRecord.RECORD_ID);
        clientData.setOptions(0);
        spContainer2.addChildRecord(sp);
        spContainer2.addChildRecord(opt);
        spContainer2.addChildRecord(anchor);
        spContainer2.addChildRecord(clientData);
        return spContainer2;
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
        c.setReserved2(0);
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
