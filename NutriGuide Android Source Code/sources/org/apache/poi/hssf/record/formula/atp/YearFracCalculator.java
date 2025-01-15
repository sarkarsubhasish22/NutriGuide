package org.apache.poi.hssf.record.formula.atp;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.formula.eval.EvaluationException;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;

final class YearFracCalculator {
    private static final int DAYS_PER_LEAP_YEAR = 366;
    private static final int DAYS_PER_NORMAL_YEAR = 365;
    private static final int LONG_FEB_LEN = 29;
    private static final int LONG_MONTH_LEN = 31;
    private static final int MS_PER_DAY = 86400000;
    private static final int MS_PER_HOUR = 3600000;
    private static final int SHORT_FEB_LEN = 28;
    private static final int SHORT_MONTH_LEN = 30;
    private static final TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");

    private YearFracCalculator() {
    }

    public static double calculate(double pStartDateVal, double pEndDateVal, int basis) throws EvaluationException {
        if (basis < 0 || basis >= 5) {
            throw new EvaluationException(ErrorEval.NUM_ERROR);
        }
        int startDateVal = (int) Math.floor(pStartDateVal);
        int endDateVal = (int) Math.floor(pEndDateVal);
        if (startDateVal == endDateVal) {
            return 0.0d;
        }
        if (startDateVal > endDateVal) {
            int temp = startDateVal;
            startDateVal = endDateVal;
            endDateVal = temp;
        }
        if (basis == 0) {
            return basis0(startDateVal, endDateVal);
        }
        if (basis == 1) {
            return basis1(startDateVal, endDateVal);
        }
        if (basis == 2) {
            return basis2(startDateVal, endDateVal);
        }
        if (basis == 3) {
            return basis3((double) startDateVal, (double) endDateVal);
        }
        if (basis == 4) {
            return basis4(startDateVal, endDateVal);
        }
        throw new IllegalStateException("cannot happen");
    }

    public static double basis0(int startDateVal, int endDateVal) {
        SimpleDate startDate = createDate(startDateVal);
        SimpleDate endDate = createDate(endDateVal);
        int date1day = startDate.day;
        int date2day = endDate.day;
        if (date1day == 31 && date2day == 31) {
            date1day = 30;
            date2day = 30;
        } else if (date1day == 31) {
            date1day = 30;
        } else if (date1day == 30 && date2day == 31) {
            date2day = 30;
        } else if (startDate.month == 2 && isLastDayOfMonth(startDate)) {
            date1day = 30;
            if (endDate.month == 2 && isLastDayOfMonth(endDate)) {
                date2day = 30;
            }
        }
        return calculateAdjusted(startDate, endDate, date1day, date2day);
    }

    public static double basis1(int startDateVal, int endDateVal) {
        double yearLength;
        SimpleDate startDate = createDate(startDateVal);
        SimpleDate endDate = createDate(endDateVal);
        if (isGreaterThanOneYear(startDate, endDate)) {
            yearLength = averageYearLength(startDate.year, endDate.year);
        } else if (shouldCountFeb29(startDate, endDate)) {
            yearLength = 366.0d;
        } else {
            yearLength = 365.0d;
        }
        double dateDiff = (double) dateDiff(startDate.tsMilliseconds, endDate.tsMilliseconds);
        Double.isNaN(dateDiff);
        return dateDiff / yearLength;
    }

    public static double basis2(int startDateVal, int endDateVal) {
        double d = (double) (endDateVal - startDateVal);
        Double.isNaN(d);
        return d / 360.0d;
    }

    public static double basis3(double startDateVal, double endDateVal) {
        return (endDateVal - startDateVal) / 365.0d;
    }

    public static double basis4(int startDateVal, int endDateVal) {
        SimpleDate startDate = createDate(startDateVal);
        SimpleDate endDate = createDate(endDateVal);
        int date1day = startDate.day;
        int date2day = endDate.day;
        if (date1day == 31) {
            date1day = 30;
        }
        if (date2day == 31) {
            date2day = 30;
        }
        return calculateAdjusted(startDate, endDate, date1day, date2day);
    }

    private static double calculateAdjusted(SimpleDate startDate, SimpleDate endDate, int date1day, int date2day) {
        double dayCount = (double) (((endDate.year - startDate.year) * 360) + ((endDate.month - startDate.month) * 30) + ((date2day - date1day) * 1));
        Double.isNaN(dayCount);
        return dayCount / 360.0d;
    }

    private static boolean isLastDayOfMonth(SimpleDate date) {
        if (date.day >= 28 && date.day == getLastDayOfMonth(date)) {
            return true;
        }
        return false;
    }

    private static int getLastDayOfMonth(SimpleDate date) {
        switch (date.month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            default:
                if (isLeapYear(date.year)) {
                    return 29;
                }
                return 28;
        }
    }

    private static boolean shouldCountFeb29(SimpleDate start, SimpleDate end) {
        int i;
        boolean startIsLeapYear = isLeapYear(start.year);
        if (startIsLeapYear && start.year == end.year) {
            return true;
        }
        boolean endIsLeapYear = isLeapYear(end.year);
        if (!startIsLeapYear && !endIsLeapYear) {
            return false;
        }
        if (startIsLeapYear) {
            int i2 = start.month;
            if (i2 == 1 || i2 == 2) {
                return true;
            }
            return false;
        } else if (!endIsLeapYear || (i = end.month) == 1) {
            return false;
        } else {
            if (i == 2 && end.day != 29) {
                return false;
            }
            return true;
        }
    }

    private static int dateDiff(long startDateMS, long endDateMS) {
        long msDiff = endDateMS - startDateMS;
        if (((int) ((msDiff % 86400000) / 3600000)) == 0) {
            double d = (double) msDiff;
            Double.isNaN(d);
            return (int) ((d / 8.64E7d) + 0.5d);
        }
        throw new RuntimeException("Unexpected date diff between " + startDateMS + " and " + endDateMS);
    }

    private static double averageYearLength(int startYear, int endYear) {
        int dayCount = 0;
        for (int i = startYear; i <= endYear; i++) {
            dayCount += DAYS_PER_NORMAL_YEAR;
            if (isLeapYear(i)) {
                dayCount++;
            }
        }
        double numberOfYears = (double) ((endYear - startYear) + 1);
        double d = (double) dayCount;
        Double.isNaN(d);
        Double.isNaN(numberOfYears);
        return d / numberOfYears;
    }

    private static boolean isLeapYear(int i) {
        if (i % 4 != 0) {
            return false;
        }
        if (i % 400 != 0 && i % 100 == 0) {
            return false;
        }
        return true;
    }

    private static boolean isGreaterThanOneYear(SimpleDate start, SimpleDate end) {
        if (start.year == end.year) {
            return false;
        }
        if (start.year + 1 != end.year) {
            return true;
        }
        if (start.month > end.month) {
            return false;
        }
        if (start.month >= end.month && start.day >= end.day) {
            return false;
        }
        return true;
    }

    private static SimpleDate createDate(int dayCount) {
        GregorianCalendar calendar = new GregorianCalendar(UTC_TIME_ZONE);
        HSSFDateUtil.setCalendar(calendar, dayCount, 0, false);
        return new SimpleDate(calendar);
    }

    private static final class SimpleDate {
        public static final int FEBRUARY = 2;
        public static final int JANUARY = 1;
        public final int day;
        public final int month;
        public long tsMilliseconds;
        public final int year;

        public SimpleDate(Calendar cal) {
            this.year = cal.get(1);
            this.month = cal.get(2) + 1;
            this.day = cal.get(5);
            this.tsMilliseconds = cal.getTimeInMillis();
        }
    }
}
