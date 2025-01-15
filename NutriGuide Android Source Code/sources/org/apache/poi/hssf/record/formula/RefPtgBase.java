package org.apache.poi.hssf.record.formula;

import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

public abstract class RefPtgBase extends OperandPtg {
    private static final BitField colRelative = BitFieldFactory.getInstance(16384);
    private static final BitField column = BitFieldFactory.getInstance(255);
    private static final BitField rowRelative = BitFieldFactory.getInstance(32768);
    private int field_1_row;
    private int field_2_col;

    protected RefPtgBase() {
    }

    protected RefPtgBase(CellReference c) {
        setRow(c.getRow());
        setColumn(c.getCol());
        setColRelative(!c.isColAbsolute());
        setRowRelative(!c.isRowAbsolute());
    }

    /* access modifiers changed from: protected */
    public final void readCoordinates(LittleEndianInput in) {
        this.field_1_row = in.readUShort();
        this.field_2_col = in.readUShort();
    }

    /* access modifiers changed from: protected */
    public final void writeCoordinates(LittleEndianOutput out) {
        out.writeShort(this.field_1_row);
        out.writeShort(this.field_2_col);
    }

    public final void setRow(int rowIndex) {
        this.field_1_row = rowIndex;
    }

    public final int getRow() {
        return this.field_1_row;
    }

    public final boolean isRowRelative() {
        return rowRelative.isSet(this.field_2_col);
    }

    public final void setRowRelative(boolean rel) {
        this.field_2_col = rowRelative.setBoolean(this.field_2_col, rel);
    }

    public final boolean isColRelative() {
        return colRelative.isSet(this.field_2_col);
    }

    public final void setColRelative(boolean rel) {
        this.field_2_col = colRelative.setBoolean(this.field_2_col, rel);
    }

    public final void setColumn(int col) {
        this.field_2_col = column.setValue(this.field_2_col, col);
    }

    public final int getColumn() {
        return column.getValue(this.field_2_col);
    }

    /* access modifiers changed from: protected */
    public final String formatReferenceAsString() {
        return new CellReference(getRow(), getColumn(), !isRowRelative(), !isColRelative()).formatAsString();
    }

    public final byte getDefaultOperandClass() {
        return 0;
    }
}
