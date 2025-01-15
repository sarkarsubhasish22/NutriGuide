package org.apache.poi.hssf.record.formula.functions;

import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.formula.eval.EvaluationException;
import org.apache.poi.hssf.record.formula.eval.NumberEval;
import org.apache.poi.hssf.record.formula.eval.OperandResolver;
import org.apache.poi.hssf.record.formula.eval.ValueEval;

public final class Value extends Fixed1ArgFunction {
    private static final int MIN_DISTANCE_BETWEEN_THOUSANDS_SEPARATOR = 4;
    private static final Double ZERO = new Double(0.0d);

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
        try {
            Double result = convertTextToNumber(OperandResolver.coerceValueToString(OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex)));
            if (result == null) {
                return ErrorEval.VALUE_INVALID;
            }
            return new NumberEval(result.doubleValue());
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    private static Double convertTextToNumber(String str) {
        int length = str.length();
        boolean z = false;
        int i = 0;
        boolean z2 = false;
        boolean z3 = false;
        boolean z4 = false;
        while (i < length) {
            char charAt = str.charAt(i);
            if (Character.isDigit(charAt) || charAt == '.') {
                break;
            }
            if (charAt != ' ') {
                if (charAt != '$') {
                    if (charAt != '+') {
                        if (charAt != '-' || z3 || z4) {
                            return null;
                        }
                        z3 = true;
                    } else if (z3 || z4) {
                        return null;
                    } else {
                        z4 = true;
                    }
                } else if (z2) {
                    return null;
                } else {
                    z2 = true;
                }
            }
            i++;
        }
        if (i < length) {
            int i2 = -32768;
            StringBuffer stringBuffer = new StringBuffer(length);
            while (i < length) {
                char charAt2 = str.charAt(i);
                if (Character.isDigit(charAt2)) {
                    stringBuffer.append(charAt2);
                } else if (charAt2 != ' ') {
                    if (charAt2 != ',') {
                        if (charAt2 != '.') {
                            if ((charAt2 != 'E' && charAt2 != 'e') || i - i2 < 4) {
                                return null;
                            }
                            stringBuffer.append(str.substring(i));
                            i = length;
                        } else if (z || i - i2 < 4) {
                            return null;
                        } else {
                            stringBuffer.append('.');
                            z = true;
                        }
                    } else if (z || i - i2 < 4) {
                        return null;
                    } else {
                        i2 = i;
                    }
                } else if (str.substring(i).trim().length() > 0) {
                    return null;
                }
                i++;
            }
            if (!z && i - i2 < 4) {
                return null;
            }
            try {
                double parseDouble = Double.parseDouble(stringBuffer.toString());
                if (z3) {
                    parseDouble = -parseDouble;
                }
                return new Double(parseDouble);
            } catch (NumberFormatException e) {
                return null;
            }
        } else if (z2 || z3 || z4) {
            return null;
        } else {
            return ZERO;
        }
    }
}
