package org.apache.poi.ddf;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

public final class EscherArrayProperty extends EscherComplexProperty {
    private static final int FIXED_SIZE = 6;
    private boolean emptyComplexPart = false;
    private boolean sizeIncludesHeaderSize = true;

    public EscherArrayProperty(short id, byte[] complexData) {
        super(id, checkComplexData(complexData));
        boolean z = true;
        this.emptyComplexPart = complexData.length != 0 ? false : z;
    }

    public EscherArrayProperty(short propertyNumber, boolean isBlipId, byte[] complexData) {
        super(propertyNumber, isBlipId, checkComplexData(complexData));
    }

    private static byte[] checkComplexData(byte[] complexData) {
        if (complexData == null || complexData.length == 0) {
            return new byte[6];
        }
        return complexData;
    }

    public int getNumberOfElementsInArray() {
        return LittleEndian.getUShort(this._complexData, 0);
    }

    public void setNumberOfElementsInArray(int numberOfElements) {
        int expectedArraySize = (getActualSizeOfElements(getSizeOfElements()) * numberOfElements) + 6;
        if (expectedArraySize != this._complexData.length) {
            byte[] newArray = new byte[expectedArraySize];
            System.arraycopy(this._complexData, 0, newArray, 0, this._complexData.length);
            this._complexData = newArray;
        }
        LittleEndian.putShort(this._complexData, 0, (short) numberOfElements);
    }

    public int getNumberOfElementsInMemory() {
        return LittleEndian.getUShort(this._complexData, 2);
    }

    public void setNumberOfElementsInMemory(int numberOfElements) {
        int expectedArraySize = (getActualSizeOfElements(getSizeOfElements()) * numberOfElements) + 6;
        if (expectedArraySize != this._complexData.length) {
            byte[] newArray = new byte[expectedArraySize];
            System.arraycopy(this._complexData, 0, newArray, 0, expectedArraySize);
            this._complexData = newArray;
        }
        LittleEndian.putShort(this._complexData, 2, (short) numberOfElements);
    }

    public short getSizeOfElements() {
        return LittleEndian.getShort(this._complexData, 4);
    }

    public void setSizeOfElements(int sizeOfElements) {
        LittleEndian.putShort(this._complexData, 4, (short) sizeOfElements);
        int expectedArraySize = (getNumberOfElementsInArray() * getActualSizeOfElements(getSizeOfElements())) + 6;
        if (expectedArraySize != this._complexData.length) {
            byte[] newArray = new byte[expectedArraySize];
            System.arraycopy(this._complexData, 0, newArray, 0, 6);
            this._complexData = newArray;
        }
    }

    public byte[] getElement(int index) {
        int actualSize = getActualSizeOfElements(getSizeOfElements());
        byte[] result = new byte[actualSize];
        System.arraycopy(this._complexData, (index * actualSize) + 6, result, 0, result.length);
        return result;
    }

    public void setElement(int index, byte[] element) {
        int actualSize = getActualSizeOfElements(getSizeOfElements());
        System.arraycopy(element, 0, this._complexData, (index * actualSize) + 6, actualSize);
    }

    public String toString() {
        StringBuffer results = new StringBuffer();
        results.append("    {EscherArrayProperty:\n");
        results.append("     Num Elements: " + getNumberOfElementsInArray() + 10);
        results.append("     Num Elements In Memory: " + getNumberOfElementsInMemory() + 10);
        results.append("     Size of elements: " + getSizeOfElements() + 10);
        for (int i = 0; i < getNumberOfElementsInArray(); i++) {
            results.append("     Element " + i + ": " + HexDump.toHex(getElement(i)) + 10);
        }
        results.append("}\n");
        return "propNum: " + getPropertyNumber() + ", propName: " + EscherProperties.getPropertyName(getPropertyNumber()) + ", complex: " + isComplex() + ", blipId: " + isBlipId() + ", data: " + 10 + results.toString();
    }

    public int setArrayData(byte[] data, int offset) {
        if (this.emptyComplexPart) {
            this._complexData = new byte[0];
        } else {
            short numElements = LittleEndian.getShort(data, offset);
            LittleEndian.getShort(data, offset + 2);
            int arraySize = getActualSizeOfElements(LittleEndian.getShort(data, offset + 4)) * numElements;
            if (arraySize == this._complexData.length) {
                this._complexData = new byte[(arraySize + 6)];
                this.sizeIncludesHeaderSize = false;
            }
            System.arraycopy(data, offset, this._complexData, 0, this._complexData.length);
        }
        return this._complexData.length;
    }

    public int serializeSimplePart(byte[] data, int pos) {
        LittleEndian.putShort(data, pos, getId());
        int recordSize = this._complexData.length;
        if (!this.sizeIncludesHeaderSize) {
            recordSize -= 6;
        }
        LittleEndian.putInt(data, pos + 2, recordSize);
        return 6;
    }

    public static int getActualSizeOfElements(short sizeOfElements) {
        if (sizeOfElements < 0) {
            return (short) ((-sizeOfElements) >> 2);
        }
        return sizeOfElements;
    }
}
