package org.apache.poi.hssf.record;

import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.hssf.util.CellRangeAddress8Bit;
import org.apache.poi.ss.formula.Formula;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

public final class ArrayRecord extends SharedValueRecordBase {
    private static final int OPT_ALWAYS_RECALCULATE = 1;
    private static final int OPT_CALCULATE_ON_OPEN = 2;
    public static final short sid = 545;
    private int _field3notUsed;
    private Formula _formula;
    private int _options;

    public ArrayRecord(RecordInputStream in) {
        super((LittleEndianInput) in);
        this._options = in.readUShort();
        this._field3notUsed = in.readInt();
        this._formula = Formula.read(in.readUShort(), in, in.available());
    }

    public ArrayRecord(Formula formula, CellRangeAddress8Bit range) {
        super(range);
        this._options = 0;
        this._field3notUsed = 0;
        this._formula = formula;
    }

    public boolean isAlwaysRecalculate() {
        return (this._options & 1) != 0;
    }

    public boolean isCalculateOnOpen() {
        return (this._options & 2) != 0;
    }

    public Ptg[] getFormulaTokens() {
        return this._formula.getTokens();
    }

    /* access modifiers changed from: protected */
    public int getExtraDataSize() {
        return this._formula.getEncodedSize() + 6;
    }

    /* access modifiers changed from: protected */
    public void serializeExtraData(LittleEndianOutput out) {
        out.writeShort(this._options);
        out.writeInt(this._field3notUsed);
        this._formula.serialize(out);
    }

    public short getSid() {
        return sid;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getName());
        sb.append(" [ARRAY]\n");
        sb.append(" range=");
        sb.append(getRange().toString());
        sb.append("\n");
        sb.append(" options=");
        sb.append(HexDump.shortToHex(this._options));
        sb.append("\n");
        sb.append(" notUsed=");
        sb.append(HexDump.intToHex(this._field3notUsed));
        sb.append("\n");
        sb.append(" formula:");
        sb.append("\n");
        Ptg[] ptgs = this._formula.getTokens();
        for (Ptg ptg : ptgs) {
            sb.append(ptg.toString());
            sb.append(ptg.getRVAType());
            sb.append("\n");
        }
        sb.append("]");
        return sb.toString();
    }
}
