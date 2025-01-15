package org.apache.poi.hssf.record.formula.functions;

import org.apache.poi.hssf.record.formula.functions.NumericFunction;

public final class Odd extends NumericFunction.OneArg {
    private static final long PARITY_MASK = -2;

    /* access modifiers changed from: protected */
    public double evaluate(double d) {
        if (d == 0.0d) {
            return 1.0d;
        }
        if (d > 0.0d) {
            return (double) calcOdd(d);
        }
        return (double) (-calcOdd(-d));
    }

    private static long calcOdd(double d) {
        double dpm1 = 1.0d + d;
        long x = ((long) dpm1) & PARITY_MASK;
        if (((double) x) == dpm1) {
            return x - 1;
        }
        return 1 + x;
    }
}
