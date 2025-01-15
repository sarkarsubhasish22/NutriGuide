package org.apache.poi.ss.usermodel;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

public class DateUtil {
    private static final int BAD_DATE = -1;
    private static final long DAY_MILLISECONDS = 86400000;
    private static final int HOURS_PER_DAY = 24;
    private static final int MINUTES_PER_HOUR = 60;
    private static final int SECONDS_PER_DAY = 86400;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final Pattern TIME_SEPARATOR_PATTERN = Pattern.compile(":");
    private static final Pattern date_ptrn1 = Pattern.compile("^\\[\\$\\-.*?\\]");
    private static final Pattern date_ptrn2 = Pattern.compile("^\\[[a-zA-Z]+\\]");
    private static final Pattern date_ptrn3 = Pattern.compile("^[yYmMdDhHsS\\-/,. :\"\\\\]+0?[ampAMP/]*$");

    protected DateUtil() {
    }

    public static double getExcelDate(Date date) {
        return getExcelDate(date, false);
    }

    public static double getExcelDate(Date date, boolean use1904windowing) {
        Calendar calStart = new GregorianCalendar();
        calStart.setTime(date);
        return internalGetExcelDate(calStart, use1904windowing);
    }

    public static double getExcelDate(Calendar date, boolean use1904windowing) {
        return internalGetExcelDate((Calendar) date.clone(), use1904windowing);
    }

    private static double internalGetExcelDate(Calendar date, boolean use1904windowing) {
        if (!use1904windowing && date.get(1) < 1900) {
            return -1.0d;
        }
        if (use1904windowing && date.get(1) < 1904) {
            return -1.0d;
        }
        double d = (double) ((((((date.get(11) * 60) + date.get(12)) * 60) + date.get(13)) * 1000) + date.get(14));
        Double.isNaN(d);
        double absoluteDay = (double) absoluteDay(dayStart(date), use1904windowing);
        Double.isNaN(absoluteDay);
        double value = absoluteDay + (d / 8.64E7d);
        if (!use1904windowing && value >= 60.0d) {
            return value + 1.0d;
        }
        if (use1904windowing) {
            return value - 1.0d;
        }
        return value;
    }

    public static Date getJavaDate(double date) {
        return getJavaDate(date, false);
    }

    public static Date getJavaDate(double date, boolean use1904windowing) {
        if (!isValidExcelDate(date)) {
            return null;
        }
        int wholeDays = (int) Math.floor(date);
        double d = (double) wholeDays;
        Double.isNaN(d);
        Calendar calendar = new GregorianCalendar();
        setCalendar(calendar, wholeDays, (int) (((date - d) * 8.64E7d) + 0.5d), use1904windowing);
        return calendar.getTime();
    }

    public static void setCalendar(Calendar calendar, int wholeDays, int millisecondsInDay, boolean use1904windowing) {
        int startYear = 1900;
        int dayAdjust = -1;
        if (use1904windowing) {
            startYear = 1904;
            dayAdjust = 1;
        } else if (wholeDays < 61) {
            dayAdjust = 0;
        }
        calendar.set(startYear, 0, wholeDays + dayAdjust, 0, 0, 0);
        calendar.set(14, millisecondsInDay);
    }

    public static boolean isADateFormat(int formatIndex, String formatString) {
        if (isInternalDateFormat(formatIndex)) {
            return true;
        }
        if (formatString == null || formatString.length() == 0) {
            return false;
        }
        String fs = formatString;
        StringBuilder sb = new StringBuilder(fs.length());
        int i = 0;
        while (i < fs.length()) {
            char c = fs.charAt(i);
            if (i < fs.length() - 1) {
                char nc = fs.charAt(i + 1);
                if (c == '\\') {
                    if (!(nc == ' ' || nc == '\\')) {
                        switch (nc) {
                            case ',':
                            case '-':
                            case '.':
                                break;
                        }
                    }
                    i++;
                } else if (c == ';' && nc == '@') {
                    i++;
                    i++;
                }
            }
            sb.append(c);
            i++;
        }
        String fs2 = date_ptrn2.matcher(date_ptrn1.matcher(sb.toString()).replaceAll("")).replaceAll("");
        if (fs2.indexOf(59) > 0 && fs2.indexOf(59) < fs2.length() - 1) {
            fs2 = fs2.substring(0, fs2.indexOf(59));
        }
        return date_ptrn3.matcher(fs2).matches();
    }

    public static boolean isInternalDateFormat(int format) {
        switch (format) {
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
                return true;
            default:
                switch (format) {
                    case 45:
                    case 46:
                    case 47:
                        return true;
                    default:
                        return false;
                }
        }
    }

    public static boolean isCellDateFormatted(Cell cell) {
        CellStyle style;
        if (cell == null || !isValidExcelDate(cell.getNumericCellValue()) || (style = cell.getCellStyle()) == null) {
            return false;
        }
        return isADateFormat(style.getDataFormat(), style.getDataFormatString());
    }

    public static boolean isCellInternalDateFormatted(Cell cell) {
        if (cell != null && isValidExcelDate(cell.getNumericCellValue())) {
            return isInternalDateFormat(cell.getCellStyle().getDataFormat());
        }
        return false;
    }

    public static boolean isValidExcelDate(double value) {
        return value > -4.9E-324d;
    }

    protected static int absoluteDay(Calendar cal, boolean use1904windowing) {
        return cal.get(6) + daysInPriorYears(cal.get(1), use1904windowing);
    }

    private static int daysInPriorYears(int yr, boolean use1904windowing) {
        int i = 1900;
        if ((use1904windowing || yr >= 1900) && (!use1904windowing || yr >= 1900)) {
            int yr1 = yr - 1;
            int leapDays = (((yr1 / 4) - (yr1 / 100)) + (yr1 / 400)) - 460;
            if (use1904windowing) {
                i = 1904;
            }
            return ((yr - i) * 365) + leapDays;
        }
        throw new IllegalArgumentException("'year' must be 1900 or greater");
    }

    private static Calendar dayStart(Calendar cal) {
        cal.get(11);
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        cal.get(11);
        return cal;
    }

    private static final class FormatException extends Exception {
        public FormatException(String msg) {
            super(msg);
        }
    }

    public static double convertTime(String timeStr) {
        try {
            return convertTimeInternal(timeStr);
        } catch (FormatException e) {
            throw new IllegalArgumentException("Bad time format '" + timeStr + "' expected 'HH:MM' or 'HH:MM:SS' - " + e.getMessage());
        }
    }

    private static double convertTimeInternal(String timeStr) throws FormatException {
        String secStr;
        int len = timeStr.length();
        if (len < 4 || len > 8) {
            throw new FormatException("Bad length");
        }
        String[] parts = TIME_SEPARATOR_PATTERN.split(timeStr);
        int length = parts.length;
        if (length == 2) {
            secStr = "00";
        } else if (length == 3) {
            secStr = parts[2];
        } else {
            throw new FormatException("Expected 2 or 3 fields but got (" + parts.length + ")");
        }
        String hourStr = parts[0];
        String minStr = parts[1];
        int hours = parseInt(hourStr, "hour", 24);
        double totalSeconds = (double) ((((hours * 60) + parseInt(minStr, "minute", 60)) * 60) + parseInt(secStr, "second", 60));
        Double.isNaN(totalSeconds);
        return totalSeconds / 86400.0d;
    }

    public static Date parseYYYYMMDDDate(String dateStr) {
        try {
            return parseYYYYMMDDDateInternal(dateStr);
        } catch (FormatException e) {
            throw new IllegalArgumentException("Bad time format " + dateStr + " expected 'YYYY/MM/DD' - " + e.getMessage());
        }
    }

    private static Date parseYYYYMMDDDateInternal(String timeStr) throws FormatException {
        if (timeStr.length() == 10) {
            String yearStr = timeStr.substring(0, 4);
            String monthStr = timeStr.substring(5, 7);
            String dayStr = timeStr.substring(8, 10);
            int year = parseInt(yearStr, "year", -32768, 32767);
            int i = year;
            Calendar cal = new GregorianCalendar(i, parseInt(monthStr, "month", 1, 12) - 1, parseInt(dayStr, "day", 1, 31), 0, 0, 0);
            cal.set(14, 0);
            return cal.getTime();
        }
        throw new FormatException("Bad length");
    }

    private static int parseInt(String strVal, String fieldName, int rangeMax) throws FormatException {
        return parseInt(strVal, fieldName, 0, rangeMax - 1);
    }

    private static int parseInt(String strVal, String fieldName, int lowerLimit, int upperLimit) throws FormatException {
        try {
            int result = Integer.parseInt(strVal);
            if (result >= lowerLimit && result <= upperLimit) {
                return result;
            }
            throw new FormatException(fieldName + " value (" + result + ") is outside the allowable range(0.." + upperLimit + ")");
        } catch (NumberFormatException e) {
            throw new FormatException("Bad int format '" + strVal + "' for " + fieldName + " field");
        }
    }
}
