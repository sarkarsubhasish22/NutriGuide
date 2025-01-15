package org.apache.poi.hssf.record.formula.eval;

import org.apache.poi.hssf.record.formula.functions.Fixed2ArgFunction;
import org.apache.poi.hssf.record.formula.functions.Function;

public abstract class TwoOperandNumericOperation extends Fixed2ArgFunction {
    public static final Function AddEval = new TwoOperandNumericOperation() {
        /* access modifiers changed from: protected */
        public double evaluate(double d0, double d1) {
            return d0 + d1;
        }
    };
    public static final Function DivideEval = new TwoOperandNumericOperation() {
        /* access modifiers changed from: protected */
        public double evaluate(double d0, double d1) throws EvaluationException {
            if (d1 != 0.0d) {
                return d0 / d1;
            }
            throw new EvaluationException(ErrorEval.DIV_ZERO);
        }
    };
    public static final Function MultiplyEval = new TwoOperandNumericOperation() {
        /* access modifiers changed from: protected */
        public double evaluate(double d0, double d1) {
            return d0 * d1;
        }
    };
    public static final Function PowerEval = new TwoOperandNumericOperation() {
        /* access modifiers changed from: protected */
        public double evaluate(double d0, double d1) {
            return Math.pow(d0, d1);
        }
    };
    public static final Function SubtractEval = new SubtractEvalClass();

    /* access modifiers changed from: protected */
    public abstract double evaluate(double d, double d2) throws EvaluationException;

    /* access modifiers changed from: protected */
    public final double singleOperandEvaluate(ValueEval arg, int srcCellRow, int srcCellCol) throws EvaluationException {
        return OperandResolver.coerceValueToDouble(OperandResolver.getSingleValue(arg, srcCellRow, srcCellCol));
    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
        EvaluationException e;
        try {
            double result = evaluate(singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex), singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex));
            if (result == 0.0d) {
                try {
                    if (!(this instanceof SubtractEvalClass)) {
                        return NumberEval.ZERO;
                    }
                } catch (EvaluationException e2) {
                    e = e2;
                    return e.getErrorEval();
                }
            }
            return (Double.isNaN(result) || Double.isInfinite(result)) ? ErrorEval.NUM_ERROR : new NumberEval(result);
        } catch (EvaluationException e3) {
            e = e3;
            return e.getErrorEval();
        }
    }

    private static final class SubtractEvalClass extends TwoOperandNumericOperation {
        /* access modifiers changed from: protected */
        public double evaluate(double d0, double d1) {
            return d0 - d1;
        }
    }
}
