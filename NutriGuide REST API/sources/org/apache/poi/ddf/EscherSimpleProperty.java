package org.apache.poi.ddf;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

public class EscherSimpleProperty extends EscherProperty {
    protected int propertyValue;

    public EscherSimpleProperty(short id, int propertyValue2) {
        super(id);
        this.propertyValue = propertyValue2;
    }

    public EscherSimpleProperty(short propertyNumber, boolean isComplex, boolean isBlipId, int propertyValue2) {
        super(propertyNumber, isComplex, isBlipId);
        this.propertyValue = propertyValue2;
    }

    public int serializeSimplePart(byte[] data, int offset) {
        LittleEndian.putShort(data, offset, getId());
        LittleEndian.putInt(data, offset + 2, this.propertyValue);
        return 6;
    }

    public int serializeComplexPart(byte[] data, int pos) {
        return 0;
    }

    public int getPropertyValue() {
        return this.propertyValue;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EscherSimpleProperty)) {
            return false;
        }
        EscherSimpleProperty escherSimpleProperty = (EscherSimpleProperty) o;
        if (this.propertyValue == escherSimpleProperty.propertyValue && getId() == escherSimpleProperty.getId()) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.propertyValue;
    }

    public String toString() {
        return "propNum: " + getPropertyNumber() + ", RAW: 0x" + HexDump.toHex(getId()) + ", propName: " + EscherProperties.getPropertyName(getPropertyNumber()) + ", complex: " + isComplex() + ", blipId: " + isBlipId() + ", value: " + this.propertyValue + " (0x" + HexDump.toHex(this.propertyValue) + ")";
    }
}
