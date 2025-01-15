package org.apache.poi.hssf.model;

import org.apache.poi.ddf.EscherArrayProperty;
import org.apache.poi.ddf.EscherClientDataRecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.ddf.EscherProperties;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherShapePathProperty;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.ddf.EscherSpRecord;
import org.apache.poi.hssf.record.CommonObjectDataSubRecord;
import org.apache.poi.hssf.record.EndSubRecord;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.hssf.usermodel.HSSFPolygon;
import org.apache.poi.hssf.usermodel.HSSFShape;
import org.apache.poi.util.LittleEndian;

public class PolygonShape extends AbstractShape {
    public static final short OBJECT_TYPE_MICROSOFT_OFFICE_DRAWING = 30;
    private ObjRecord objRecord;
    private EscherContainerRecord spContainer;

    PolygonShape(HSSFPolygon hssfShape, int shapeId) {
        this.spContainer = createSpContainer(hssfShape, shapeId);
        this.objRecord = createObjRecord(hssfShape, shapeId);
    }

    private EscherContainerRecord createSpContainer(HSSFPolygon hssfShape, int shapeId) {
        HSSFShape shape = hssfShape;
        EscherContainerRecord spContainer2 = new EscherContainerRecord();
        EscherSpRecord sp = new EscherSpRecord();
        EscherOptRecord opt = new EscherOptRecord();
        EscherClientDataRecord clientData = new EscherClientDataRecord();
        spContainer2.setRecordId(EscherContainerRecord.SP_CONTAINER);
        spContainer2.setOptions(15);
        sp.setRecordId(EscherSpRecord.RECORD_ID);
        sp.setOptions(370);
        sp.setShapeId(shapeId);
        if (hssfShape.getParent() == null) {
            sp.setFlags(2560);
        } else {
            sp.setFlags(2562);
        }
        opt.setRecordId(EscherOptRecord.RECORD_ID);
        opt.addEscherProperty(new EscherSimpleProperty(4, false, false, 0));
        opt.addEscherProperty(new EscherSimpleProperty(EscherProperties.GEOMETRY__RIGHT, false, false, hssfShape.getDrawAreaWidth()));
        opt.addEscherProperty(new EscherSimpleProperty(EscherProperties.GEOMETRY__BOTTOM, false, false, hssfShape.getDrawAreaHeight()));
        opt.addEscherProperty(new EscherShapePathProperty(EscherProperties.GEOMETRY__SHAPEPATH, 4));
        EscherArrayProperty verticesProp = new EscherArrayProperty(EscherProperties.GEOMETRY__VERTICES, false, new byte[0]);
        verticesProp.setNumberOfElementsInArray(hssfShape.getXPoints().length + 1);
        verticesProp.setNumberOfElementsInMemory(hssfShape.getXPoints().length + 1);
        verticesProp.setSizeOfElements(65520);
        for (int i = 0; i < hssfShape.getXPoints().length; i++) {
            byte[] data = new byte[4];
            LittleEndian.putShort(data, 0, (short) hssfShape.getXPoints()[i]);
            LittleEndian.putShort(data, 2, (short) hssfShape.getYPoints()[i]);
            verticesProp.setElement(i, data);
        }
        int point = hssfShape.getXPoints().length;
        byte[] data2 = new byte[4];
        LittleEndian.putShort(data2, 0, (short) hssfShape.getXPoints()[0]);
        LittleEndian.putShort(data2, 2, (short) hssfShape.getYPoints()[0]);
        verticesProp.setElement(point, data2);
        opt.addEscherProperty(verticesProp);
        EscherArrayProperty segmentsProp = new EscherArrayProperty(EscherProperties.GEOMETRY__SEGMENTINFO, false, (byte[]) null);
        segmentsProp.setSizeOfElements(2);
        segmentsProp.setNumberOfElementsInArray((hssfShape.getXPoints().length * 2) + 4);
        segmentsProp.setNumberOfElementsInMemory((hssfShape.getXPoints().length * 2) + 4);
        segmentsProp.setElement(0, new byte[]{0, Ptg.CLASS_ARRAY});
        segmentsProp.setElement(1, new byte[]{0, -84});
        for (int i2 = 0; i2 < hssfShape.getXPoints().length; i2++) {
            segmentsProp.setElement((i2 * 2) + 2, new byte[]{1, 0});
            segmentsProp.setElement((i2 * 2) + 3, new byte[]{0, -84});
        }
        segmentsProp.setElement(segmentsProp.getNumberOfElementsInArray() - 2, new byte[]{1, 96});
        segmentsProp.setElement(segmentsProp.getNumberOfElementsInArray() - 1, new byte[]{0, Byte.MIN_VALUE});
        opt.addEscherProperty(segmentsProp);
        opt.addEscherProperty(new EscherSimpleProperty(EscherProperties.GEOMETRY__FILLOK, false, false, 65537));
        opt.addEscherProperty(new EscherSimpleProperty(EscherProperties.LINESTYLE__LINESTARTARROWHEAD, false, false, 0));
        opt.addEscherProperty(new EscherSimpleProperty(EscherProperties.LINESTYLE__LINEENDARROWHEAD, false, false, 0));
        opt.addEscherProperty(new EscherSimpleProperty(EscherProperties.LINESTYLE__LINEENDCAPSTYLE, false, false, 0));
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

    private ObjRecord createObjRecord(HSSFShape hssfShape, int shapeId) {
        HSSFShape hSSFShape = hssfShape;
        ObjRecord obj = new ObjRecord();
        CommonObjectDataSubRecord c = new CommonObjectDataSubRecord();
        c.setObjectType(30);
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
