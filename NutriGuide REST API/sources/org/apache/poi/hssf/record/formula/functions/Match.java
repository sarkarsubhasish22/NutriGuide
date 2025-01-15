package org.apache.poi.hssf.record.formula.functions;

import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.formula.eval.EvaluationException;
import org.apache.poi.hssf.record.formula.eval.NumberEval;
import org.apache.poi.hssf.record.formula.eval.NumericValueEval;
import org.apache.poi.hssf.record.formula.eval.OperandResolver;
import org.apache.poi.hssf.record.formula.eval.RefEval;
import org.apache.poi.hssf.record.formula.eval.StringEval;
import org.apache.poi.hssf.record.formula.eval.ValueEval;
import org.apache.poi.hssf.record.formula.functions.LookupUtils;
import org.apache.poi.ss.formula.TwoDEval;

public final class Match extends Var2or3ArgFunction {
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
        return eval(srcRowIndex, srcColumnIndex, arg0, arg1, 1.0d);
    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, ValueEval arg2) {
        try {
            return eval(srcRowIndex, srcColumnIndex, arg0, arg1, evaluateMatchTypeArg(arg2, srcRowIndex, srcColumnIndex));
        } catch (EvaluationException e) {
            return ErrorEval.REF_INVALID;
        }
    }

    private static ValueEval eval(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, double match_type) {
        boolean findLargestLessThanOrEqual = false;
        boolean matchExact = match_type == 0.0d;
        if (match_type > 0.0d) {
            findLargestLessThanOrEqual = true;
        }
        try {
            return new NumberEval((double) (findIndexOfValue(OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex), evaluateLookupRange(arg1), matchExact, findLargestLessThanOrEqual) + 1));
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    private static final class SingleValueVector implements LookupUtils.ValueVector {
        private final ValueEval _value;

        public SingleValueVector(ValueEval value) {
            this._value = value;
        }

        public ValueEval getItem(int index) {
            if (index == 0) {
                return this._value;
            }
            throw new RuntimeException("Invalid index (" + index + ") only zero is allowed");
        }

        public int getSize() {
            return 1;
        }
    }

    private static LookupUtils.ValueVector evaluateLookupRange(ValueEval eval) throws EvaluationException {
        if (eval instanceof RefEval) {
            return new SingleValueVector(((RefEval) eval).getInnerValueEval());
        }
        if (eval instanceof TwoDEval) {
            LookupUtils.ValueVector result = LookupUtils.createVector((TwoDEval) eval);
            if (result != null) {
                return result;
            }
            throw new EvaluationException(ErrorEval.NA);
        } else if (eval instanceof NumericValueEval) {
            throw new EvaluationException(ErrorEval.NA);
        } else if (!(eval instanceof StringEval)) {
            throw new RuntimeException("Unexpected eval type (" + eval.getClass().getName() + ")");
        } else if (OperandResolver.parseDouble(((StringEval) eval).getStringValue()) == null) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        } else {
            throw new EvaluationException(ErrorEval.NA);
        }
    }

    private static double evaluateMatchTypeArg(ValueEval arg, int srcCellRow, int srcCellCol) throws EvaluationException {
        ValueEval match_type = OperandResolver.getSingleValue(arg, srcCellRow, srcCellCol);
        if (match_type instanceof ErrorEval) {
            throw new EvaluationException((ErrorEval) match_type);
        } else if (match_type instanceof NumericValueEval) {
            return ((NumericValueEval) match_type).getNumberValue();
        } else {
            if (match_type instanceof StringEval) {
                Double d = OperandResolver.parseDouble(((StringEval) match_type).getStringValue());
                if (d != null) {
                    return d.doubleValue();
                }
                throw new EvaluationException(ErrorEval.VALUE_INVALID);
            }
            throw new RuntimeException("Unexpected match_type type (" + match_type.getClass().getName() + ")");
        }
    }

    private static int findIndexOfValue(ValueEval lookupValue, LookupUtils.ValueVector lookupRange, boolean matchExact, boolean findLargestLessThanOrEqual) throws EvaluationException {
        LookupUtils.LookupValueComparer lookupComparer = createLookupComparer(lookupValue, matchExact);
        int size = lookupRange.getSize();
        if (matchExact) {
            for (int i = 0; i < size; i++) {
                if (lookupComparer.compareTo(lookupRange.getItem(i)).isEqual()) {
                    return i;
                }
            }
            throw new EvaluationException(ErrorEval.NA);
        } else if (findLargestLessThanOrEqual) {
            for (int i2 = size - 1; i2 >= 0; i2--) {
                LookupUtils.CompareResult cmp = lookupComparer.compareTo(lookupRange.getItem(i2));
                if (!cmp.isTypeMismatch() && !cmp.isLessThan()) {
                    return i2;
                }
            }
            throw new EvaluationException(ErrorEval.NA);
        } else {
            int i3 = 0;
            while (i3 < size) {
                LookupUtils.CompareResult cmp2 = lookupComparer.compareTo(lookupRange.getItem(i3));
                if (cmp2.isEqual()) {
                    return i3;
                }
                if (!cmp2.isGreaterThan()) {
                    i3++;
                } else if (i3 >= 1) {
                    return i3 - 1;
                } else {
                    throw new EvaluationException(ErrorEval.NA);
                }
            }
            throw new EvaluationException(ErrorEval.NA);
        }
    }

    private static LookupUtils.LookupValueComparer createLookupComparer(ValueEval lookupValue, boolean matchExact) {
        if (matchExact && (lookupValue instanceof StringEval)) {
            String stringValue = ((StringEval) lookupValue).getStringValue();
            if (isLookupValueWild(stringValue)) {
                throw new RuntimeException("Wildcard lookup values '" + stringValue + "' not supported yet");
            }
        }
        return LookupUtils.createLookupComparer(lookupValue);
    }

    private static boolean isLookupValueWild(String stringValue) {
        if (stringValue.indexOf(63) >= 0 || stringValue.indexOf(42) >= 0) {
            return true;
        }
        return false;
    }
}
