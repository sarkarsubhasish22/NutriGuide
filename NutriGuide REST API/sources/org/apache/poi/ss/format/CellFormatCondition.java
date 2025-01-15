package org.apache.poi.ss.format;

import java.util.HashMap;
import java.util.Map;

public abstract class CellFormatCondition {
    private static final int EQ = 4;
    private static final int GE = 3;
    private static final int GT = 2;
    private static final int LE = 1;
    private static final int LT = 0;
    private static final int NE = 5;
    private static final Map<String, Integer> TESTS;

    public abstract boolean pass(double d);

    static {
        HashMap hashMap = new HashMap();
        TESTS = hashMap;
        hashMap.put("<", 0);
        hashMap.put("<=", 1);
        hashMap.put(">", 2);
        hashMap.put(">=", 3);
        hashMap.put("=", 4);
        hashMap.put("==", 4);
        hashMap.put("!=", 5);
        hashMap.put("<>", 5);
    }

    public static CellFormatCondition getInstance(String opString, String constStr) {
        Map<String, Integer> map = TESTS;
        if (map.containsKey(opString)) {
            int test = map.get(opString).intValue();
            final double c = Double.parseDouble(constStr);
            if (test == 0) {
                return new CellFormatCondition() {
                    public boolean pass(double value) {
                        return value < c;
                    }
                };
            }
            if (test == 1) {
                return new CellFormatCondition() {
                    public boolean pass(double value) {
                        return value <= c;
                    }
                };
            }
            if (test == 2) {
                return new CellFormatCondition() {
                    public boolean pass(double value) {
                        return value > c;
                    }
                };
            }
            if (test == 3) {
                return new CellFormatCondition() {
                    public boolean pass(double value) {
                        return value >= c;
                    }
                };
            }
            if (test == 4) {
                return new CellFormatCondition() {
                    public boolean pass(double value) {
                        return value == c;
                    }
                };
            }
            if (test == 5) {
                return new CellFormatCondition() {
                    public boolean pass(double value) {
                        return value != c;
                    }
                };
            }
            throw new IllegalArgumentException("Cannot create for test number " + test + "(\"" + opString + "\")");
        }
        throw new IllegalArgumentException("Unknown test: " + opString);
    }
}
