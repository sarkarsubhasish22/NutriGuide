package org.apache.poi.hssf.record.constant;

import java.io.PrintStream;
import org.apache.poi.hssf.usermodel.HSSFErrorConstants;

public class ErrorConstant {
    private static final ErrorConstant DIV_0 = new ErrorConstant(7);
    private static final HSSFErrorConstants EC = null;
    private static final ErrorConstant NA = new ErrorConstant(42);
    private static final ErrorConstant NAME = new ErrorConstant(29);
    private static final ErrorConstant NULL = new ErrorConstant(0);
    private static final ErrorConstant NUM = new ErrorConstant(36);
    private static final ErrorConstant REF = new ErrorConstant(23);
    private static final ErrorConstant VALUE = new ErrorConstant(15);
    private final int _errorCode;

    private ErrorConstant(int errorCode) {
        this._errorCode = errorCode;
    }

    public int getErrorCode() {
        return this._errorCode;
    }

    public String getText() {
        if (HSSFErrorConstants.isValidCode(this._errorCode)) {
            return HSSFErrorConstants.getText(this._errorCode);
        }
        return "unknown error code (" + this._errorCode + ")";
    }

    public static ErrorConstant valueOf(int errorCode) {
        if (errorCode == 0) {
            return NULL;
        }
        if (errorCode == 7) {
            return DIV_0;
        }
        if (errorCode == 15) {
            return VALUE;
        }
        if (errorCode == 23) {
            return REF;
        }
        if (errorCode == 29) {
            return NAME;
        }
        if (errorCode == 36) {
            return NUM;
        }
        if (errorCode == 42) {
            return NA;
        }
        PrintStream printStream = System.err;
        printStream.println("Warning - unexpected error code (" + errorCode + ")");
        return new ErrorConstant(errorCode);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(64);
        sb.append(getClass().getName());
        sb.append(" [");
        sb.append(getText());
        sb.append("]");
        return sb.toString();
    }
}
