package org.apache.poi.hssf.record.formula;

import org.apache.poi.util.LittleEndianInput;

public final class AreaNPtg extends Area2DPtgBase {
    public static final short sid = 45;

    public AreaNPtg(LittleEndianInput in) {
        super(in);
    }

    /* access modifiers changed from: protected */
    public byte getSid() {
        return 45;
    }
}
