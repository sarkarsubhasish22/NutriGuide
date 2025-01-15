package org.apache.poi.hssf.record;

import org.apache.poi.ss.usermodel.ErrorConstants;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class BoolErrRecord extends CellRecord {
    public static final short sid = 517;
    private boolean _isError;
    private int _value;

    public BoolErrRecord() {
    }

    public BoolErrRecord(RecordInputStream in) {
        super(in);
        int remaining = in.remaining();
        if (remaining == 2) {
            this._value = in.readByte();
        } else if (remaining == 3) {
            this._value = in.readUShort();
        } else {
            throw new RecordFormatException("Unexpected size (" + in.remaining() + ") for BOOLERR record.");
        }
        int flag = in.readUByte();
        if (flag == 0) {
            this._isError = false;
        } else if (flag == 1) {
            this._isError = true;
        } else {
            throw new RecordFormatException("Unexpected isError flag (" + flag + ") for BOOLERR record.");
        }
    }

    public void setValue(boolean value) {
        this._value = value;
        this._isError = false;
    }

    public void setValue(byte value) {
        if (value == 0 || value == 7 || value == 15 || value == 23 || value == 29 || value == 36 || value == 42) {
            this._value = value;
            this._isError = true;
            return;
        }
        throw new IllegalArgumentException("Error Value can only be 0,7,15,23,29,36 or 42. It cannot be " + value);
    }

    public boolean getBooleanValue() {
        return this._value != 0;
    }

    public byte getErrorValue() {
        return (byte) this._value;
    }

    public boolean isBoolean() {
        return !this._isError;
    }

    public boolean isError() {
        return this._isError;
    }

    /* access modifiers changed from: protected */
    public String getRecordName() {
        return "BOOLERR";
    }

    /* access modifiers changed from: protected */
    public void appendValueText(StringBuilder sb) {
        if (isBoolean()) {
            sb.append("  .boolVal = ");
            sb.append(getBooleanValue());
            return;
        }
        sb.append("  .errCode = ");
        sb.append(ErrorConstants.getText(getErrorValue()));
        sb.append(" (");
        sb.append(HexDump.byteToHex(getErrorValue()));
        sb.append(")");
    }

    /* access modifiers changed from: protected */
    public void serializeValue(LittleEndianOutput out) {
        out.writeByte(this._value);
        out.writeByte(this._isError ? 1 : 0);
    }

    /* access modifiers changed from: protected */
    public int getValueDataSize() {
        return 2;
    }

    public short getSid() {
        return 517;
    }

    public Object clone() {
        BoolErrRecord rec = new BoolErrRecord();
        copyBaseFields(rec);
        rec._value = this._value;
        rec._isError = this._isError;
        return rec;
    }
}
