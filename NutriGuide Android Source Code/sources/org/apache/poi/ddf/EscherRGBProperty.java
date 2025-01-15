package org.apache.poi.ddf;

public class EscherRGBProperty extends EscherSimpleProperty {
    public EscherRGBProperty(short propertyNumber, int rgbColor) {
        super(propertyNumber, rgbColor);
    }

    public int getRgbColor() {
        return this.propertyValue;
    }

    public byte getRed() {
        return (byte) (this.propertyValue & 255);
    }

    public byte getGreen() {
        return (byte) ((this.propertyValue >> 8) & 255);
    }

    public byte getBlue() {
        return (byte) ((this.propertyValue >> 16) & 255);
    }
}
