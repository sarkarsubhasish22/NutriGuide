package org.apache.poi.hssf.record.formula.functions;

final class FinanceLib {
    private FinanceLib() {
    }

    public static double fv(double r, double n, double y, double p, boolean t) {
        double d = n;
        if (r == 0.0d) {
            return (p + (d * y)) * -1.0d;
        }
        double d2 = 1.0d;
        double r1 = r + 1.0d;
        double pow = 1.0d - Math.pow(r1, n);
        if (t) {
            d2 = r1;
        }
        return (((pow * d2) * y) / r) - (Math.pow(r1, n) * p);
    }

    public static double pv(double r, double n, double y, double f, boolean t) {
        double d = n;
        if (r == 0.0d) {
            return ((d * y) + f) * -1.0d;
        }
        double d2 = 1.0d;
        double r1 = r + 1.0d;
        double pow = (1.0d - Math.pow(r1, n)) / r;
        if (t) {
            d2 = r1;
        }
        return (((pow * d2) * y) - f) / Math.pow(r1, n);
    }

    public static double npv(double r, double[] cfs) {
        double npv = 0.0d;
        double r1 = 1.0d + r;
        double trate = r1;
        for (double d : cfs) {
            npv += d / trate;
            trate *= r1;
        }
        return npv;
    }

    public static double pmt(double r, double n, double p, double f, boolean t) {
        double d = n;
        if (r == 0.0d) {
            return ((f + p) * -1.0d) / d;
        }
        double r1 = r + 1.0d;
        return ((f + (Math.pow(r1, d) * p)) * r) / ((t ? r1 : 1.0d) * (1.0d - Math.pow(r1, d)));
    }

    public static double nper(double r, double y, double p, double f, boolean t) {
        double d = p;
        if (r == 0.0d) {
            return ((f + d) * -1.0d) / y;
        }
        double retval = 1.0d;
        double r1 = r + 1.0d;
        if (t) {
            retval = r1;
        }
        double ryr = (retval * y) / r;
        return (Math.log(ryr - f < 0.0d ? f - ryr : ryr - f) - Math.log(ryr - f < 0.0d ? (-d) - ryr : d + ryr)) / Math.log(r1);
    }
}
