package org.apache.poi.hssf.record.formula.functions;

import org.apache.poi.hssf.record.formula.functions.XYNumericFunction;

public final class Sumx2py2 extends XYNumericFunction {
    private static final XYNumericFunction.Accumulator XSquaredPlusYSquaredAccumulator = new XYNumericFunction.Accumulator() {
        public double accumulate(double x, double y) {
            return (x * x) + (y * y);
        }
    };

    /* access modifiers changed from: protected */
    public XYNumericFunction.Accumulator createAccumulator() {
        return XSquaredPlusYSquaredAccumulator;
    }
}
