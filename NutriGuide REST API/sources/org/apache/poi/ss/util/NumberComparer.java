package org.apache.poi.ss.util;

public final class NumberComparer {
    public static int compare(double a, double b) {
        long rawBitsA = Double.doubleToLongBits(a);
        long rawBitsB = Double.doubleToLongBits(b);
        int biasedExponentA = IEEEDouble.getBiasedExponent(rawBitsA);
        int biasedExponentB = IEEEDouble.getBiasedExponent(rawBitsB);
        if (biasedExponentA == 2047) {
            throw new IllegalArgumentException("Special double values are not allowed: " + toHex(a));
        } else if (biasedExponentB != 2047) {
            boolean aIsNegative = rawBitsA < 0;
            if (aIsNegative == (rawBitsB < 0)) {
                int cmp = biasedExponentA - biasedExponentB;
                int absExpDiff = Math.abs(cmp);
                if (absExpDiff > 1) {
                    return aIsNegative ? -cmp : cmp;
                }
                if (absExpDiff != 1 && rawBitsA == rawBitsB) {
                    return 0;
                }
                if (biasedExponentA == 0) {
                    if (biasedExponentB == 0) {
                        return compareSubnormalNumbers(rawBitsA & IEEEDouble.FRAC_MASK, IEEEDouble.FRAC_MASK & rawBitsB, aIsNegative);
                    }
                    return -compareAcrossSubnormalThreshold(rawBitsB, rawBitsA, aIsNegative);
                } else if (biasedExponentB == 0) {
                    return compareAcrossSubnormalThreshold(rawBitsA, rawBitsB, aIsNegative);
                } else {
                    int cmp2 = ExpandedDouble.fromRawBitsAndExponent(rawBitsA, biasedExponentA - 1023).normaliseBaseTen().roundUnits().compareNormalised(ExpandedDouble.fromRawBitsAndExponent(rawBitsB, biasedExponentB - 1023).normaliseBaseTen().roundUnits());
                    if (aIsNegative) {
                        return -cmp2;
                    }
                    return cmp2;
                }
            } else if (aIsNegative) {
                return -1;
            } else {
                return 1;
            }
        } else {
            throw new IllegalArgumentException("Special double values are not allowed: " + toHex(a));
        }
    }

    private static int compareSubnormalNumbers(long fracA, long fracB, boolean isNegative) {
        int cmp = fracA > fracB ? 1 : fracA < fracB ? -1 : 0;
        return isNegative ? -cmp : cmp;
    }

    private static int compareAcrossSubnormalThreshold(long normalRawBitsA, long subnormalRawBitsB, boolean isNegative) {
        long fracB = subnormalRawBitsB & IEEEDouble.FRAC_MASK;
        if (fracB != 0) {
            long fracA = IEEEDouble.FRAC_MASK & normalRawBitsA;
            if (fracA > 7 || fracB < 4503599627370490L) {
                if (isNegative) {
                    return -1;
                }
                return 1;
            } else if (fracA == 7 && fracB == 4503599627370490L) {
                return 0;
            } else {
                if (isNegative) {
                    return 1;
                }
                return -1;
            }
        } else if (isNegative) {
            return -1;
        } else {
            return 1;
        }
    }

    private static String toHex(double a) {
        return "0x" + Long.toHexString(Double.doubleToLongBits(a)).toUpperCase();
    }
}
