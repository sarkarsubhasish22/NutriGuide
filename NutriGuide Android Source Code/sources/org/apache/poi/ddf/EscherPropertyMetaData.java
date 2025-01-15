package org.apache.poi.ddf;

public class EscherPropertyMetaData {
    public static final byte TYPE_ARRAY = 5;
    public static final byte TYPE_BOOLEAN = 1;
    public static final byte TYPE_RGB = 2;
    public static final byte TYPE_SHAPEPATH = 3;
    public static final byte TYPE_SIMPLE = 4;
    public static final byte TYPE_UNKNOWN = 0;
    private String description;
    private byte type;

    public EscherPropertyMetaData(String description2) {
        this.description = description2;
    }

    public EscherPropertyMetaData(String description2, byte type2) {
        this.description = description2;
        this.type = type2;
    }

    public String getDescription() {
        return this.description;
    }

    public byte getType() {
        return this.type;
    }
}
