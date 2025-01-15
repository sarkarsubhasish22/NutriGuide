package org.apache.poi.hssf.record.formula;

import java.lang.reflect.Array;
import org.apache.poi.hssf.record.constant.ConstantValueParser;
import org.apache.poi.hssf.record.constant.ErrorConstant;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

public final class ArrayPtg extends Ptg {
    public static final int PLAIN_TOKEN_SIZE = 8;
    private static final int RESERVED_FIELD_LEN = 7;
    public static final byte sid = 32;
    private final Object[] _arrayValues;
    private final int _nColumns;
    private final int _nRows;
    private final int _reserved0Int;
    private final int _reserved1Short;
    private final int _reserved2Byte;

    ArrayPtg(int reserved0, int reserved1, int reserved2, int nColumns, int nRows, Object[] arrayValues) {
        this._reserved0Int = reserved0;
        this._reserved1Short = reserved1;
        this._reserved2Byte = reserved2;
        this._nColumns = nColumns;
        this._nRows = nRows;
        this._arrayValues = arrayValues;
    }

    public ArrayPtg(Object[][] values2d) {
        int nColumns = values2d[0].length;
        int nRows = values2d.length;
        short s = (short) nColumns;
        this._nColumns = s;
        short s2 = (short) nRows;
        this._nRows = s2;
        Object[] vv = new Object[(s * s2)];
        for (int r = 0; r < nRows; r++) {
            Object[] rowData = values2d[r];
            for (int c = 0; c < nColumns; c++) {
                vv[getValueIndex(c, r)] = rowData[c];
            }
        }
        this._arrayValues = vv;
        this._reserved0Int = 0;
        this._reserved1Short = 0;
        this._reserved2Byte = 0;
    }

    public Object[][] getTokenArrayValues() {
        if (this._arrayValues != null) {
            int i = this._nRows;
            int[] iArr = new int[2];
            iArr[1] = this._nColumns;
            iArr[0] = i;
            Object[][] result = (Object[][]) Array.newInstance(Object.class, iArr);
            for (int r = 0; r < this._nRows; r++) {
                Object[] rowData = result[r];
                for (int c = 0; c < this._nColumns; c++) {
                    rowData[c] = this._arrayValues[getValueIndex(c, r)];
                }
            }
            return result;
        }
        throw new IllegalStateException("array values not read yet");
    }

    public boolean isBaseToken() {
        return false;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("[ArrayPtg]\n");
        sb.append("nRows = ");
        sb.append(getRowCount());
        sb.append("\n");
        sb.append("nCols = ");
        sb.append(getColumnCount());
        sb.append("\n");
        if (this._arrayValues == null) {
            sb.append("  #values#uninitialised#\n");
        } else {
            sb.append("  ");
            sb.append(toFormulaString());
        }
        return sb.toString();
    }

    /* access modifiers changed from: package-private */
    public int getValueIndex(int colIx, int rowIx) {
        int i;
        if (colIx < 0 || colIx >= (i = this._nColumns)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Specified colIx (");
            sb.append(colIx);
            sb.append(") is outside the allowed range (0..");
            sb.append(this._nColumns - 1);
            sb.append(")");
            throw new IllegalArgumentException(sb.toString());
        } else if (rowIx >= 0 && rowIx < this._nRows) {
            return (i * rowIx) + colIx;
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Specified rowIx (");
            sb2.append(rowIx);
            sb2.append(") is outside the allowed range (0..");
            sb2.append(this._nRows - 1);
            sb2.append(")");
            throw new IllegalArgumentException(sb2.toString());
        }
    }

    public void write(LittleEndianOutput out) {
        out.writeByte(getPtgClass() + 32);
        out.writeInt(this._reserved0Int);
        out.writeShort(this._reserved1Short);
        out.writeByte(this._reserved2Byte);
    }

    public int writeTokenValueBytes(LittleEndianOutput out) {
        out.writeByte(this._nColumns - 1);
        out.writeShort(this._nRows - 1);
        ConstantValueParser.encode(out, this._arrayValues);
        return ConstantValueParser.getEncodedSize(this._arrayValues) + 3;
    }

    public int getRowCount() {
        return this._nRows;
    }

    public int getColumnCount() {
        return this._nColumns;
    }

    public int getSize() {
        return ConstantValueParser.getEncodedSize(this._arrayValues) + 11;
    }

    public String toFormulaString() {
        StringBuffer b = new StringBuffer();
        b.append("{");
        for (int y = 0; y < getRowCount(); y++) {
            if (y > 0) {
                b.append(";");
            }
            for (int x = 0; x < getColumnCount(); x++) {
                if (x > 0) {
                    b.append(",");
                }
                b.append(getConstantText(this._arrayValues[getValueIndex(x, y)]));
            }
        }
        b.append("}");
        return b.toString();
    }

    private static String getConstantText(Object o) {
        if (o == null) {
            throw new RuntimeException("Array item cannot be null");
        } else if (o instanceof String) {
            return "\"" + ((String) o) + "\"";
        } else if (o instanceof Double) {
            return NumberToTextConverter.toText(((Double) o).doubleValue());
        } else {
            if (o instanceof Boolean) {
                return ((Boolean) o).booleanValue() ? "TRUE" : "FALSE";
            }
            if (o instanceof ErrorConstant) {
                return ((ErrorConstant) o).getText();
            }
            throw new IllegalArgumentException("Unexpected constant class (" + o.getClass().getName() + ")");
        }
    }

    public byte getDefaultOperandClass() {
        return Ptg.CLASS_ARRAY;
    }

    static final class Initial extends Ptg {
        private final int _reserved0;
        private final int _reserved1;
        private final int _reserved2;

        public Initial(LittleEndianInput in) {
            this._reserved0 = in.readInt();
            this._reserved1 = in.readUShort();
            this._reserved2 = in.readUByte();
        }

        private static RuntimeException invalid() {
            throw new IllegalStateException("This object is a partially initialised tArray, and cannot be used as a Ptg");
        }

        public byte getDefaultOperandClass() {
            throw invalid();
        }

        public int getSize() {
            return 8;
        }

        public boolean isBaseToken() {
            return false;
        }

        public String toFormulaString() {
            throw invalid();
        }

        public void write(LittleEndianOutput out) {
            throw invalid();
        }

        public ArrayPtg finishReading(LittleEndianInput in) {
            int nColumns = in.readUByte() + 1;
            short nRows = (short) (in.readShort() + 1);
            ArrayPtg result = new ArrayPtg(this._reserved0, this._reserved1, this._reserved2, nColumns, nRows, ConstantValueParser.parse(in, nRows * nColumns));
            result.setClass(getPtgClass());
            return result;
        }
    }
}
