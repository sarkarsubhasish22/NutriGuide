package org.apache.poi.hssf.record.formula.functions;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.formula.eval.EvaluationException;
import org.apache.poi.hssf.record.formula.eval.NumberEval;
import org.apache.poi.hssf.record.formula.eval.OperandResolver;
import org.apache.poi.hssf.record.formula.eval.ValueEval;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;

public final class CalendarFieldFunction extends Fixed1ArgFunction {
    public static final Function DAY = new CalendarFieldFunction(5, false);
    public static final Function MONTH = new CalendarFieldFunction(2, true);
    public static final Function YEAR = new CalendarFieldFunction(1, false);
    private final int _dateFieldId;
    private final boolean _needsOneBaseAdjustment;

    private CalendarFieldFunction(int dateFieldId, boolean needsOneBaseAdjustment) {
        this._dateFieldId = dateFieldId;
        this._needsOneBaseAdjustment = needsOneBaseAdjustment;
    }

    public final ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
        try {
            int val = OperandResolver.coerceValueToInt(OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex));
            if (val < 0) {
                return ErrorEval.NUM_ERROR;
            }
            return new NumberEval((double) getCalField(val));
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    private int getCalField(int serialDay) {
        if (serialDay == 0) {
            int i = this._dateFieldId;
            if (i == 1) {
                return 1900;
            }
            if (i == 2) {
                return 1;
            }
            if (i == 5) {
                return 0;
            }
            throw new IllegalStateException("bad date field " + this._dateFieldId);
        }
        Date d = HSSFDateUtil.getJavaDate((double) serialDay, false);
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        int result = c.get(this._dateFieldId);
        if (this._needsOneBaseAdjustment) {
            return result + 1;
        }
        return result;
    }
}
