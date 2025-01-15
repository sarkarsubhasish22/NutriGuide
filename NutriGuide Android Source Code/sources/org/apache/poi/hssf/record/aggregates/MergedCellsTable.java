package org.apache.poi.hssf.record.aggregates;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.model.RecordStream;
import org.apache.poi.hssf.record.MergeCellsRecord;
import org.apache.poi.hssf.record.aggregates.RecordAggregate;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;

public final class MergedCellsTable extends RecordAggregate {
    private static int MAX_MERGED_REGIONS = 1027;
    private final List _mergedRegions = new ArrayList();

    public void read(RecordStream rs) {
        List temp = this._mergedRegions;
        while (rs.peekNextClass() == MergeCellsRecord.class) {
            MergeCellsRecord mcr = (MergeCellsRecord) rs.getNext();
            int nRegions = mcr.getNumAreas();
            for (int i = 0; i < nRegions; i++) {
                temp.add(mcr.getAreaAt(i));
            }
        }
    }

    public int getRecordSize() {
        int nRegions = this._mergedRegions.size();
        if (nRegions < 1) {
            return 0;
        }
        int i = MAX_MERGED_REGIONS;
        return ((CellRangeAddressList.getEncodedSize(i) + 4) * (nRegions / i)) + 4 + CellRangeAddressList.getEncodedSize(nRegions % i);
    }

    public void visitContainedRecords(RecordAggregate.RecordVisitor rv) {
        int nRegions = this._mergedRegions.size();
        if (nRegions >= 1) {
            int i = MAX_MERGED_REGIONS;
            int nFullMergedCellsRecords = nRegions / i;
            int nLeftoverMergedRegions = nRegions % i;
            CellRangeAddress[] cras = new CellRangeAddress[nRegions];
            this._mergedRegions.toArray(cras);
            for (int i2 = 0; i2 < nFullMergedCellsRecords; i2++) {
                int i3 = MAX_MERGED_REGIONS;
                rv.visitRecord(new MergeCellsRecord(cras, i2 * i3, i3));
            }
            if (nLeftoverMergedRegions > 0) {
                rv.visitRecord(new MergeCellsRecord(cras, MAX_MERGED_REGIONS * nFullMergedCellsRecords, nLeftoverMergedRegions));
            }
        }
    }

    public void addRecords(MergeCellsRecord[] mcrs) {
        for (MergeCellsRecord addMergeCellsRecord : mcrs) {
            addMergeCellsRecord(addMergeCellsRecord);
        }
    }

    private void addMergeCellsRecord(MergeCellsRecord mcr) {
        int nRegions = mcr.getNumAreas();
        for (int i = 0; i < nRegions; i++) {
            this._mergedRegions.add(mcr.getAreaAt(i));
        }
    }

    public CellRangeAddress get(int index) {
        checkIndex(index);
        return (CellRangeAddress) this._mergedRegions.get(index);
    }

    public void remove(int index) {
        checkIndex(index);
        this._mergedRegions.remove(index);
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= this._mergedRegions.size()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Specified CF index ");
            sb.append(index);
            sb.append(" is outside the allowable range (0..");
            sb.append(this._mergedRegions.size() - 1);
            sb.append(")");
            throw new IllegalArgumentException(sb.toString());
        }
    }

    public void addArea(int rowFrom, int colFrom, int rowTo, int colTo) {
        this._mergedRegions.add(new CellRangeAddress(rowFrom, rowTo, colFrom, colTo));
    }

    public int getNumberOfMergedRegions() {
        return this._mergedRegions.size();
    }
}
