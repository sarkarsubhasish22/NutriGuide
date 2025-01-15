package org.apache.poi.hssf.record.formula.functions;

import org.apache.poi.hssf.record.formula.eval.BoolEval;
import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.formula.eval.EvaluationException;
import org.apache.poi.hssf.record.formula.eval.OperandResolver;
import org.apache.poi.hssf.record.formula.eval.RefEval;
import org.apache.poi.hssf.record.formula.eval.ValueEval;
import org.apache.poi.ss.formula.TwoDEval;

public abstract class BooleanFunction implements Function {
    public static final Function AND = new BooleanFunction() {
        /* access modifiers changed from: protected */
        public boolean getInitialResultValue() {
            return true;
        }

        /* access modifiers changed from: protected */
        public boolean partialEvaluate(boolean cumulativeResult, boolean currentValue) {
            return cumulativeResult && currentValue;
        }
    };
    public static final Function FALSE = new Fixed0ArgFunction() {
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex) {
            return BoolEval.FALSE;
        }
    };
    public static final Function NOT = new Fixed1ArgFunction() {
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
            boolean z = false;
            try {
                Boolean b = OperandResolver.coerceValueToBoolean(OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex), false);
                if (!(b == null ? false : b.booleanValue())) {
                    z = true;
                }
                return BoolEval.valueOf(z);
            } catch (EvaluationException e) {
                return e.getErrorEval();
            }
        }
    };
    public static final Function OR = new BooleanFunction() {
        /* access modifiers changed from: protected */
        public boolean getInitialResultValue() {
            return false;
        }

        /* access modifiers changed from: protected */
        public boolean partialEvaluate(boolean cumulativeResult, boolean currentValue) {
            return cumulativeResult || currentValue;
        }
    };
    public static final Function TRUE = new Fixed0ArgFunction() {
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex) {
            return BoolEval.TRUE;
        }
    };

    /* access modifiers changed from: protected */
    public abstract boolean getInitialResultValue();

    /* access modifiers changed from: protected */
    public abstract boolean partialEvaluate(boolean z, boolean z2);

    public final ValueEval evaluate(ValueEval[] args, int srcRow, int srcCol) {
        if (args.length < 1) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            return BoolEval.valueOf(calculate(args));
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    private boolean calculate(ValueEval[] args) throws EvaluationException {
        Boolean tempVe;
        boolean result = getInitialResultValue();
        boolean atleastOneNonBlank = false;
        for (TwoDEval twoDEval : args) {
            if (twoDEval instanceof TwoDEval) {
                TwoDEval ae = twoDEval;
                int height = ae.getHeight();
                int width = ae.getWidth();
                for (int rrIx = 0; rrIx < height; rrIx++) {
                    for (int rcIx = 0; rcIx < width; rcIx++) {
                        Boolean tempVe2 = OperandResolver.coerceValueToBoolean(ae.getValue(rrIx, rcIx), true);
                        if (tempVe2 != null) {
                            result = partialEvaluate(result, tempVe2.booleanValue());
                            atleastOneNonBlank = true;
                        }
                    }
                }
            } else {
                if (twoDEval instanceof RefEval) {
                    tempVe = OperandResolver.coerceValueToBoolean(twoDEval.getInnerValueEval(), true);
                } else {
                    tempVe = OperandResolver.coerceValueToBoolean(twoDEval, false);
                }
                if (tempVe != null) {
                    result = partialEvaluate(result, tempVe.booleanValue());
                    atleastOneNonBlank = true;
                }
            }
        }
        if (atleastOneNonBlank) {
            return result;
        }
        throw new EvaluationException(ErrorEval.VALUE_INVALID);
    }
}
