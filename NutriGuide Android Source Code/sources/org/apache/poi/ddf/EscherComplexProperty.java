package org.apache.poi.ddf;

import java.util.Arrays;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

public class EscherComplexProperty extends EscherProperty {
    protected byte[] _complexData;

    public EscherComplexProperty(short id, byte[] complexData) {
        super(id);
        this._complexData = complexData;
    }

    public EscherComplexProperty(short propertyNumber, boolean isBlipId, byte[] complexData) {
        super(propertyNumber, true, isBlipId);
        this._complexData = complexData;
    }

    public int serializeSimplePart(byte[] data, int pos) {
        LittleEndian.putShort(data, pos, getId());
        LittleEndian.putInt(data, pos + 2, this._complexData.length);
        return 6;
    }

    public int serializeComplexPart(byte[] data, int pos) {
        byte[] bArr = this._complexData;
        System.arraycopy(bArr, 0, data, pos, bArr.length);
        return this._complexData.length;
    }

    public byte[] getComplexData() {
        return this._complexData;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o instanceof EscherComplexProperty) && Arrays.equals(this._complexData, ((EscherComplexProperty) o)._complexData)) {
            return true;
        }
        return false;
    }

    public int getPropertySize() {
        return this._complexData.length + 6;
    }

    public int hashCode() {
        return getId() * 11;
    }

    public String toString() {
        String dataStr = HexDump.toHex(this._complexData, 32);
        return "propNum: " + getPropertyNumber() + ", propName: " + EscherProperties.getPropertyName(getPropertyNumber()) + ", complex: " + isComplex() + ", blipId: " + isBlipId() + ", data: " + System.getProperty("line.separator") + dataStr;
    }
}
