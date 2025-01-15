package org.apache.poi.hpsf;

public abstract class VariantTypeException extends HPSFException {
    private Object value;
    private long variantType;

    public VariantTypeException(long variantType2, Object value2, String msg) {
        super(msg);
        this.variantType = variantType2;
        this.value = value2;
    }

    public long getVariantType() {
        return this.variantType;
    }

    public Object getValue() {
        return this.value;
    }
}
