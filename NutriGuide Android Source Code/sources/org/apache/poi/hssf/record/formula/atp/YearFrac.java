package org.apache.poi.hssf.record.formula.atp;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;
import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.formula.eval.EvaluationException;
import org.apache.poi.hssf.record.formula.eval.NumberEval;
import org.apache.poi.hssf.record.formula.eval.OperandResolver;
import org.apache.poi.hssf.record.formula.eval.StringEval;
import org.apache.poi.hssf.record.formula.eval.ValueEval;
import org.apache.poi.hssf.record.formula.functions.FreeRefFunction;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.usermodel.DateUtil;

final class YearFrac implements FreeRefFunction {
    public static final FreeRefFunction instance = new YearFrac();

    private YearFrac() {
    }

    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        int srcCellRow = ec.getRowIndex();
        int srcCellCol = ec.getColumnIndex();
        int basis = 0;
        try {
            int length = args.length;
            if (length != 2) {
                if (length != 3) {
                    return ErrorEval.VALUE_INVALID;
                }
                basis = evaluateIntArg(args[2], srcCellRow, srcCellCol);
            }
            return new NumberEval(YearFracCalculator.calculate(evaluateDateArg(args[0], srcCellRow, srcCellCol), evaluateDateArg(args[1], srcCellRow, srcCellCol), basis));
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    private static double evaluateDateArg(ValueEval arg, int srcCellRow, int srcCellCol) throws EvaluationException {
        ValueEval ve = OperandResolver.getSingleValue(arg, srcCellRow, (short) srcCellCol);
        if (!(ve instanceof StringEval)) {
            return OperandResolver.coerceValueToDouble(ve);
        }
        String strVal = ((StringEval) ve).getStringValue();
        Double dVal = OperandResolver.parseDouble(strVal);
        if (dVal != null) {
            return dVal.doubleValue();
        }
        return DateUtil.getExcelDate(parseDate(strVal), false);
    }

    private static Calendar parseDate(String strVal) throws EvaluationException {
        String[] parts = Pattern.compile("/").split(strVal);
        if (parts.length == 3) {
            String part2 = parts[2];
            int spacePos = part2.indexOf(32);
            if (spacePos > 0) {
                part2 = part2.substring(0, spacePos);
            }
            try {
                int f0 = Integer.parseInt(parts[0]);
                try {
                    int f1 = Integer.parseInt(parts[1]);
                    try {
                        int f2 = Integer.parseInt(part2);
                        if (f0 < 0 || f1 < 0 || f2 < 0 || (f0 > 12 && f1 > 12 && f2 > 12)) {
                            throw new EvaluationException(ErrorEval.VALUE_INVALID);
                        } else if (f0 >= 1900 && f0 < 9999) {
                            return makeDate(f0, f1, f2);
                        } else {
                            throw new RuntimeException("Unable to determine date format for text '" + strVal + "'");
                        }
                    } catch (NumberFormatException e) {
                        throw new EvaluationException(ErrorEval.VALUE_INVALID);
                    }
                } catch (NumberFormatException e2) {
                    throw new EvaluationException(ErrorEval.VALUE_INVALID);
                }
            } catch (NumberFormatException e3) {
                throw new EvaluationException(ErrorEval.VALUE_INVALID);
            }
        } else {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
    }

    private static Calendar makeDate(int year, int month, int day) throws EvaluationException {
        if (month < 1 || month > 12) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        GregorianCalendar gregorianCalendar = new GregorianCalendar(year, month - 1, 1, 0, 0, 0);
        gregorianCalendar.set(14, 0);
        if (day < 1 || day > gregorianCalendar.getActualMaximum(5)) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        gregorianCalendar.set(5, day);
        return gregorianCalendar;
    }

    private static int evaluateIntArg(ValueEval arg, int srcCellRow, int srcCellCol) throws EvaluationException {
        return OperandResolver.coerceValueToInt(OperandResolver.getSingleValue(arg, srcCellRow, (short) srcCellCol));
    }
}
