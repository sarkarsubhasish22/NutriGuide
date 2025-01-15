package org.apache.poi.hssf.model;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.record.ArrayRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.MergeCellsRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SharedFormulaRecord;
import org.apache.poi.hssf.record.TableRecord;
import org.apache.poi.hssf.record.aggregates.SharedValueManager;
import org.apache.poi.ss.util.CellReference;

public final class RowBlocksReader {
    private final MergeCellsRecord[] _mergedCellsRecords;
    private final List _plainRecords;
    private final SharedValueManager _sfm;

    public RowBlocksReader(RecordStream rs) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        List<CellReference> firstCellRefs = new ArrayList<>();
        ArrayList arrayList3 = new ArrayList();
        ArrayList arrayList4 = new ArrayList();
        ArrayList arrayList5 = new ArrayList();
        FormulaRecord prevRec = null;
        ArrayList arrayList6 = null;
        while (!RecordOrderer.isEndOfRowBlock(rs.peekNextSid())) {
            if (rs.hasNext()) {
                Record rec = rs.getNext();
                short sid = rec.getSid();
                if (sid == 229) {
                    arrayList6 = arrayList5;
                } else if (sid == 545) {
                    arrayList6 = arrayList3;
                } else if (sid == 566) {
                    arrayList6 = arrayList4;
                } else if (sid != 1212) {
                    arrayList6 = arrayList;
                } else {
                    ArrayList arrayList7 = arrayList6;
                    ArrayList arrayList8 = arrayList2;
                    if (prevRec instanceof FormulaRecord) {
                        FormulaRecord fr = prevRec;
                        firstCellRefs.add(new CellReference(fr.getRow(), fr.getColumn()));
                        FormulaRecord formulaRecord = fr;
                        arrayList6 = arrayList8;
                        FormulaRecord formulaRecord2 = formulaRecord;
                    } else {
                        throw new RuntimeException("Shared formula record should follow a FormulaRecord");
                    }
                }
                arrayList6.add(rec);
                prevRec = rec;
            } else {
                throw new RuntimeException("Failed to find end of row/cell records");
            }
        }
        SharedFormulaRecord[] sharedFormulaRecs = new SharedFormulaRecord[arrayList2.size()];
        CellReference[] firstCells = new CellReference[firstCellRefs.size()];
        ArrayRecord[] arrayRecs = new ArrayRecord[arrayList3.size()];
        TableRecord[] tableRecs = new TableRecord[arrayList4.size()];
        arrayList2.toArray(sharedFormulaRecs);
        firstCellRefs.toArray(firstCells);
        arrayList3.toArray(arrayRecs);
        arrayList4.toArray(tableRecs);
        this._plainRecords = arrayList;
        this._sfm = SharedValueManager.create(sharedFormulaRecs, firstCells, arrayRecs, tableRecs);
        MergeCellsRecord[] mergeCellsRecordArr = new MergeCellsRecord[arrayList5.size()];
        this._mergedCellsRecords = mergeCellsRecordArr;
        arrayList5.toArray(mergeCellsRecordArr);
    }

    public MergeCellsRecord[] getLooseMergedCells() {
        return this._mergedCellsRecords;
    }

    public SharedValueManager getSharedFormulaManager() {
        return this._sfm;
    }

    public RecordStream getPlainRecordStream() {
        return new RecordStream(this._plainRecords, 0);
    }
}
