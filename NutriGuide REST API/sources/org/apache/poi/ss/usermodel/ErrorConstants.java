package org.apache.poi.ss.usermodel;

public class ErrorConstants {
    public static final int ERROR_DIV_0 = 7;
    public static final int ERROR_NA = 42;
    public static final int ERROR_NAME = 29;
    public static final int ERROR_NULL = 0;
    public static final int ERROR_NUM = 36;
    public static final int ERROR_REF = 23;
    public static final int ERROR_VALUE = 15;

    protected ErrorConstants() {
    }

    public static final String getText(int errorCode) {
        if (errorCode == 0) {
            return "#NULL!";
        }
        if (errorCode == 7) {
            return "#DIV/0!";
        }
        if (errorCode == 15) {
            return "#VALUE!";
        }
        if (errorCode == 23) {
            return "#REF!";
        }
        if (errorCode == 29) {
            return "#NAME?";
        }
        if (errorCode == 36) {
            return "#NUM!";
        }
        if (errorCode == 42) {
            return "#N/A";
        }
        throw new IllegalArgumentException("Bad error code (" + errorCode + ")");
    }

    public static final boolean isValidCode(int errorCode) {
        if (errorCode == 0 || errorCode == 7 || errorCode == 15 || errorCode == 23 || errorCode == 29 || errorCode == 36 || errorCode == 42) {
            return true;
        }
        return false;
    }
}
