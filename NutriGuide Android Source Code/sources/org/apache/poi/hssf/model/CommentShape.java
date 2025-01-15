package org.apache.poi.hssf.model;

import java.util.Iterator;
import java.util.List;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.ddf.EscherProperties;
import org.apache.poi.ddf.EscherProperty;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.hssf.record.CommonObjectDataSubRecord;
import org.apache.poi.hssf.record.NoteRecord;
import org.apache.poi.hssf.record.NoteStructureSubRecord;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.hssf.record.SubRecord;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFShape;
import org.apache.poi.ss.usermodel.ShapeTypes;

public final class CommentShape extends TextboxShape {
    private NoteRecord _note;

    public CommentShape(HSSFComment hssfShape, int shapeId) {
        super(hssfShape, shapeId);
        this._note = createNoteRecord(hssfShape, shapeId);
        ObjRecord obj = getObjRecord();
        List<SubRecord> records = obj.getSubRecords();
        int cmoIdx = 0;
        for (int i = 0; i < records.size(); i++) {
            SubRecord subRecord = records.get(i);
            if (subRecord instanceof CommonObjectDataSubRecord) {
                ((CommonObjectDataSubRecord) subRecord).setAutofill(false);
                cmoIdx = i;
            }
        }
        obj.addSubRecord(cmoIdx + 1, new NoteStructureSubRecord());
    }

    private NoteRecord createNoteRecord(HSSFComment shape, int shapeId) {
        NoteRecord note = new NoteRecord();
        note.setColumn(shape.getColumn());
        note.setRow(shape.getRow());
        note.setFlags(shape.isVisible() ? (short) 2 : 0);
        note.setShapeId(shapeId);
        note.setAuthor(shape.getAuthor() == null ? "" : shape.getAuthor());
        return note;
    }

    /* access modifiers changed from: protected */
    public int addStandardOptions(HSSFShape shape, EscherOptRecord opt) {
        super.addStandardOptions(shape, opt);
        Iterator<EscherProperty> iterator = opt.getEscherProperties().iterator();
        while (iterator.hasNext()) {
            short id = iterator.next().getId();
            if (!(id == 387 || id == 448 || id == 959)) {
                switch (id) {
                    case 129:
                    case ShapeTypes.DOUBLE_WAVE /*130*/:
                    case ShapeTypes.PLUS /*131*/:
                    case ShapeTypes.FLOW_CHART_PROCESS /*132*/:
                        break;
                }
            }
            iterator.remove();
        }
        opt.addEscherProperty(new EscherSimpleProperty(EscherProperties.GROUPSHAPE__PRINT, ((HSSFComment) shape).isVisible() ? 655360 : 655362));
        opt.addEscherProperty(new EscherSimpleProperty(EscherProperties.SHADOWSTYLE__SHADOWOBSURED, 196611));
        opt.addEscherProperty(new EscherSimpleProperty(513, 0));
        opt.sortProperties();
        return opt.getEscherProperties().size();
    }

    public NoteRecord getNoteRecord() {
        return this._note;
    }
}
