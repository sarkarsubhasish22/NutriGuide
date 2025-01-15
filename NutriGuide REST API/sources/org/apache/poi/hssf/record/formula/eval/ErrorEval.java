package org.apache.poi.hssf.record.formula.eval;

import org.apache.poi.hssf.usermodel.HSSFErrorConstants;

public final class ErrorEval implements ValueEval {
    public static final ErrorEval CIRCULAR_REF_ERROR = new ErrorEval(CIRCULAR_REF_ERROR_CODE);
    private static final int CIRCULAR_REF_ERROR_CODE = -60;
    public static final ErrorEval DIV_ZERO = new ErrorEval(7);
    private static final HSSFErrorConstants EC = null;
    private static final int FUNCTION_NOT_IMPLEMENTED_CODE = -30;
    public static final ErrorEval NA = new ErrorEval(42);
    public static final ErrorEval NAME_INVALID = new ErrorEval(29);
    public static final ErrorEval NULL_INTERSECTION = new ErrorEval(0);
    public static final ErrorEval NUM_ERROR = new ErrorEval(36);
    public static final ErrorEval REF_INVALID = new ErrorEval(23);
    public static final ErrorEval VALUE_INVALID = new ErrorEval(15);
    private int _errorCode;

    public static ErrorEval valueOf(int errorCode) {
        if (errorCode == CIRCULAR_REF_ERROR_CODE) {
            return CIRCULAR_REF_ERROR;
        }
        if (errorCode == 0) {
            return NULL_INTERSECTION;
        }
        if (errorCode == 7) {
            return DIV_ZERO;
        }
        if (errorCode == 15) {
            return VALUE_INVALID;
        }
        if (errorCode == 23) {
            return REF_INVALID;
        }
        if (errorCode == 29) {
            return NAME_INVALID;
        }
        if (errorCode == 36) {
            return NUM_ERROR;
        }
        if (errorCode == 42) {
            return NA;
        }
        throw new RuntimeException("Unexpected error code (" + errorCode + ")");
    }

    public static String getText(int errorCode) {
        if (HSSFErrorConstants.isValidCode(errorCode)) {
            return HSSFErrorConstants.getText(errorCode);
        }
        if (errorCode == CIRCULAR_REF_ERROR_CODE) {
            return "~CIRCULAR~REF~";
        }
        if (errorCode == FUNCTION_NOT_IMPLEMENTED_CODE) {
            return "~FUNCTION~NOT~IMPLEMENTED~";
        }
        return "~non~std~err(" + errorCode + ")~";
    }

    private ErrorEval(int errorCode) {
        this._errorCode = errorCode;
    }

    public int getErrorCode() {
        return this._errorCode;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(64);
        sb.append(getClass().getName());
        sb.append(" [");
        sb.append(getText(this._errorCode));
        sb.append("]");
        return sb.toString();
    }
}
