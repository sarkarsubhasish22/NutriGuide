package org.apache.poi.hssf.record.formula;

import org.apache.poi.hssf.usermodel.HSSFErrorConstants;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

public final class ErrPtg extends ScalarConstantPtg {
    public static final ErrPtg DIV_ZERO = new ErrPtg(7);
    private static final HSSFErrorConstants EC = null;
    public static final ErrPtg NAME_INVALID = new ErrPtg(29);
    public static final ErrPtg NULL_INTERSECTION = new ErrPtg(0);
    public static final ErrPtg NUM_ERROR = new ErrPtg(36);
    public static final ErrPtg N_A = new ErrPtg(42);
    public static final ErrPtg REF_INVALID = new ErrPtg(23);
    private static final int SIZE = 2;
    public static final ErrPtg VALUE_INVALID = new ErrPtg(15);
    public static final short sid = 28;
    private final int field_1_error_code;

    private ErrPtg(int errorCode) {
        if (HSSFErrorConstants.isValidCode(errorCode)) {
            this.field_1_error_code = errorCode;
            return;
        }
        throw new IllegalArgumentException("Invalid error code (" + errorCode + ")");
    }

    public static ErrPtg read(LittleEndianInput in) {
        return valueOf(in.readByte());
    }

    public void write(LittleEndianOutput out) {
        out.writeByte(getPtgClass() + 28);
        out.writeByte(this.field_1_error_code);
    }

    public String toFormulaString() {
        return HSSFErrorConstants.getText(this.field_1_error_code);
    }

    public int getSize() {
        return 2;
    }

    public int getErrorCode() {
        return this.field_1_error_code;
    }

    public static ErrPtg valueOf(int code) {
        if (code == 0) {
            return NULL_INTERSECTION;
        }
        if (code == 7) {
            return DIV_ZERO;
        }
        if (code == 15) {
            return VALUE_INVALID;
        }
        if (code == 23) {
            return REF_INVALID;
        }
        if (code == 29) {
            return NAME_INVALID;
        }
        if (code == 36) {
            return NUM_ERROR;
        }
        if (code == 42) {
            return N_A;
        }
        throw new RuntimeException("Unexpected error code (" + code + ")");
    }
}
