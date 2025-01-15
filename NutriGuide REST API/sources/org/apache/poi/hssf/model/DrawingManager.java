package org.apache.poi.hssf.model;

import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ddf.EscherDgRecord;
import org.apache.poi.ddf.EscherDggRecord;

public class DrawingManager {
    Map dgMap = new HashMap();
    EscherDggRecord dgg;

    public DrawingManager(EscherDggRecord dgg2) {
        this.dgg = dgg2;
    }

    public EscherDgRecord createDgRecord() {
        EscherDgRecord dg = new EscherDgRecord();
        dg.setRecordId(EscherDgRecord.RECORD_ID);
        short dgId = findNewDrawingGroupId();
        dg.setOptions((short) (dgId << 4));
        dg.setNumShapes(0);
        dg.setLastMSOSPID(-1);
        this.dgg.addCluster(dgId, 0);
        EscherDggRecord escherDggRecord = this.dgg;
        escherDggRecord.setDrawingsSaved(escherDggRecord.getDrawingsSaved() + 1);
        this.dgMap.put(Short.valueOf(dgId), dg);
        return dg;
    }

    public int allocateShapeId(short drawingGroupId) {
        EscherDgRecord dg = (EscherDgRecord) this.dgMap.get(Short.valueOf(drawingGroupId));
        int newShapeId = 0;
        if (dg.getLastMSOSPID() % 1024 == 1023) {
            newShapeId = findFreeSPIDBlock();
            this.dgg.addCluster(drawingGroupId, 1);
        } else {
            for (EscherDggRecord.FileIdCluster c : this.dgg.getFileIdClusters()) {
                if (c.getDrawingGroupId() == drawingGroupId && c.getNumShapeIdsUsed() != 1024) {
                    c.incrementShapeId();
                }
                if (dg.getLastMSOSPID() == -1) {
                    newShapeId = findFreeSPIDBlock();
                } else {
                    newShapeId = dg.getLastMSOSPID() + 1;
                }
            }
        }
        EscherDggRecord escherDggRecord = this.dgg;
        escherDggRecord.setNumShapesSaved(escherDggRecord.getNumShapesSaved() + 1);
        if (newShapeId >= this.dgg.getShapeIdMax()) {
            this.dgg.setShapeIdMax(newShapeId + 1);
        }
        dg.setLastMSOSPID(newShapeId);
        dg.incrementShapeCount();
        return newShapeId;
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
