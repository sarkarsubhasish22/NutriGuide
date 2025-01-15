package org.apache.poi.ss.usermodel;

import java.util.HashMap;
import java.util.Map;

public enum FormulaError {
    NULL(0, "#NULL!"),
    DIV0(7, "#DIV/0!"),
    VALUE(15, "#VALUE!"),
    REF(23, "#REF!"),
    NAME(29, "#NAME?"),
    NUM(36, "#NUM!"),
    NA(42, "#N/A");
    
    private static Map<Byte, FormulaError> imap;
    private static Map<String, FormulaError> smap;
    private String repr;
    private byte type;

    static {
        smap = new HashMap();
        imap = new HashMap();
        for (FormulaError error : values()) {
            imap.put(Byte.valueOf(error.getCode()), error);
            smap.put(error.getString(), error);
        }
    }

    private FormulaError(int type2, String repr2) {
        this.type = (byte) type2;
        this.repr = repr2;
    }

    public byte getCode() {
        return this.type;
    }

    public String getString() {
        return this.repr;
    }

    public static FormulaError forInt(byte type2) {
        FormulaError err = imap.get(Byte.valueOf(type2));
        if (err != null) {
            return err;
        }
        throw new IllegalArgumentException("Unknown error type: " + type2);
    }

    public static FormulaError forString(String code) {
        FormulaError err = smap.get(code);
        if (err != null) {
            return err;
        }
        throw new IllegalArgumentException("Unknown error code: " + code);
    }
}
