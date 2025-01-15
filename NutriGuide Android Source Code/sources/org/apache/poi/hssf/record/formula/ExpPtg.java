package org.apache.poi.hssf.record.formula;

import org.apache.poi.hssf.record.RecordFormatException;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

public final class ExpPtg extends ControlPtg {
    private static final int SIZE = 5;
    public static final short sid = 1;
    private final int field_1_first_row;
    private final int field_2_first_col;

    public ExpPtg(LittleEndianInput in) {
        this.field_1_first_row = in.readShort();
        this.field_2_first_col = in.readShort();
    }

    public ExpPtg(int firstRow, int firstCol) {
        this.field_1_first_row = firstRow;
        this.field_2_first_col = firstCol;
    }

    public void write(LittleEndianOutput out) {
        out.writeByte(getPtgClass() + 1);
        out.writeShort(this.field_1_first_row);
        out.writeShort(this.field_2_first_col);
    }

    public int getSize() {
        return 5;
    }

    public int getRow() {
        return this.field_1_first_row;
    }

    public int getColumn() {
        return this.field_2_first_col;
    }

    public String toFormulaString() {
        throw new RecordFormatException("Coding Error: Expected ExpPtg to be converted from Shared to Non-Shared Formula by ValueRecordsAggregate, but it wasn't");
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer("[Array Formula or Shared Formula]\n");
        buffer.append("row = ");
        buffer.append(getRow());
        buffer.append("\n");
        buffer.append("col = ");
        buffer.append(getColumn());
        buffer.append("\n");
        return buffer.toString();
    }
}
