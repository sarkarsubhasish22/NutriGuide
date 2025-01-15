package org.apache.poi.ss.usermodel;

import org.apache.poi.hssf.record.formula.eval.ErrorEval;

public final class CellValue {
    public static final CellValue FALSE = new CellValue(4, 0.0d, false, (String) null, 0);
    public static final CellValue TRUE = new CellValue(4, 0.0d, true, (String) null, 0);
    private final boolean _booleanValue;
    private final int _cellType;
    private final int _errorCode;
    private final double _numberValue;
    private final String _textValue;

    private CellValue(int cellType, double numberValue, boolean booleanValue, String textValue, int errorCode) {
        this._cellType = cellType;
        this._numberValue = numberValue;
        this._booleanValue = booleanValue;
        this._textValue = textValue;
        this._errorCode = errorCode;
    }

    public CellValue(double numberValue) {
        this(0, numberValue, false, (String) null, 0);
    }

    public static CellValue valueOf(boolean booleanValue) {
        return booleanValue ? TRUE : FALSE;
    }

    public CellValue(String stringValue) {
        this(1, 0.0d, false, stringValue, 0);
    }

    public static CellValue getError(int errorCode) {
        return new CellValue(5, 0.0d, false, (String) null, errorCode);
    }

    public boolean getBooleanValue() {
        return this._booleanValue;
    }

    public double getNumberValue() {
        return this._numberValue;
    }

    public String getStringValue() {
        return this._textValue;
    }

    public int getCellType() {
        return this._cellType;
    }

    public byte getErrorValue() {
        return (byte) this._errorCode;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(64);
        sb.append(getClass().getName());
        sb.append(" [");
        sb.append(formatAsString());
        sb.append("]");
        return sb.toString();
    }

    public String formatAsString() {
        int i = this._cellType;
        if (i == 0) {
            return String.valueOf(this._numberValue);
        }
        if (i == 1) {
            return '\"' + this._textValue + '\"';
        } else if (i == 4) {
            return this._booleanValue ? "TRUE" : "FALSE";
        } else {
            if (i == 5) {
                return ErrorEval.getText(this._errorCode);
            }
            return "<error unexpected cell type " + this._cellType + ">";
        }
    }
}
