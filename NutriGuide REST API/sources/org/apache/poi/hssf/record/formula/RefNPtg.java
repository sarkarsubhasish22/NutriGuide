package org.apache.poi.hssf.record.formula;

import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

public final class RefNPtg extends Ref2DPtgBase {
    public static final byte sid = 44;

    public /* bridge */ /* synthetic */ void write(LittleEndianOutput x0) {
        super.write(x0);
    }

    public RefNPtg(LittleEndianInput in) {
        super(in);
    }

    /* access modifiers changed from: protected */
    public byte getSid() {
        return sid;
    }
}
