package org.apache.poi.hssf.record.formula;

import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

public final class RefPtg extends Ref2DPtgBase {
    public static final byte sid = 36;

    public /* bridge */ /* synthetic */ void write(LittleEndianOutput x0) {
        super.write(x0);
    }

    public RefPtg(String cellref) {
        super(new CellReference(cellref));
    }

    public RefPtg(int row, int column, boolean isRowRelative, boolean isColumnRelative) {
        super(row, column, isRowRelative, isColumnRelative);
    }

    public RefPtg(LittleEndianInput in) {
        super(in);
    }

    public RefPtg(CellReference cr) {
        super(cr);
    }

    /* access modifiers changed from: protected */
    public byte getSid() {
        return sid;
    }
}
