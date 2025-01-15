package org.apache.poi.hssf.usermodel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.record.formula.NumberPtg;
import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.hssf.record.formula.StringPtg;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.Sheet;

public class DVConstraint implements DataValidationConstraint {
    private static final DataValidationConstraint.ValidationType VT = null;
    private String[] _explicitListValues;
    private String _formula1;
    private String _formula2;
    private int _operator;
    private final int _validationType;
    private Double _value1;
    private Double _value2;

    public static final class FormulaPair {
        private final Ptg[] _formula1;
        private final Ptg[] _formula2;

        public FormulaPair(Ptg[] formula1, Ptg[] formula2) {
            this._formula1 = formula1;
            this._formula2 = formula2;
        }

        public Ptg[] getFormula1() {
            return this._formula1;
        }

        public Ptg[] getFormula2() {
            return this._formula2;
        }
    }

    private DVConstraint(int validationType, int comparisonOperator, String formulaA, String formulaB, Double value1, Double value2, String[] excplicitListValues) {
        this._validationType = validationType;
        this._operator = comparisonOperator;
        this._formula1 = formulaA;
        this._formula2 = formulaB;
        this._value1 = value1;
        this._value2 = value2;
        this._explicitListValues = excplicitListValues;
    }

    private DVConstraint(String listFormula, String[] excplicitListValues) {
        this(3, 0, listFormula, (String) null, (Double) null, (Double) null, excplicitListValues);
    }

    public static DVConstraint createNumericConstraint(int validationType, int comparisonOperator, String expr1, String expr2) {
        if (validationType != 0) {
            if (validationType != 1 && validationType != 2 && validationType != 6) {
                throw new IllegalArgumentException("Validation Type (" + validationType + ") not supported with this method");
            } else if (expr1 != null) {
                DataValidationConstraint.OperatorType.validateSecondArg(comparisonOperator, expr2);
            } else {
                throw new IllegalArgumentException("expr1 must be supplied");
            }
        } else if (!(expr1 == null && expr2 == null)) {
            throw new IllegalArgumentException("expr1 and expr2 must be null for validation type 'any'");
        }
        String formula1 = getFormulaFromTextExpression(expr1);
        Double value2 = null;
        Double value1 = formula1 == null ? convertNumber(expr1) : null;
        String formula2 = getFormulaFromTextExpression(expr2);
        if (formula2 == null) {
            value2 = convertNumber(expr2);
        }
        return new DVConstraint(validationType, comparisonOperator, formula1, formula2, value1, value2, (String[]) null);
    }

    public static DVConstraint createFormulaListConstraint(String listFormula) {
        return new DVConstraint(listFormula, (String[]) null);
    }

    public static DVConstraint createExplicitListConstraint(String[] explicitListValues) {
        return new DVConstraint((String) null, explicitListValues);
    }

    public static DVConstraint createTimeConstraint(int comparisonOperator, String expr1, String expr2) {
        if (expr1 != null) {
            DataValidationConstraint.OperatorType.validateSecondArg(comparisonOperator, expr1);
            String formula1 = getFormulaFromTextExpression(expr1);
            Double value2 = null;
            Double value1 = formula1 == null ? convertTime(expr1) : null;
            String formula2 = getFormulaFromTextExpression(expr2);
            if (formula2 == null) {
                value2 = convertTime(expr2);
            }
            return new DVConstraint(5, comparisonOperator, formula1, formula2, value1, value2, (String[]) null);
        }
        throw new IllegalArgumentException("expr1 must be supplied");
    }

    public static DVConstraint createDateConstraint(int comparisonOperator, String expr1, String expr2, String dateFormat) {
        if (expr1 != null) {
            DataValidationConstraint.OperatorType.validateSecondArg(comparisonOperator, expr2);
            Double value2 = null;
            SimpleDateFormat df = dateFormat == null ? null : new SimpleDateFormat(dateFormat);
            String formula1 = getFormulaFromTextExpression(expr1);
            Double value1 = formula1 == null ? convertDate(expr1, df) : null;
            String formula2 = getFormulaFromTextExpression(expr2);
            if (formula2 == null) {
                value2 = convertDate(expr2, df);
            }
            return new DVConstraint(4, comparisonOperator, formula1, formula2, value1, value2, (String[]) null);
        }
        throw new IllegalArgumentException("expr1 must be supplied");
    }

    private static String getFormulaFromTextExpression(String textExpr) {
        if (textExpr == null) {
            return null;
        }
        if (textExpr.length() < 1) {
            throw new IllegalArgumentException("Empty string is not a valid formula/value expression");
        } else if (textExpr.charAt(0) == '=') {
            return textExpr.substring(1);
        } else {
            return null;
        }
    }

    private static Double convertNumber(String numberStr) {
        if (numberStr == null) {
            return null;
        }
        try {
            return new Double(numberStr);
        } catch (NumberFormatException e) {
            throw new RuntimeException("The supplied text '" + numberStr + "' could not be parsed as a number");
        }
    }

    private static Double convertTime(String timeStr) {
        if (timeStr == null) {
            return null;
        }
        return new Double(HSSFDateUtil.convertTime(timeStr));
    }

    private static Double convertDate(String dateStr, SimpleDateFormat dateFormat) {
        Date dateVal;
        if (dateStr == null) {
            return null;
        }
        if (dateFormat == null) {
            dateVal = HSSFDateUtil.parseYYYYMMDDDate(dateStr);
        } else {
            try {
                dateVal = dateFormat.parse(dateStr);
            } catch (ParseException e) {
                throw new RuntimeException("Failed to parse date '" + dateStr + "' using specified format '" + dateFormat + "'", e);
            }
        }
        return new Double(HSSFDateUtil.getExcelDate(dateVal));
    }

    public static DVConstraint createCustomFormulaConstraint(String formula) {
        if (formula != null) {
            return new DVConstraint(7, 0, formula, (String) null, (Double) null, (Double) null, (String[]) null);
        }
        throw new IllegalArgumentException("formula must be supplied");
    }

    public int getValidationType() {
        return this._validationType;
    }

    public boolean isListValidationType() {
        return this._validationType == 3;
    }

    public boolean isExplicitList() {
        return this._validationType == 3 && this._explicitListValues != null;
    }

    public int getOperator() {
        return this._operator;
    }

    public void setOperator(int operator) {
        this._operator = operator;
    }

    public String[] getExplicitListValues() {
        return this._explicitListValues;
    }

    public void setExplicitListValues(String[] explicitListValues) {
        if (this._validationType == 3) {
            this._formula1 = null;
            this._explicitListValues = explicitListValues;
            return;
        }
        throw new RuntimeException("Cannot setExplicitListValues on non-list constraint");
    }

    public String getFormula1() {
        return this._formula1;
    }

    public void setFormula1(String formula1) {
        this._value1 = null;
        this._explicitListValues = null;
        this._formula1 = formula1;
    }

    public String getFormula2() {
        return this._formula2;
    }

    public void setFormula2(String formula2) {
        this._value2 = null;
        this._formula2 = formula2;
    }

    public Double getValue1() {
        return this._value1;
    }

    public void setValue1(double value1) {
        this._formula1 = null;
        this._value1 = new Double(value1);
    }

    public Double getValue2() {
        return this._value2;
    }

    public void setValue2(double value2) {
        this._formula2 = null;
        this._value2 = new Double(value2);
    }

    /* access modifiers changed from: package-private */
    public FormulaPair createFormulas(HSSFSheet sheet) {
        Ptg[] formula2;
        Ptg[] formula1;
        if (isListValidationType()) {
            formula1 = createListFormula(sheet);
            formula2 = Ptg.EMPTY_PTG_ARRAY;
        } else {
            formula1 = convertDoubleFormula(this._formula1, this._value1, sheet);
            formula2 = convertDoubleFormula(this._formula2, this._value2, sheet);
        }
        return new FormulaPair(formula1, formula2);
    }

    private Ptg[] createListFormula(HSSFSheet sheet) {
        String[] strArr = this._explicitListValues;
        if (strArr == null) {
            HSSFWorkbook wb = sheet.getWorkbook();
            return HSSFFormulaParser.parse(this._formula1, wb, 5, wb.getSheetIndex((Sheet) sheet));
        }
        StringBuffer sb = new StringBuffer(strArr.length * 16);
        for (int i = 0; i < this._explicitListValues.length; i++) {
            if (i > 0) {
                sb.append(0);
            }
            sb.append(this._explicitListValues[i]);
        }
        return new Ptg[]{new StringPtg(sb.toString())};
    }

    private static Ptg[] convertDoubleFormula(String formula, Double value, HSSFSheet sheet) {
        if (formula == null) {
            if (value == null) {
                return Ptg.EMPTY_PTG_ARRAY;
            }
            return new Ptg[]{new NumberPtg(value.doubleValue())};
        } else if (value == null) {
            HSSFWorkbook wb = sheet.getWorkbook();
            return HSSFFormulaParser.parse(formula, wb, 0, wb.getSheetIndex((Sheet) sheet));
        } else {
            throw new IllegalStateException("Both formula and value cannot be present");
        }
    }
}
