package org.apache.poi.ddf;

public class EscherBoolProperty extends EscherSimpleProperty {
    public EscherBoolProperty(short propertyNumber, int value) {
        super(propertyNumber, value);
    }

    public boolean isTrue() {
        return this.propertyValue != 0;
    }

    public boolean isFalse() {
        return this.propertyValue == 0;
    }
}
