package org.apache.poi.hssf.model;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ddf.EscherDgRecord;
import org.apache.poi.ddf.EscherDggRecord;

public class DrawingManager2 {
    EscherDggRecord dgg;
    List drawingGroups = new ArrayList();

    public DrawingManager2(EscherDggRecord dgg2) {
        this.dgg = dgg2;
    }

    public void clearDrawingGroups() {
        this.drawingGroups.clear();
    }

    public EscherDgRecord createDgRecord() {
        EscherDgRecord dg = new EscherDgRecord();
        dg.setRecordId(EscherDgRecord.RECORD_ID);
        short dgId = findNewDrawingGroupId();
        dg.setOptions((short) (dgId << 4));
        dg.setNumShapes(0);
        dg.setLastMSOSPID(-1);
        this.drawingGroups.add(dg);
        this.dgg.addCluster(dgId, 0);
        EscherDggRecord escherDggRecord = this.dgg;
        escherDggRecord.setDrawingsSaved(escherDggRecord.getDrawingsSaved() + 1);
        return dg;
    }

    public int allocateShapeId(short drawingGroupId) {
        return allocateShapeId(drawingGroupId, getDrawingGroup(drawingGroupId));
    }

    public int allocateShapeId(short drawingGroupId, EscherDgRecord dg) {
        EscherDggRecord escherDggRecord = this.dgg;
        escherDggRecord.setNumShapesSaved(escherDggRecord.getNumShapesSaved() + 1);
        int i = 0;
        while (i < this.dgg.getFileIdClusters().length) {
            EscherDggRecord.FileIdCluster c = this.dgg.getFileIdClusters()[i];
            if (c.getDrawingGroupId() != drawingGroupId || c.getNumShapeIdsUsed() == 1024) {
                i++;
            } else {
                int result = c.getNumShapeIdsUsed() + ((i + 1) * 1024);
                c.incrementShapeId();
                dg.setNumShapes(dg.getNumShapes() + 1);
                dg.setLastMSOSPID(result);
                if (result >= this.dgg.getShapeIdMax()) {
                    this.dgg.setShapeIdMax(result + 1);
                }
                return result;
            }
        }
        this.dgg.addCluster(drawingGroupId, 0);
        this.dgg.getFileIdClusters()[this.dgg.getFileIdClusters().length - 1].incrementShapeId();
        dg.setNumShapes(dg.getNumShapes() + 1);
        int result2 = this.dgg.getFileIdClusters().length * 1024;
        dg.setLastMSOSPID(result2);
        if (result2 >= this.dgg.getShapeIdMax()) {
            this.dgg.setShapeIdMax(result2 + 1);
        }
        return result2;
    }

    /* access modifiers changed from: package-private */
    public short findNewDrawingGroupId() {
        short dgId = 1;
        while (drawingGroupExists(dgId)) {
            dgId = (short) (dgId + 1);
        }
        return dgId;
    }

    /* access modifiers changed from: package-private */
    public EscherDgRecord getDrawingGroup(int drawingGroupId) {
        return (EscherDgRecord) this.drawingGroups.get(drawingGroupId - 1);
    }

    /* access modifiers changed from: package-private */
    public boolean drawingGroupExists(short dgId) {
        for (EscherDggRecord.FileIdCluster drawingGroupId : this.dgg.getFileIdClusters()) {
            if (drawingGroupId.getDrawingGroupId() == dgId) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public int findFreeSPIDBlock() {
        return ((this.dgg.getShapeIdMax() / 1024) + 1) * 1024;
    }

    public EscherDggRecord getDgg() {
        return this.dgg;
    }
}
