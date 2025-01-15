package org.apache.poi.ss.usermodel;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExcelStyleDateFormatter extends SimpleDateFormat {
    public static final char HH_BRACKET_SYMBOL = '';
    public static final char H_BRACKET_SYMBOL = '';
    public static final char MMMMM_START_SYMBOL = '';
    public static final char MMMMM_TRUNCATE_SYMBOL = '';
    public static final char MM_BRACKET_SYMBOL = '';
    public static final char M_BRACKET_SYMBOL = '';
    public static final char SS_BRACKET_SYMBOL = '';
    public static final char S_BRACKET_SYMBOL = '';
    private double dateToBeFormatted;
    private DecimalFormat format1digit = new DecimalFormat("0");
    private DecimalFormat format2digits = new DecimalFormat("00");

    public ExcelStyleDateFormatter() {
        DataFormatter.setExcelStyleRoundingMode(this.format1digit);
        DataFormatter.setExcelStyleRoundingMode(this.format2digits);
        this.dateToBeFormatted = 0.0d;
    }

    public ExcelStyleDateFormatter(String pattern) {
        super(processFormatPattern(pattern));
        DataFormatter.setExcelStyleRoundingMode(this.format1digit);
        DataFormatter.setExcelStyleRoundingMode(this.format2digits);
        this.dateToBeFormatted = 0.0d;
    }

    public ExcelStyleDateFormatter(String pattern, DateFormatSymbols formatSymbols) {
        super(processFormatPattern(pattern), formatSymbols);
        DataFormatter.setExcelStyleRoundingMode(this.format1digit);
        DataFormatter.setExcelStyleRoundingMode(this.format2digits);
        this.dateToBeFormatted = 0.0d;
    }

    public ExcelStyleDateFormatter(String pattern, Locale locale) {
        super(processFormatPattern(pattern), locale);
        DataFormatter.setExcelStyleRoundingMode(this.format1digit);
        DataFormatter.setExcelStyleRoundingMode(this.format2digits);
        this.dateToBeFormatted = 0.0d;
    }

    private static String processFormatPattern(String f) {
        return f.replaceAll("MMMMM", "MMM").replaceAll("\\[H\\]", String.valueOf(H_BRACKET_SYMBOL)).replaceAll("\\[HH\\]", String.valueOf(HH_BRACKET_SYMBOL)).replaceAll("\\[m\\]", String.valueOf(M_BRACKET_SYMBOL)).replaceAll("\\[mm\\]", String.valueOf(MM_BRACKET_SYMBOL)).replaceAll("\\[s\\]", String.valueOf(S_BRACKET_SYMBOL)).replaceAll("\\[ss\\]", String.valueOf(SS_BRACKET_SYMBOL));
    }

    public void setDateToBeFormatted(double date) {
        this.dateToBeFormatted = date;
    }

    public StringBuffer format(Date date, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition) {
        String s = super.format(date, paramStringBuffer, paramFieldPosition).toString();
        if (s.indexOf(57345) != -1) {
            s = s.replaceAll("(\\w)\\w+", "$1");
        }
        if (!(s.indexOf(57360) == -1 && s.indexOf(57361) == -1)) {
            double hours = this.dateToBeFormatted * 24.0d;
            s = s.replaceAll(String.valueOf(H_BRACKET_SYMBOL), this.format1digit.format(hours)).replaceAll(String.valueOf(HH_BRACKET_SYMBOL), this.format2digits.format(hours));
        }
        if (!(s.indexOf(57362) == -1 && s.indexOf(57363) == -1)) {
            double minutes = this.dateToBeFormatted * 24.0d * 60.0d;
            s = s.replaceAll(String.valueOf(M_BRACKET_SYMBOL), this.format1digit.format(minutes)).replaceAll(String.valueOf(MM_BRACKET_SYMBOL), this.format2digits.format(minutes));
        }
        if (!(s.indexOf(57364) == -1 && s.indexOf(57365) == -1)) {
            double seconds = this.dateToBeFormatted * 24.0d * 60.0d * 60.0d;
            s = s.replaceAll(String.valueOf(S_BRACKET_SYMBOL), this.format1digit.format(seconds)).replaceAll(String.valueOf(SS_BRACKET_SYMBOL), this.format2digits.format(seconds));
        }
        return new StringBuffer(s);
    }
}
