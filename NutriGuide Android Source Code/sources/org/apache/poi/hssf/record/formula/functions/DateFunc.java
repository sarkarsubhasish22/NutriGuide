package org.apache.poi.hssf.record.formula.functions;

import java.util.GregorianCalendar;
import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.formula.eval.EvaluationException;
import org.apache.poi.hssf.record.formula.eval.NumberEval;
import org.apache.poi.hssf.record.formula.eval.ValueEval;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;

public final class DateFunc extends Fixed3ArgFunction {
    public static final Function instance = new DateFunc();

    private DateFunc() {
    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, ValueEval arg2) {
        double d1;
        int i = srcRowIndex;
        int i2 = srcColumnIndex;
        try {
            double d0 = NumericFunction.singleOperandEvaluate(arg0, i, i2);
            try {
                d1 = NumericFunction.singleOperandEvaluate(arg1, i, i2);
            } catch (EvaluationException e) {
                e = e;
                ValueEval valueEval = arg2;
                return e.getErrorEval();
            }
            try {
                double result = evaluate(getYear(d0), (int) (d1 - 1.0d), (int) NumericFunction.singleOperandEvaluate(arg2, i, i2));
                try {
                    NumericFunction.checkValue(result);
                    return new NumberEval(result);
                } catch (EvaluationException e2) {
                    e = e2;
                    return e.getErrorEval();
                }
            } catch (EvaluationException e3) {
                e = e3;
                return e.getErrorEval();
            }
        } catch (EvaluationException e4) {
            e = e4;
            ValueEval valueEval2 = arg1;
            ValueEval valueEval3 = arg2;
            return e.getErrorEval();
        }
    }

    private static double evaluate(int year, int month, int pDay) throws EvaluationException {
        if (year < 0 || month < 0 || pDay < 0) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        } else if (year == 1900 && month == 1 && pDay == 29) {
            return 60.0d;
        } else {
            int day = pDay;
            if (year == 1900 && ((month == 0 && day >= 60) || (month == 1 && day >= 30))) {
                day--;
            }
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.set(year, month, day, 0, 0, 0);
            gregorianCalendar.set(14, 0);
            return HSSFDateUtil.getExcelDate(gregorianCalendar.getTime(), false);
        }
    }

    private static int getYear(double d) {
        int year = (int) d;
        if (year < 0) {
            return -1;
        }
        return year < 1900 ? year + 1900 : year;
    }
}
