package org.apache.poi.hssf.record.formula.functions;

import org.apache.poi.hssf.record.formula.functions.NumericFunction;

public final class Even extends NumericFunction.OneArg {
    private static final long PARITY_MASK = -2;

    /* access modifiers changed from: protected */
    public double evaluate(double d) {
        long result;
        if (d == 0.0d) {
            return 0.0d;
        }
        if (d > 0.0d) {
            result = calcEven(d);
        } else {
            result = -calcEven(-d);
        }
        return (double) result;
    }

    private static long calcEven(double d) {
        long x = ((long) d) & PARITY_MASK;
        if (((double) x) == d) {
            return x;
        }
        return 2 + x;
    }
}
