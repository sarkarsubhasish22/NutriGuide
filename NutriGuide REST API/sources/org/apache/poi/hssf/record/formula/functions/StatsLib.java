package org.apache.poi.hssf.record.formula.functions;

import java.util.Arrays;

final class StatsLib {
    private StatsLib() {
    }

    public static double avedev(double[] v) {
        double s = 0.0d;
        for (double d : v) {
            s += d;
        }
        double length = (double) v.length;
        Double.isNaN(length);
        double m = s / length;
        double s2 = 0.0d;
        for (double d2 : v) {
            s2 += Math.abs(d2 - m);
        }
        double length2 = (double) v.length;
        Double.isNaN(length2);
        return s2 / length2;
    }

    public static double stdev(double[] v) {
        if (v == null || v.length <= 1) {
            return Double.NaN;
        }
        double devsq = devsq(v);
        double length = (double) (v.length - 1);
        Double.isNaN(length);
        return Math.sqrt(devsq / length);
    }

    public static double median(double[] v) {
        if (v == null || v.length < 1) {
            return Double.NaN;
        }
        int n = v.length;
        Arrays.sort(v);
        return n % 2 == 0 ? (v[n / 2] + v[(n / 2) - 1]) / 2.0d : v[n / 2];
    }

    public static double devsq(double[] v) {
        if (v == null || v.length < 1) {
            return Double.NaN;
        }
        double s = 0.0d;
        for (double d : v) {
            s += d;
        }
        double d2 = (double) n;
        Double.isNaN(d2);
        double m = s / d2;
        double s2 = 0.0d;
        for (int i = 0; i < n; i++) {
            s2 += (v[i] - m) * (v[i] - m);
        }
        return n == 1 ? 0.0d : s2;
    }

    public static double kthLargest(double[] v, int k) {
        int index = k - 1;
        if (v == null || v.length <= index || index < 0) {
            return Double.NaN;
        }
        Arrays.sort(v);
        return v[(v.length - index) - 1];
    }

    public static double kthSmallest(double[] v, int k) {
        int index = k - 1;
        if (v == null || v.length <= index || index < 0) {
            return Double.NaN;
        }
        Arrays.sort(v);
        return v[index];
    }
}
