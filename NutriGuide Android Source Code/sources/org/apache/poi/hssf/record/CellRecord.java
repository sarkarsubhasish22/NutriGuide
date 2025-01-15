package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public abstract class CellRecord extends StandardRecord implements CellValueRecordInterface {
    private int _columnIndex;
    private int _formatIndex;
    private int _rowIndex;

    /* access modifiers changed from: protected */
    public abstract void appendValueText(StringBuilder sb);

    /* access modifiers changed from: protected */
    public abstract String getRecordName();

    /* access modifiers changed from: protected */
    public abstract int getValueDataSize();

    /* access modifiers changed from: protected */
    public abstract void serializeValue(LittleEndianOutput littleEndianOutput);

    protected CellRecord() {
    }

    protected CellRecord(RecordInputStream in) {
        this._rowIndex = in.readUShort();
        this._columnIndex = in.readUShort();
        this._formatIndex = in.readUShort();
    }

    public final void setRow(int row) {
        this._rowIndex = row;
    }

    public final void setColumn(short col) {
        this._columnIndex = col;
    }

    public final void setXFIndex(short xf) {
        this._formatIndex = xf;
    }

    public final int getRow() {
        return this._rowIndex;
    }

    public final short getColumn() {
        return (short) this._columnIndex;
    }

    public final short getXFIndex() {
        return (short) this._formatIndex;
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        String recordName = getRecordName();
        sb.append("[");
        sb.append(recordName);
        sb.append("]\n");
        sb.append("    .row    = ");
        sb.append(HexDump.shortToHex(getRow()));
        sb.append("\n");
        sb.append("    .col    = ");
        sb.append(HexDump.shortToHex(getColumn()));
        sb.append("\n");
        sb.append("    .xfindex= ");
        sb.append(HexDump.shortToHex(getXFIndex()));
        sb.append("\n");
        appendValueText(sb);
        sb.append("\n");
        sb.append("[/");
        sb.append(recordName);
        sb.append("]\n");
        return sb.toString();
    }

    public final void serialize(LittleEndianOutput out) {
        out.writeShort(getRow());
        out.writeShort(getColumn());
        out.writeShort(getXFIndex());
        serializeValue(out);
    }

    /* access modifiers changed from: protected */
    public final int getDataSize() {
        return getValueDataSize() + 6;
    }

    /* access modifiers changed from: protected */
    public final void copyBaseFields(CellRecord rec) {
        rec._rowIndex = this._rowIndex;
        rec._columnIndex = this._columnIndex;
        rec._formatIndex = this._formatIndex;
    }
}
