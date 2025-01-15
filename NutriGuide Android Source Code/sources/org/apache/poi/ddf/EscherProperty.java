package org.apache.poi.ddf;

public abstract class EscherProperty {
    private short _id;

    public abstract int serializeComplexPart(byte[] bArr, int i);

    public abstract int serializeSimplePart(byte[] bArr, int i);

    public EscherProperty(short id) {
        this._id = id;
    }

    public EscherProperty(short propertyNumber, boolean isComplex, boolean isBlipId) {
        this._id = (short) ((isComplex ? (short) 32768 : 0) + propertyNumber + (isBlipId ? 16384 : 0));
    }

    public short getId() {
        return this._id;
    }

    public short getPropertyNumber() {
        return (short) (this._id & 16383);
    }

    public boolean isComplex() {
        return (this._id & Short.MIN_VALUE) != 0;
    }

    public boolean isBlipId() {
        return (this._id & 16384) != 0;
    }

    public String getName() {
        return EscherProperties.getPropertyName(this._id);
    }

    public int getPropertySize() {
        return 6;
    }
}
