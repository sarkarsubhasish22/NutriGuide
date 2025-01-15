package org.apache.poi.ss.usermodel;

import java.lang.reflect.InvocationTargetException;
import java.math.RoundingMode;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataFormatter {
    private static final Pattern amPmPattern = Pattern.compile("((A|P)[M/P]*)", 2);
    private static final Pattern colorPattern = Pattern.compile("(\\[BLACK\\])|(\\[BLUE\\])|(\\[CYAN\\])|(\\[GREEN\\])|(\\[MAGENTA\\])|(\\[RED\\])|(\\[WHITE\\])|(\\[YELLOW\\])|(\\[COLOR\\s*\\d\\])|(\\[COLOR\\s*[0-5]\\d\\])", 2);
    private static final Pattern daysAsText = Pattern.compile("([d]{3,})", 2);
    private static final Pattern numPattern = Pattern.compile("[0#]+");
    private static final Pattern specialPatternGroup = Pattern.compile("(\\[\\$[^-\\]]*-[0-9A-Z]+\\])");
    private final DateFormatSymbols dateSymbols;
    private final DecimalFormatSymbols decimalSymbols;
    private Format defaultNumFormat;
    private final Map<String, Format> formats;
    private final Format generalDecimalNumFormat;
    private final Format generalWholeNumFormat;

    public DataFormatter() {
        this(Locale.getDefault());
    }

    public DataFormatter(Locale locale) {
        this.dateSymbols = new DateFormatSymbols(locale);
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(locale);
        this.decimalSymbols = decimalFormatSymbols;
        this.generalWholeNumFormat = new DecimalFormat("#", decimalFormatSymbols);
        this.generalDecimalNumFormat = new DecimalFormat("#.##########", decimalFormatSymbols);
        this.formats = new HashMap();
        Format zipFormat = ZipPlusFourFormat.instance;
        addFormat("00000\\-0000", zipFormat);
        addFormat("00000-0000", zipFormat);
        Format phoneFormat = PhoneFormat.instance;
        addFormat("[<=9999999]###\\-####;\\(###\\)\\ ###\\-####", phoneFormat);
        addFormat("[<=9999999]###-####;(###) ###-####", phoneFormat);
        addFormat("###\\-####;\\(###\\)\\ ###\\-####", phoneFormat);
        addFormat("###-####;(###) ###-####", phoneFormat);
        Format ssnFormat = SSNFormat.instance;
        addFormat("000\\-00\\-0000", ssnFormat);
        addFormat("000-00-0000", ssnFormat);
    }

    private Format getFormat(Cell cell) {
        if (cell.getCellStyle() == null) {
            return null;
        }
        int formatIndex = cell.getCellStyle().getDataFormat();
        String formatStr = cell.getCellStyle().getDataFormatString();
        if (formatStr == null || formatStr.trim().length() == 0) {
            return null;
        }
        return getFormat(cell.getNumericCellValue(), formatIndex, formatStr);
    }

    private Format getFormat(double cellValue, int formatIndex, String formatStr) {
        if (formatStr.indexOf(59) != formatStr.lastIndexOf(59)) {
            int lastAt = formatStr.lastIndexOf(59);
            String zeroFormat = formatStr.substring(lastAt + 1);
            String normalFormat = formatStr.substring(0, lastAt);
            if (cellValue == 0.0d) {
                formatStr = zeroFormat;
            } else {
                formatStr = normalFormat;
            }
        }
        Format format = this.formats.get(formatStr);
        if (format != null) {
            return format;
        }
        if (!"General".equals(formatStr) && !"@".equals(formatStr)) {
            Format format2 = createFormat(cellValue, formatIndex, formatStr);
            this.formats.put(formatStr, format2);
            return format2;
        } else if (isWholeNumber(cellValue)) {
            return this.generalWholeNumFormat;
        } else {
            return this.generalDecimalNumFormat;
        }
    }

    public Format createFormat(Cell cell) {
        return createFormat(cell.getNumericCellValue(), cell.getCellStyle().getDataFormat(), cell.getCellStyle().getDataFormatString());
    }

    private Format createFormat(double cellValue, int formatIndex, String sFormat) {
        String colour;
        int at;
        String formatStr = sFormat;
        Matcher colourM = colorPattern.matcher(formatStr);
        while (colourM.find() && (at = formatStr.indexOf((colour = colourM.group()))) != -1) {
            String nFormatStr = formatStr.substring(0, at) + formatStr.substring(colour.length() + at);
            if (nFormatStr.equals(formatStr)) {
                break;
            }
            formatStr = nFormatStr;
            colourM = colorPattern.matcher(formatStr);
        }
        Matcher m = specialPatternGroup.matcher(formatStr);
        while (m.find()) {
            String match = m.group();
            String symbol = match.substring(match.indexOf(36) + 1, match.indexOf(45));
            if (symbol.indexOf(36) > -1) {
                StringBuffer sb = new StringBuffer();
                sb.append(symbol.substring(0, symbol.indexOf(36)));
                sb.append('\\');
                sb.append(symbol.substring(symbol.indexOf(36), symbol.length()));
                symbol = sb.toString();
            }
            formatStr = m.replaceAll(symbol);
            m = specialPatternGroup.matcher(formatStr);
        }
        if (formatStr == null || formatStr.trim().length() == 0) {
            return getDefaultFormat(cellValue);
        }
        if (DateUtil.isADateFormat(formatIndex, formatStr) && DateUtil.isValidExcelDate(cellValue)) {
            return createDateFormat(formatStr, cellValue);
        }
        if (numPattern.matcher(formatStr).find()) {
            return createNumberFormat(formatStr, cellValue);
        }
        return null;
    }

    private Format createDateFormat(String pFormatStr, double cellValue) {
        String formatStr = pFormatStr.replaceAll("\\\\-", "-").replaceAll("\\\\,", ",").replaceAll("\\\\ ", " ").replaceAll("\\\\/", "/").replaceAll(";@", "").replaceAll("\"/\"", "/");
        boolean hasAmPm = false;
        Matcher amPmMatcher = amPmPattern.matcher(formatStr);
        while (amPmMatcher.find()) {
            formatStr = amPmMatcher.replaceAll("@");
            hasAmPm = true;
            amPmMatcher = amPmPattern.matcher(formatStr);
        }
        String formatStr2 = formatStr.replaceAll("@", "a");
        Matcher dateMatcher = daysAsText.matcher(formatStr2);
        if (dateMatcher.find()) {
            formatStr2 = dateMatcher.replaceAll(dateMatcher.group(0).toUpperCase().replaceAll("D", "E"));
        }
        StringBuffer sb = new StringBuffer();
        char[] chars = formatStr2.toCharArray();
        boolean mIsMonth = true;
        List<Integer> ms = new ArrayList<>();
        for (char c : chars) {
            if (c == 'h' || c == 'H') {
                mIsMonth = false;
                if (hasAmPm) {
                    sb.append('h');
                } else {
                    sb.append('H');
                }
            } else if (c == 'm' || c == 'M') {
                if (mIsMonth) {
                    sb.append('M');
                    ms.add(Integer.valueOf(sb.length() - 1));
                } else {
                    sb.append('m');
                }
            } else if (c == 's' || c == 'S') {
                sb.append('s');
                for (int i = 0; i < ms.size(); i++) {
                    int index = ms.get(i).intValue();
                    if (sb.charAt(index) == 'M') {
                        sb.replace(index, index + 1, "m");
                    }
                }
                mIsMonth = true;
                ms.clear();
            } else if (Character.isLetter(c)) {
                mIsMonth = true;
                ms.clear();
                if (c == 'y' || c == 'Y') {
                    sb.append('y');
                } else if (c == 'd' || c == 'D') {
                    sb.append('d');
                } else {
                    sb.append(c);
                }
            } else {
                sb.append(c);
            }
        }
        try {
            return new ExcelStyleDateFormatter(sb.toString(), this.dateSymbols);
        } catch (IllegalArgumentException e) {
            return getDefaultFormat(cellValue);
        }
    }

    private Format createNumberFormat(String formatStr, double cellValue) {
        StringBuffer sb = new StringBuffer(formatStr);
        for (int i = 0; i < sb.length(); i++) {
            char c = sb.charAt(i);
            if ((c == '_' || c == '*') && (i <= 0 || sb.charAt(i - 1) != '\\')) {
                if (i < sb.length() - 1) {
                    sb.deleteCharAt(i + 1);
                }
                sb.deleteCharAt(i);
            }
        }
        int i2 = 0;
        while (i2 < sb.length()) {
            char c2 = sb.charAt(i2);
            if (c2 == '\\' || c2 == '\"') {
                sb.deleteCharAt(i2);
                i2--;
            } else if (c2 == '+' && i2 > 0 && sb.charAt(i2 - 1) == 'E') {
                sb.deleteCharAt(i2);
                i2--;
            }
            i2++;
        }
        try {
            DecimalFormat df = new DecimalFormat(sb.toString(), this.decimalSymbols);
            setExcelStyleRoundingMode(df);
            return df;
        } catch (IllegalArgumentException e) {
            return getDefaultFormat(cellValue);
        }
    }

    private static boolean isWholeNumber(double d) {
        return d == Math.floor(d);
    }

    public Format getDefaultFormat(Cell cell) {
        return getDefaultFormat(cell.getNumericCellValue());
    }

    private Format getDefaultFormat(double cellValue) {
        Format format = this.defaultNumFormat;
        if (format != null) {
            return format;
        }
        if (isWholeNumber(cellValue)) {
            return this.generalWholeNumFormat;
        }
        return this.generalDecimalNumFormat;
    }

    private String performDateFormatting(Date d, Format dateFormat) {
        if (dateFormat != null) {
            return dateFormat.format(d);
        }
        return d.toString();
    }

    private String getFormattedDateString(Cell cell) {
        Format dateFormat = getFormat(cell);
        if (dateFormat instanceof ExcelStyleDateFormatter) {
            ((ExcelStyleDateFormatter) dateFormat).setDateToBeFormatted(cell.getNumericCellValue());
        }
        return performDateFormatting(cell.getDateCellValue(), dateFormat);
    }

    private String getFormattedNumberString(Cell cell) {
        Format numberFormat = getFormat(cell);
        double d = cell.getNumericCellValue();
        if (numberFormat == null) {
            return String.valueOf(d);
        }
        return numberFormat.format(new Double(d));
    }

    public String formatRawCellContents(double value, int formatIndex, String formatString) {
        return formatRawCellContents(value, formatIndex, formatString, false);
    }

    public String formatRawCellContents(double value, int formatIndex, String formatString, boolean use1904Windowing) {
        if (!DateUtil.isADateFormat(formatIndex, formatString) || !DateUtil.isValidExcelDate(value)) {
            Format dateFormat = getFormat(value, formatIndex, formatString);
            if (dateFormat == null) {
                return String.valueOf(value);
            }
            return dateFormat.format(new Double(value));
        }
        Format dateFormat2 = getFormat(value, formatIndex, formatString);
        if (dateFormat2 instanceof ExcelStyleDateFormatter) {
            ((ExcelStyleDateFormatter) dateFormat2).setDateToBeFormatted(value);
        }
        return performDateFormatting(DateUtil.getJavaDate(value, use1904Windowing), dateFormat2);
    }

    public String formatCellValue(Cell cell) {
        return formatCellValue(cell, (FormulaEvaluator) null);
    }

    public String formatCellValue(Cell cell, FormulaEvaluator evaluator) {
        if (cell == null) {
            return "";
        }
        int cellType = cell.getCellType();
        if (cellType == 2) {
            if (evaluator == null) {
                return cell.getCellFormula();
            }
            cellType = evaluator.evaluateFormulaCell(cell);
        }
        if (cellType != 0) {
            if (cellType == 1) {
                return cell.getRichStringCellValue().getString();
            }
            if (cellType == 3) {
                return "";
            }
            if (cellType == 4) {
                return String.valueOf(cell.getBooleanCellValue());
            }
            throw new RuntimeException("Unexpected celltype (" + cellType + ")");
        } else if (DateUtil.isCellDateFormatted(cell)) {
            return getFormattedDateString(cell);
        } else {
            return getFormattedNumberString(cell);
        }
    }

    public void setDefaultNumberFormat(Format format) {
        for (Map.Entry<String, Format> entry : this.formats.entrySet()) {
            if (entry.getValue() == this.generalDecimalNumFormat || entry.getValue() == this.generalWholeNumFormat) {
                entry.setValue(format);
            }
        }
        this.defaultNumFormat = format;
    }

    public void addFormat(String excelFormatStr, Format format) {
        this.formats.put(excelFormatStr, format);
    }

    static DecimalFormat createIntegerOnlyFormat(String fmt) {
        DecimalFormat result = new DecimalFormat(fmt);
        result.setParseIntegerOnly(true);
        return result;
    }

    public static void setExcelStyleRoundingMode(DecimalFormat format) {
        try {
            format.getClass().getMethod("setRoundingMode", new Class[]{RoundingMode.class}).invoke(format, new Object[]{RoundingMode.HALF_UP});
        } catch (IllegalAccessException iae) {
            throw new RuntimeException("Unable to set rounding mode", iae);
        } catch (InvocationTargetException ite) {
            throw new RuntimeException("Unable to set rounding mode", ite);
        } catch (NoSuchMethodException | SecurityException e) {
        }
    }

    private static final class SSNFormat extends Format {
        private static final DecimalFormat df = DataFormatter.createIntegerOnlyFormat("000000000");
        public static final Format instance = new SSNFormat();

        private SSNFormat() {
        }

        public static String format(Number num) {
            String result = df.format(num);
            StringBuffer sb = new StringBuffer();
            sb.append(result.substring(0, 3));
            sb.append('-');
            sb.append(result.substring(3, 5));
            sb.append('-');
            sb.append(result.substring(5, 9));
            return sb.toString();
        }

        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            toAppendTo.append(format((Number) obj));
            return toAppendTo;
        }

        public Object parseObject(String source, ParsePosition pos) {
            return df.parseObject(source, pos);
        }
    }

    private static final class ZipPlusFourFormat extends Format {
        private static final DecimalFormat df = DataFormatter.createIntegerOnlyFormat("000000000");
        public static final Format instance = new ZipPlusFourFormat();

        private ZipPlusFourFormat() {
        }

        public static String format(Number num) {
            String result = df.format(num);
            StringBuffer sb = new StringBuffer();
            sb.append(result.substring(0, 5));
            sb.append('-');
            sb.append(result.substring(5, 9));
            return sb.toString();
        }

        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            toAppendTo.append(format((Number) obj));
            return toAppendTo;
        }

        public Object parseObject(String source, ParsePosition pos) {
            return df.parseObject(source, pos);
        }
    }

    private static final class PhoneFormat extends Format {
        private static final DecimalFormat df = DataFormatter.createIntegerOnlyFormat("##########");
        public static final Format instance = new PhoneFormat();

        private PhoneFormat() {
        }

        public static String format(Number num) {
            String result = df.format(num);
            StringBuffer sb = new StringBuffer();
            int len = result.length();
            if (len <= 4) {
                return result;
            }
            String seg3 = result.substring(len - 4, len);
            String seg2 = result.substring(Math.max(0, len - 7), len - 4);
            String seg1 = result.substring(Math.max(0, len - 10), Math.max(0, len - 7));
            if (seg1 != null && seg1.trim().length() > 0) {
                sb.append('(');
                sb.append(seg1);
                sb.append(") ");
            }
            if (seg2 != null && seg2.trim().length() > 0) {
                sb.append(seg2);
                sb.append('-');
            }
            sb.append(seg3);
            return sb.toString();
        }

        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            toAppendTo.append(format((Number) obj));
            return toAppendTo;
        }

        public Object parseObject(String source, ParsePosition pos) {
            return df.parseObject(source, pos);
        }
    }
}
