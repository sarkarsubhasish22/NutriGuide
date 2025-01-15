package org.apache.poi.hssf.record;

import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class ColumnInfoRecord extends StandardRecord {
    private static final BitField collapsed = BitFieldFactory.getInstance(4096);
    private static final BitField hidden = BitFieldFactory.getInstance(1);
    private static final BitField outlevel = BitFieldFactory.getInstance(1792);
    public static final short sid = 125;
    private int _colWidth;
    private int _firstCol;
    private int _lastCol;
    private int _options;
    private int _xfIndex;
    private int field_6_reserved;

    public ColumnInfoRecord() {
        setColumnWidth(2275);
        this._options = 2;
        this._xfIndex = 15;
        this.field_6_reserved = 2;
    }

    public ColumnInfoRecord(RecordInputStream in) {
        this._firstCol = in.readUShort();
        this._lastCol = in.readUShort();
        this._colWidth = in.readUShort();
        this._xfIndex = in.readUShort();
        this._options = in.readUShort();
        int remaining = in.remaining();
        if (remaining == 0) {
            this.field_6_reserved = 0;
        } else if (remaining == 1) {
            this.field_6_reserved = in.readByte();
        } else if (remaining == 2) {
            this.field_6_reserved = in.readUShort();
        } else {
            throw new RuntimeException("Unusual record size remaining=(" + in.remaining() + ")");
        }
    }

    public void setFirstColumn(int fc) {
        this._firstCol = fc;
    }

    public void setLastColumn(int lc) {
        this._lastCol = lc;
    }

    public void setColumnWidth(int cw) {
        this._colWidth = cw;
    }

    public void setXFIndex(int xfi) {
        this._xfIndex = xfi;
    }

    public void setHidden(boolean ishidden) {
        this._options = hidden.setBoolean(this._options, ishidden);
    }

    public void setOutlineLevel(int olevel) {
        this._options = outlevel.setValue(this._options, olevel);
    }

    public void setCollapsed(boolean isCollapsed) {
        this._options = collapsed.setBoolean(this._options, isCollapsed);
    }

    public int getFirstColumn() {
        return this._firstCol;
    }

    public int getLastColumn() {
        return this._lastCol;
    }

    public int getColumnWidth() {
        return this._colWidth;
    }

    public int getXFIndex() {
        return this._xfIndex;
    }

    public boolean getHidden() {
        return hidden.isSet(this._options);
    }

    public int getOutlineLevel() {
        return outlevel.getValue(this._options);
    }

    public boolean getCollapsed() {
        return collapsed.isSet(this._options);
    }

    public boolean containsColumn(int columnIndex) {
        return this._firstCol <= columnIndex && columnIndex <= this._lastCol;
    }

    public boolean isAdjacentBefore(ColumnInfoRecord other) {
        return this._lastCol == other._firstCol - 1;
    }

    public boolean formatMatches(ColumnInfoRecord other) {
        if (this._xfIndex == other._xfIndex && this._options == other._options && this._colWidth == other._colWidth) {
            return true;
        }
        return false;
    }

    public short getSid() {
        return 125;
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(getFirstColumn());
        out.writeShort(getLastColumn());
        out.writeShort(getColumnWidth());
        out.writeShort(getXFIndex());
        out.writeShort(this._options);
        out.writeShort(this.field_6_reserved);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 12;
    }

    public String toString() {
        return "[COLINFO]\n" + "  colfirst = " + getFirstColumn() + "\n" + "  collast  = " + getLastColumn() + "\n" + "  colwidth = " + getColumnWidth() + "\n" + "  xfindex  = " + getXFIndex() + "\n" + "  options  = " + HexDump.shortToHex(this._options) + "\n" + "    hidden   = " + getHidden() + "\n" + "    olevel   = " + getOutlineLevel() + "\n" + "    collapsed= " + getCollapsed() + "\n" + "[/COLINFO]\n";
    }

    public Object clone() {
        ColumnInfoRecord rec = new ColumnInfoRecord();
        rec._firstCol = this._firstCol;
        rec._lastCol = this._lastCol;
        rec._colWidth = this._colWidth;
        rec._xfIndex = this._xfIndex;
        rec._options = this._options;
        rec.field_6_reserved = this.field_6_reserved;
        return rec;
    }
}
