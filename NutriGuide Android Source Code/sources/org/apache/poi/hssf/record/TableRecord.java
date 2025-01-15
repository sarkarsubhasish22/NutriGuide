package org.apache.poi.hssf.record;

import org.apache.poi.hssf.util.CellRangeAddress8Bit;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

public final class TableRecord extends SharedValueRecordBase {
    private static final BitField alwaysCalc = BitFieldFactory.getInstance(1);
    private static final BitField calcOnOpen = BitFieldFactory.getInstance(2);
    private static final BitField colDeleted = BitFieldFactory.getInstance(32);
    private static final BitField oneOrTwoVar = BitFieldFactory.getInstance(8);
    private static final BitField rowDeleted = BitFieldFactory.getInstance(16);
    private static final BitField rowOrColInpCell = BitFieldFactory.getInstance(4);
    public static final short sid = 566;
    private int field_10_colInputCol;
    private int field_5_flags;
    private int field_6_res;
    private int field_7_rowInputRow;
    private int field_8_colInputRow;
    private int field_9_rowInputCol;

    public TableRecord(RecordInputStream in) {
        super((LittleEndianInput) in);
        this.field_5_flags = in.readByte();
        this.field_6_res = in.readByte();
        this.field_7_rowInputRow = in.readShort();
        this.field_8_colInputRow = in.readShort();
        this.field_9_rowInputCol = in.readShort();
        this.field_10_colInputCol = in.readShort();
    }

    public TableRecord(CellRangeAddress8Bit range) {
        super(range);
        this.field_6_res = 0;
    }

    public int getFlags() {
        return this.field_5_flags;
    }

    public void setFlags(int flags) {
        this.field_5_flags = flags;
    }

    public int getRowInputRow() {
        return this.field_7_rowInputRow;
    }

    public void setRowInputRow(int rowInputRow) {
        this.field_7_rowInputRow = rowInputRow;
    }

    public int getColInputRow() {
        return this.field_8_colInputRow;
    }

    public void setColInputRow(int colInputRow) {
        this.field_8_colInputRow = colInputRow;
    }

    public int getRowInputCol() {
        return this.field_9_rowInputCol;
    }

    public void setRowInputCol(int rowInputCol) {
        this.field_9_rowInputCol = rowInputCol;
    }

    public int getColInputCol() {
        return this.field_10_colInputCol;
    }

    public void setColInputCol(int colInputCol) {
        this.field_10_colInputCol = colInputCol;
    }

    public boolean isAlwaysCalc() {
        return alwaysCalc.isSet(this.field_5_flags);
    }

    public void setAlwaysCalc(boolean flag) {
        this.field_5_flags = alwaysCalc.setBoolean(this.field_5_flags, flag);
    }

    public boolean isRowOrColInpCell() {
        return rowOrColInpCell.isSet(this.field_5_flags);
    }

    public void setRowOrColInpCell(boolean flag) {
        this.field_5_flags = rowOrColInpCell.setBoolean(this.field_5_flags, flag);
    }

    public boolean isOneNotTwoVar() {
        return oneOrTwoVar.isSet(this.field_5_flags);
    }

    public void setOneNotTwoVar(boolean flag) {
        this.field_5_flags = oneOrTwoVar.setBoolean(this.field_5_flags, flag);
    }

    public boolean isColDeleted() {
        return colDeleted.isSet(this.field_5_flags);
    }

    public void setColDeleted(boolean flag) {
        this.field_5_flags = colDeleted.setBoolean(this.field_5_flags, flag);
    }

    public boolean isRowDeleted() {
        return rowDeleted.isSet(this.field_5_flags);
    }

    public void setRowDeleted(boolean flag) {
        this.field_5_flags = rowDeleted.setBoolean(this.field_5_flags, flag);
    }

    public short getSid() {
        return sid;
    }

    /* access modifiers changed from: protected */
    public int getExtraDataSize() {
        return 10;
    }

    /* access modifiers changed from: protected */
    public void serializeExtraData(LittleEndianOutput out) {
        out.writeByte(this.field_5_flags);
        out.writeByte(this.field_6_res);
        out.writeShort(this.field_7_rowInputRow);
        out.writeShort(this.field_8_colInputRow);
        out.writeShort(this.field_9_rowInputCol);
        out.writeShort(this.field_10_colInputCol);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[TABLE]\n");
        buffer.append("    .range    = ");
        buffer.append(getRange().toString());
        buffer.append("\n");
        buffer.append("    .flags    = ");
        buffer.append(HexDump.byteToHex(this.field_5_flags));
        buffer.append("\n");
        buffer.append("    .alwaysClc= ");
        buffer.append(isAlwaysCalc());
        buffer.append("\n");
        buffer.append("    .reserved = ");
        buffer.append(HexDump.intToHex(this.field_6_res));
        buffer.append("\n");
        CellReference crRowInput = cr(this.field_7_rowInputRow, this.field_8_colInputRow);
        CellReference crColInput = cr(this.field_9_rowInputCol, this.field_10_colInputCol);
        buffer.append("    .rowInput = ");
        buffer.append(crRowInput.formatAsString());
        buffer.append("\n");
        buffer.append("    .colInput = ");
        buffer.append(crColInput.formatAsString());
        buffer.append("\n");
        buffer.append("[/TABLE]\n");
        return buffer.toString();
    }

    private static CellReference cr(int rowIx, int colIxAndFlags) {
        int colIx = colIxAndFlags & 255;
        boolean isColAbs = true;
        boolean isRowAbs = (32768 & colIxAndFlags) == 0;
        if ((colIxAndFlags & 16384) != 0) {
            isColAbs = false;
        }
        return new CellReference(rowIx, colIx, isRowAbs, isColAbs);
    }
}
