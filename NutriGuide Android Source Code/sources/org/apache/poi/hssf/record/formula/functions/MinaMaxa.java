package org.apache.poi.hssf.record.formula.functions;

public abstract class MinaMaxa extends MultiOperandNumericFunction {
    public static final Function MAXA = new MinaMaxa() {
        /* access modifiers changed from: protected */
        public double evaluate(double[] values) {
            if (values.length > 0) {
                return MathX.max(values);
            }
            return 0.0d;
        }
    };
    public static final Function MINA = new MinaMaxa() {
        /* access modifiers changed from: protected */
        public double evaluate(double[] values) {
            if (values.length > 0) {
                return MathX.min(values);
            }
            return 0.0d;
        }
    };

    protected MinaMaxa() {
        super(true, true);
    }
}
