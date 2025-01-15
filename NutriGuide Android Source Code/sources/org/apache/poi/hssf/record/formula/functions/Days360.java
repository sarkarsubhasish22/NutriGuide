package org.apache.poi.hssf.record.formula.functions;

import java.util.Calendar;
import java.util.GregorianCalendar;
import org.apache.poi.hssf.record.formula.eval.EvaluationException;
import org.apache.poi.hssf.record.formula.eval.NumberEval;
import org.apache.poi.hssf.record.formula.eval.OperandResolver;
import org.apache.poi.hssf.record.formula.eval.ValueEval;
import org.apache.poi.ss.usermodel.DateUtil;

public class Days360 extends Var2or3ArgFunction {
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
        try {
            return new NumberEval(evaluate(NumericFunction.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex), NumericFunction.singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex), false));
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, ValueEval arg2) {
        try {
            double d0 = NumericFunction.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
            double d1 = NumericFunction.singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex);
            boolean z = false;
            Boolean method = OperandResolver.coerceValueToBoolean(OperandResolver.getSingleValue(arg2, srcRowIndex, srcColumnIndex), false);
            if (method != null) {
                z = method.booleanValue();
            }
            return new NumberEval(evaluate(d0, d1, z));
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    private static double evaluate(double d0, double d1, boolean method) {
        Calendar startingDate = getStartingDate(d0);
        Calendar endingDate = getEndingDateAccordingToStartingDate(d1, startingDate);
        return (double) (((long) ((((endingDate.get(1) - startingDate.get(1)) * 360) + (endingDate.get(2) * 30)) + endingDate.get(5))) - ((long) ((startingDate.get(2) * 30) + startingDate.get(5))));
    }

    private static Calendar getDate(double date) {
        Calendar processedDate = new GregorianCalendar();
        processedDate.setTime(DateUtil.getJavaDate(date, false));
        return processedDate;
    }

    private static Calendar getStartingDate(double date) {
        Calendar startingDate = getDate(date);
        if (isLastDayOfMonth(startingDate)) {
            startingDate.set(5, 30);
        }
        return startingDate;
    }

    private static Calendar getEndingDateAccordingToStartingDate(double date, Calendar startingDate) {
        Calendar endingDate = getDate(date);
        endingDate.setTime(DateUtil.getJavaDate(date, false));
        if (!isLastDayOfMonth(endingDate) || startingDate.get(5) >= 30) {
            return endingDate;
        }
        return getFirstDayOfNextMonth(endingDate);
    }

    private static boolean isLastDayOfMonth(Calendar date) {
        Calendar clone = (Calendar) date.clone();
        clone.add(2, 1);
        clone.add(5, -1);
        if (date.get(5) == clone.get(5)) {
            return true;
        }
        return false;
    }

    private static Calendar getFirstDayOfNextMonth(Calendar date) {
        Calendar newDate = (Calendar) date.clone();
        if (date.get(2) < 11) {
            newDate.set(2, date.get(2) + 1);
        } else {
            newDate.set(2, 1);
            newDate.set(1, date.get(1) + 1);
        }
        newDate.set(5, 1);
        return newDate;
    }
}
