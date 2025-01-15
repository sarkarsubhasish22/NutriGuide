package org.apache.poi.hssf.record.formula.functions;

import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.formula.eval.EvaluationException;
import org.apache.poi.hssf.record.formula.eval.NumberEval;
import org.apache.poi.hssf.record.formula.eval.OperandResolver;
import org.apache.poi.hssf.record.formula.eval.ValueEval;

public abstract class AggregateFunction extends MultiOperandNumericFunction {
    public static final Function AVEDEV = new AggregateFunction() {
        /* access modifiers changed from: protected */
        public double evaluate(double[] values) {
            return StatsLib.avedev(values);
        }
    };
    public static final Function AVERAGE = new AggregateFunction() {
        /* access modifiers changed from: protected */
        public double evaluate(double[] values) throws EvaluationException {
            if (values.length >= 1) {
                return MathX.average(values);
            }
            throw new EvaluationException(ErrorEval.DIV_ZERO);
        }
    };
    public static final Function DEVSQ = new AggregateFunction() {
        /* access modifiers changed from: protected */
        public double evaluate(double[] values) {
            return StatsLib.devsq(values);
        }
    };
    public static final Function LARGE = new LargeSmall(true);
    public static final Function MAX = new AggregateFunction() {
        /* access modifiers changed from: protected */
        public double evaluate(double[] values) {
            if (values.length > 0) {
                return MathX.max(values);
            }
            return 0.0d;
        }
    };
    public static final Function MEDIAN = new AggregateFunction() {
        /* access modifiers changed from: protected */
        public double evaluate(double[] values) {
            return StatsLib.median(values);
        }
    };
    public static final Function MIN = new AggregateFunction() {
        /* access modifiers changed from: protected */
        public double evaluate(double[] values) {
            if (values.length > 0) {
                return MathX.min(values);
            }
            return 0.0d;
        }
    };
    public static final Function PRODUCT = new AggregateFunction() {
        /* access modifiers changed from: protected */
        public double evaluate(double[] values) {
            return MathX.product(values);
        }
    };
    public static final Function SMALL = new LargeSmall(false);
    public static final Function STDEV = new AggregateFunction() {
        /* access modifiers changed from: protected */
        public double evaluate(double[] values) throws EvaluationException {
            if (values.length >= 1) {
                return StatsLib.stdev(values);
            }
            throw new EvaluationException(ErrorEval.DIV_ZERO);
        }
    };
    public static final Function SUM = new AggregateFunction() {
        /* access modifiers changed from: protected */
        public double evaluate(double[] values) {
            return MathX.sum(values);
        }
    };
    public static final Function SUMSQ = new AggregateFunction() {
        /* access modifiers changed from: protected */
        public double evaluate(double[] values) {
            return MathX.sumsq(values);
        }
    };

    private static final class LargeSmall extends Fixed2ArgFunction {
        private final boolean _isLarge;

        protected LargeSmall(boolean isLarge) {
            this._isLarge = isLarge;
        }

        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
            try {
                double dn = OperandResolver.coerceValueToDouble(OperandResolver.getSingleValue(arg1, srcRowIndex, srcColumnIndex));
                if (dn < 1.0d) {
                    return ErrorEval.NUM_ERROR;
                }
                int k = (int) Math.ceil(dn);
                try {
                    double[] ds = ValueCollector.collectValues(arg0);
                    if (k > ds.length) {
                        return ErrorEval.NUM_ERROR;
                    }
                    double result = this._isLarge ? StatsLib.kthLargest(ds, k) : StatsLib.kthSmallest(ds, k);
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
            } catch (EvaluationException e3) {
                return ErrorEval.VALUE_INVALID;
            }
        }
    }

    private static final class ValueCollector extends MultiOperandNumericFunction {
        private static final ValueCollector instance = new ValueCollector();

        public ValueCollector() {
            super(false, false);
        }

        public static double[] collectValues(ValueEval... operands) throws EvaluationException {
            return instance.getNumberArray(operands);
        }

        /* access modifiers changed from: protected */
        public double evaluate(double[] values) {
            throw new IllegalStateException("should not be called");
        }
    }

    protected AggregateFunction() {
        super(false, false);
    }
}
