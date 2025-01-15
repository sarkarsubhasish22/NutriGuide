package org.apache.poi.hssf.record;

import org.apache.poi.hssf.record.cf.CellRangeUtil;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.util.LittleEndianOutput;

public final class CFHeaderRecord extends StandardRecord {
    public static final short sid = 432;
    private int field_1_numcf;
    private int field_2_need_recalculation;
    private CellRangeAddress field_3_enclosing_cell_range;
    private CellRangeAddressList field_4_cell_ranges;

    public CFHeaderRecord() {
        this.field_4_cell_ranges = new CellRangeAddressList();
    }

    public CFHeaderRecord(CellRangeAddress[] regions, int nRules) {
        setCellRanges(CellRangeUtil.mergeCellRanges(regions));
        this.field_1_numcf = nRules;
    }

    public CFHeaderRecord(RecordInputStream in) {
        this.field_1_numcf = in.readShort();
        this.field_2_need_recalculation = in.readShort();
        this.field_3_enclosing_cell_range = new CellRangeAddress(in);
        this.field_4_cell_ranges = new CellRangeAddressList(in);
    }

    public int getNumberOfConditionalFormats() {
        return this.field_1_numcf;
    }

    public void setNumberOfConditionalFormats(int n) {
        this.field_1_numcf = n;
    }

    public boolean getNeedRecalculation() {
        return this.field_2_need_recalculation == 1;
    }

    public void setNeedRecalculation(boolean b) {
        this.field_2_need_recalculation = b;
    }

    public CellRangeAddress getEnclosingCellRange() {
        return this.field_3_enclosing_cell_range;
    }

    public void setEnclosingCellRange(CellRangeAddress cr) {
        this.field_3_enclosing_cell_range = cr;
    }

    public void setCellRanges(CellRangeAddress[] cellRanges) {
        if (cellRanges != null) {
            CellRangeAddressList cral = new CellRangeAddressList();
            CellRangeAddress enclosingRange = null;
            for (CellRangeAddress cr : cellRanges) {
                enclosingRange = CellRangeUtil.createEnclosingCellRange(cr, enclosingRange);
                cral.addCellRangeAddress(cr);
            }
            this.field_3_enclosing_cell_range = enclosingRange;
            this.field_4_cell_ranges = cral;
            return;
        }
        throw new IllegalArgumentException("cellRanges must not be null");
    }

    public CellRangeAddress[] getCellRanges() {
        return this.field_4_cell_ranges.getCellRangeAddresses();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[CFHEADER]\n");
        buffer.append("\t.id\t\t= ");
        buffer.append(Integer.toHexString(432));
        buffer.append("\n");
        buffer.append("\t.numCF\t\t\t= ");
        buffer.append(getNumberOfConditionalFormats());
        buffer.append("\n");
        buffer.append("\t.needRecalc\t   = ");
        buffer.append(getNeedRecalculation());
        buffer.append("\n");
        buffer.append("\t.enclosingCellRange= ");
        buffer.append(getEnclosingCellRange());
        buffer.append("\n");
        buffer.append("\t.cfranges=[");
        int i = 0;
        while (i < this.field_4_cell_ranges.countRanges()) {
            buffer.append(i == 0 ? "" : ",");
            buffer.append(this.field_4_cell_ranges.getCellRangeAddress(i).toString());
            i++;
        }
        buffer.append("]\n");
        buffer.append("[/CFHEADER]\n");
        return buffer.toString();
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return this.field_4_cell_ranges.getSize() + 12;
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_numcf);
        out.writeShort(this.field_2_need_recalculation);
        this.field_3_enclosing_cell_range.serialize(out);
        this.field_4_cell_ranges.serialize(out);
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        CFHeaderRecord result = new CFHeaderRecord();
        result.field_1_numcf = this.field_1_numcf;
        result.field_2_need_recalculation = this.field_2_need_recalculation;
        result.field_3_enclosing_cell_range = this.field_3_enclosing_cell_range;
        result.field_4_cell_ranges = this.field_4_cell_ranges.copy();
        return result;
    }
}
