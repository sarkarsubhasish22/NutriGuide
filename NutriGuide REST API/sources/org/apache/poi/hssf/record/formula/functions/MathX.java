package org.apache.poi.hssf.record.formula.functions;

final class MathX {
    private MathX() {
    }

    public static double round(double n, int p) {
        if (Double.isNaN(n) || Double.isInfinite(n)) {
            return Double.NaN;
        }
        if (p == 0) {
            return (double) Math.round(n);
        }
        double temp = Math.pow(10.0d, (double) p);
        double round = (double) Math.round(n * temp);
        Double.isNaN(round);
        return round / temp;
    }

    public static double roundUp(double n, int p) {
        double d;
        if (Double.isNaN(n) || Double.isInfinite(n)) {
            return Double.NaN;
        }
        if (p != 0) {
            double temp = Math.pow(10.0d, (double) p);
            double nat = Math.abs(n * temp);
            double sign = (double) sign(n);
            if (nat == ((double) ((long) nat))) {
                d = nat / temp;
            } else {
                double round = (double) Math.round(0.5d + nat);
                Double.isNaN(round);
                d = round / temp;
            }
            Double.isNaN(sign);
            return sign * d;
        }
        double na = Math.abs(n);
        double sign2 = (double) sign(n);
        double d2 = na == ((double) ((long) na)) ? na : (double) (((long) na) + 1);
        Double.isNaN(sign2);
        return d2 * sign2;
    }

    public static double roundDown(double n, int p) {
        if (Double.isNaN(n) || Double.isInfinite(n)) {
            return Double.NaN;
        }
        if (p == 0) {
            return (double) ((long) n);
        }
        double temp = Math.pow(10.0d, (double) p);
        double sign = (double) (((long) sign(n)) * Math.round((Math.abs(n) * temp) - 0.5d));
        Double.isNaN(sign);
        return sign / temp;
    }

    public static short sign(double d) {
        return (short) (d == 0.0d ? 0 : d < 0.0d ? -1 : 1);
    }

    public static double average(double[] values) {
        double sum = 0.0d;
        for (double d : values) {
            sum += d;
        }
        double length = (double) values.length;
        Double.isNaN(length);
        return sum / length;
    }

    public static double sum(double[] values) {
        double sum = 0.0d;
        for (double d : values) {
            sum += d;
        }
        return sum;
    }

    public static double sumsq(double[] values) {
        double sumsq = 0.0d;
        int iSize = values.length;
        for (int i = 0; i < iSize; i++) {
            sumsq += values[i] * values[i];
        }
        return sumsq;
    }

    public static double product(double[] values) {
        double product = 0.0d;
        if (values != null && values.length > 0) {
            product = 1.0d;
            for (double d : values) {
                product *= d;
            }
        }
        return product;
    }

    public static double min(double[] values) {
        double min = Double.POSITIVE_INFINITY;
        for (double min2 : values) {
            min = Math.min(min, min2);
        }
        return min;
    }

    public static double max(double[] values) {
        double max = Double.NEGATIVE_INFINITY;
        for (double max2 : values) {
            max = Math.max(max, max2);
        }
        return max;
    }

    public static double floor(double n, double s) {
        if ((n < 0.0d && s > 0.0d) || ((n > 0.0d && s < 0.0d) || (s == 0.0d && n != 0.0d))) {
            return Double.NaN;
        }
        if (n == 0.0d || s == 0.0d) {
            return 0.0d;
        }
        return Math.floor(n / s) * s;
    }

    public static double ceiling(double n, double s) {
        if ((n < 0.0d && s > 0.0d) || (n > 0.0d && s < 0.0d)) {
            return Double.NaN;
        }
        if (n == 0.0d || s == 0.0d) {
            return 0.0d;
        }
        return Math.ceil(n / s) * s;
    }

    public static double factorial(int n) {
        double d = 1.0d;
        if (n < 0) {
            return Double.NaN;
        }
        if (n > 170) {
            return Double.POSITIVE_INFINITY;
        }
        for (int i = 1; i <= n; i++) {
            double d2 = (double) i;
            Double.isNaN(d2);
            d *= d2;
        }
        return d;
    }

    public static double mod(double n, double d) {
        if (d == 0.0d) {
            return Double.NaN;
        }
        if (sign(n) == sign(d)) {
            return n % d;
        }
        return ((n % d) + d) % d;
    }

    public static double acosh(double d) {
        return Math.log(Math.sqrt(Math.pow(d, 2.0d) - 1.0d) + d);
    }

    public static double asinh(double d) {
        return Math.log(Math.sqrt((d * d) + 1.0d) + d);
    }

    public static double atanh(double d) {
        return Math.log((d + 1.0d) / (1.0d - d)) / 2.0d;
    }

    public static double cosh(double d) {
        return (Math.pow(2.718281828459045d, d) + Math.pow(2.718281828459045d, -d)) / 2.0d;
    }

    public static double sinh(double d) {
        return (Math.pow(2.718281828459045d, d) - Math.pow(2.718281828459045d, -d)) / 2.0d;
    }

    public static double tanh(double d) {
        double ePowX = Math.pow(2.718281828459045d, d);
        double ePowNegX = Math.pow(2.718281828459045d, -d);
        return (ePowX - ePowNegX) / (ePowX + ePowNegX);
    }

    public static double nChooseK(int n, int k) {
        double d = 1.0d;
        if (n < 0 || k < 0 || n < k) {
            return Double.NaN;
        }
        int minnk = Math.min(n - k, k);
        for (int i = Math.max(n - k, k); i < n; i++) {
            double d2 = (double) (i + 1);
            Double.isNaN(d2);
            d *= d2;
        }
        return d / factorial(minnk);
    }
}
