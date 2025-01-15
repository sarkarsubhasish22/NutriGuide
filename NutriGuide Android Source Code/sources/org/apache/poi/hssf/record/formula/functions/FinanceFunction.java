package org.apache.poi.hssf.record.formula.functions;

import org.apache.poi.hssf.record.formula.eval.BoolEval;
import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.formula.eval.EvaluationException;
import org.apache.poi.hssf.record.formula.eval.NumberEval;
import org.apache.poi.hssf.record.formula.eval.ValueEval;

public abstract class FinanceFunction implements Function3Arg, Function4Arg {
    private static final ValueEval DEFAULT_ARG3 = NumberEval.ZERO;
    private static final ValueEval DEFAULT_ARG4 = BoolEval.FALSE;
    public static final Function FV = new FinanceFunction() {
        /* access modifiers changed from: protected */
        public double evaluate(double rate, double arg1, double arg2, double arg3, boolean type) {
            return FinanceLib.fv(rate, arg1, arg2, arg3, type);
        }
    };
    public static final Function NPER = new FinanceFunction() {
        /* access modifiers changed from: protected */
        public double evaluate(double rate, double arg1, double arg2, double arg3, boolean type) {
            return FinanceLib.nper(rate, arg1, arg2, arg3, type);
        }
    };
    public static final Function PMT = new FinanceFunction() {
        /* access modifiers changed from: protected */
        public double evaluate(double rate, double arg1, double arg2, double arg3, boolean type) {
            return FinanceLib.pmt(rate, arg1, arg2, arg3, type);
        }
    };
    public static final Function PV = new FinanceFunction() {
        /* access modifiers changed from: protected */
        public double evaluate(double rate, double arg1, double arg2, double arg3, boolean type) {
            return FinanceLib.pv(rate, arg1, arg2, arg3, type);
        }
    };

    /* access modifiers changed from: protected */
    public abstract double evaluate(double d, double d2, double d3, double d4, boolean z) throws EvaluationException;

    protected FinanceFunction() {
    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, ValueEval arg2) {
        return evaluate(srcRowIndex, srcColumnIndex, arg0, arg1, arg2, DEFAULT_ARG3);
    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, ValueEval arg2, ValueEval arg3) {
        return evaluate(srcRowIndex, srcColumnIndex, arg0, arg1, arg2, arg3, DEFAULT_ARG4);
    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, ValueEval arg2, ValueEval arg3, ValueEval arg4) {
        int i = srcRowIndex;
        int i2 = srcColumnIndex;
        try {
            try {
                double result = evaluate(NumericFunction.singleOperandEvaluate(arg0, i, i2), NumericFunction.singleOperandEvaluate(arg1, i, i2), NumericFunction.singleOperandEvaluate(arg2, i, i2), NumericFunction.singleOperandEvaluate(arg3, i, i2), NumericFunction.singleOperandEvaluate(arg4, i, i2) != 0.0d);
                try {
                    NumericFunction.checkValue(result);
                    return new NumberEval(result);
                } catch (EvaluationException e) {
                    e = e;
                    double d = result;
                    return e.getErrorEval();
                }
            } catch (EvaluationException e2) {
                e = e2;
                return e.getErrorEval();
            }
        } catch (EvaluationException e3) {
            e = e3;
            ValueEval valueEval = arg4;
            return e.getErrorEval();
        }
    }

    public ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        int length = args.length;
        if (length == 3) {
            return evaluate(srcRowIndex, srcColumnIndex, args[0], args[1], args[2], DEFAULT_ARG3, DEFAULT_ARG4);
        } else if (length == 4) {
            return evaluate(srcRowIndex, srcColumnIndex, args[0], args[1], args[2], args[3], DEFAULT_ARG4);
        } else if (length != 5) {
            return ErrorEval.VALUE_INVALID;
        } else {
            return evaluate(srcRowIndex, srcColumnIndex, args[0], args[1], args[2], args[3], args[4]);
        }
    }

    /* access modifiers changed from: protected */
    public double evaluate(double[] ds) throws EvaluationException {
        double[] dArr = ds;
        double arg3 = 0.0d;
        double arg4 = 0.0d;
        int length = dArr.length;
        if (length != 3) {
            if (length != 4) {
                if (length == 5) {
                    arg4 = dArr[4];
                } else {
                    throw new IllegalStateException("Wrong number of arguments");
                }
            }
            arg3 = dArr[3];
        }
        return evaluate(dArr[0], dArr[1], dArr[2], arg3, arg4 != 0.0d);
    }
}
