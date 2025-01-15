package org.apache.poi.hssf.record.formula.functions;

import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.formula.eval.EvaluationException;
import org.apache.poi.hssf.record.formula.eval.NumberEval;
import org.apache.poi.hssf.record.formula.eval.ValueEval;

public final class Npv implements Function2Arg, Function3Arg, Function4Arg {
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
        try {
            double result = evaluate(NumericFunction.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex), NumericFunction.singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex));
            try {
                NumericFunction.checkValue(result);
                return new NumberEval(result);
            } catch (EvaluationException e) {
                e = e;
                return e.getErrorEval();
            }
        } catch (EvaluationException e2) {
            e = e2;
            return e.getErrorEval();
        }
    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, ValueEval arg2) {
        try {
            double result = evaluate(NumericFunction.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex), NumericFunction.singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex), NumericFunction.singleOperandEvaluate(arg2, srcRowIndex, srcColumnIndex));
            try {
                NumericFunction.checkValue(result);
                return new NumberEval(result);
            } catch (EvaluationException e) {
                e = e;
                return e.getErrorEval();
            }
        } catch (EvaluationException e2) {
            e = e2;
            return e.getErrorEval();
        }
    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, ValueEval arg2, ValueEval arg3) {
        double d1;
        double d2;
        double result;
        int i = srcRowIndex;
        int i2 = srcColumnIndex;
        try {
            double rate = NumericFunction.singleOperandEvaluate(arg0, i, i2);
            try {
                d1 = NumericFunction.singleOperandEvaluate(arg1, i, i2);
            } catch (EvaluationException e) {
                e = e;
                ValueEval valueEval = arg2;
                ValueEval valueEval2 = arg3;
                return e.getErrorEval();
            }
            try {
                d2 = NumericFunction.singleOperandEvaluate(arg2, i, i2);
            } catch (EvaluationException e2) {
                e = e2;
                ValueEval valueEval22 = arg3;
                return e.getErrorEval();
            }
            try {
                result = evaluate(rate, d1, d2, NumericFunction.singleOperandEvaluate(arg3, i, i2));
            } catch (EvaluationException e3) {
                e = e3;
                return e.getErrorEval();
            }
            try {
                NumericFunction.checkValue(result);
                return new NumberEval(result);
            } catch (EvaluationException e4) {
                e = e4;
                return e.getErrorEval();
            }
        } catch (EvaluationException e5) {
            e = e5;
            ValueEval valueEval3 = arg1;
            ValueEval valueEval4 = arg2;
            ValueEval valueEval222 = arg3;
            return e.getErrorEval();
        }
    }

    public ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        int nArgs = args.length;
        if (nArgs < 2) {
            return ErrorEval.VALUE_INVALID;
        }
        double[] ds = new double[(nArgs - 1)];
        try {
            double rate = NumericFunction.singleOperandEvaluate(args[0], srcRowIndex, srcColumnIndex);
            for (int i = 0; i < ds.length; i++) {
                ds[i] = NumericFunction.singleOperandEvaluate(args[i + 1], srcRowIndex, srcColumnIndex);
            }
            double result = evaluate(rate, ds);
            try {
                NumericFunction.checkValue(result);
                return new NumberEval(result);
            } catch (EvaluationException e) {
                e = e;
                return e.getErrorEval();
            }
        } catch (EvaluationException e2) {
            e = e2;
            return e.getErrorEval();
        }
    }

    private static double evaluate(double rate, double... ds) {
        double sum = 0.0d;
        for (int i = 0; i < ds.length; i++) {
            sum += ds[i] / Math.pow(1.0d + rate, (double) i);
        }
        return sum;
    }
}
