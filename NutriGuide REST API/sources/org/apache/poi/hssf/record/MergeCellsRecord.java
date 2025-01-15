package org.apache.poi.hssf.record;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.util.LittleEndianOutput;

public final class MergeCellsRecord extends StandardRecord {
    public static final short sid = 229;
    private final int _numberOfRegions;
    private CellRangeAddress[] _regions;
    private final int _startIndex;

    public MergeCellsRecord(CellRangeAddress[] regions, int startIndex, int numberOfRegions) {
        this._regions = regions;
        this._startIndex = startIndex;
        this._numberOfRegions = numberOfRegions;
    }

    public MergeCellsRecord(RecordInputStream in) {
        int nRegions = in.readUShort();
        CellRangeAddress[] cras = new CellRangeAddress[nRegions];
        for (int i = 0; i < nRegions; i++) {
            cras[i] = new CellRangeAddress(in);
        }
        this._numberOfRegions = nRegions;
        this._startIndex = 0;
        this._regions = cras;
    }

    public short getNumAreas() {
        return (short) this._numberOfRegions;
    }

    public CellRangeAddress getAreaAt(int index) {
        return this._regions[this._startIndex + index];
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return CellRangeAddressList.getEncodedSize(this._numberOfRegions);
    }

    public short getSid() {
        return sid;
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this._numberOfRegions);
        for (int i = 0; i < this._numberOfRegions; i++) {
            this._regions[this._startIndex + i].serialize(out);
        }
    }

    public String toString() {
        StringBuffer retval = new StringBuffer();
        retval.append("[MERGEDCELLS]");
        retval.append("\n");
        retval.append("     .numregions =");
        retval.append(getNumAreas());
        retval.append("\n");
        for (int k = 0; k < this._numberOfRegions; k++) {
            CellRangeAddress r = this._regions[this._startIndex + k];
            retval.append("     .rowfrom =");
            retval.append(r.getFirstRow());
            retval.append("\n");
            retval.append("     .rowto   =");
            retval.append(r.getLastRow());
            retval.append("\n");
            retval.append("     .colfrom =");
            retval.append(r.getFirstColumn());
            retval.append("\n");
            retval.append("     .colto   =");
            retval.append(r.getLastColumn());
            retval.append("\n");
        }
        retval.append("[MERGEDCELLS]");
        retval.append("\n");
        return retval.toString();
    }

    public Object clone() {
        int nRegions = this._numberOfRegions;
        CellRangeAddress[] clonedRegions = new CellRangeAddress[nRegions];
        for (int i = 0; i < clonedRegions.length; i++) {
            clonedRegions[i] = this._regions[this._startIndex + i].copy();
        }
        return new MergeCellsRecord(clonedRegions, 0, nRegions);
    }
}
