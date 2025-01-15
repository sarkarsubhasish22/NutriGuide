package org.apache.poi.hssf.record.formula.functions;

import java.util.regex.Pattern;
import org.apache.poi.hssf.record.formula.eval.BlankEval;
import org.apache.poi.hssf.record.formula.eval.BoolEval;
import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.formula.eval.EvaluationException;
import org.apache.poi.hssf.record.formula.eval.NumberEval;
import org.apache.poi.hssf.record.formula.eval.OperandResolver;
import org.apache.poi.hssf.record.formula.eval.RefEval;
import org.apache.poi.hssf.record.formula.eval.StringEval;
import org.apache.poi.hssf.record.formula.eval.ValueEval;
import org.apache.poi.hssf.record.formula.functions.CountUtils;
import org.apache.poi.ss.formula.TwoDEval;
import org.apache.poi.ss.usermodel.ErrorConstants;

public final class Countif extends Fixed2ArgFunction {

    private static final class CmpOp {
        public static final int EQ = 1;
        public static final int GE = 6;
        public static final int GT = 5;
        public static final int LE = 3;
        public static final int LT = 4;
        public static final int NE = 2;
        public static final int NONE = 0;
        public static final CmpOp OP_EQ = op("=", 1);
        public static final CmpOp OP_GE = op(">=", 6);
        public static final CmpOp OP_GT = op(">", 5);
        public static final CmpOp OP_LE = op("<=", 3);
        public static final CmpOp OP_LT = op("<", 4);
        public static final CmpOp OP_NE = op("<>", 2);
        public static final CmpOp OP_NONE = op("", 0);
        private final int _code;
        private final String _representation;

        private static CmpOp op(String rep, int code) {
            return new CmpOp(rep, code);
        }

        private CmpOp(String representation, int code) {
            this._representation = representation;
            this._code = code;
        }

        public int getLength() {
            return this._representation.length();
        }

        public int getCode() {
            return this._code;
        }

        public static CmpOp getOperator(String value) {
            int len = value.length();
            if (len < 1) {
                return OP_NONE;
            }
            switch (value.charAt(0)) {
                case '<':
                    if (len > 1) {
                        char charAt = value.charAt(1);
                        if (charAt == '=') {
                            return OP_LE;
                        }
                        if (charAt == '>') {
                            return OP_NE;
                        }
                    }
                    return OP_LT;
                case '=':
                    return OP_EQ;
                case '>':
                    if (len <= 1 || value.charAt(1) != '=') {
                        return OP_GT;
                    }
                    return OP_GE;
                default:
                    return OP_NONE;
            }
        }

        public boolean evaluate(boolean cmpResult) {
            int i = this._code;
            if (i == 0 || i == 1) {
                return cmpResult;
            }
            if (i == 2) {
                return !cmpResult;
            }
            throw new RuntimeException("Cannot call boolean evaluate on non-equality operator '" + this._representation + "'");
        }

        public boolean evaluate(int cmpResult) {
            switch (this._code) {
                case 0:
                case 1:
                    if (cmpResult == 0) {
                        return true;
                    }
                    return false;
                case 2:
                    if (cmpResult != 0) {
                        return true;
                    }
                    return false;
                case 3:
                    if (cmpResult <= 0) {
                        return true;
                    }
                    return false;
                case 4:
                    if (cmpResult < 0) {
                        return true;
                    }
                    return false;
                case 5:
                    if (cmpResult > 0) {
                        return true;
                    }
                    return false;
                case 6:
                    if (cmpResult <= 0) {
                        return true;
                    }
                    return false;
                default:
                    throw new RuntimeException("Cannot call boolean evaluate on non-equality operator '" + this._representation + "'");
            }
        }

        public String toString() {
            StringBuffer sb = new StringBuffer(64);
            sb.append(getClass().getName());
            sb.append(" [");
            sb.append(this._representation);
            sb.append("]");
            return sb.toString();
        }

        public String getRepresentation() {
            return this._representation;
        }
    }

    private static abstract class MatcherBase implements CountUtils.I_MatchPredicate {
        private final CmpOp _operator;

        /* access modifiers changed from: protected */
        public abstract String getValueText();

        MatcherBase(CmpOp operator) {
            this._operator = operator;
        }

        /* access modifiers changed from: protected */
        public final int getCode() {
            return this._operator.getCode();
        }

        /* access modifiers changed from: protected */
        public final boolean evaluate(int cmpResult) {
            return this._operator.evaluate(cmpResult);
        }

        /* access modifiers changed from: protected */
        public final boolean evaluate(boolean cmpResult) {
            return this._operator.evaluate(cmpResult);
        }

        public final String toString() {
            StringBuffer sb = new StringBuffer(64);
            sb.append(getClass().getName());
            sb.append(" [");
            sb.append(this._operator.getRepresentation());
            sb.append(getValueText());
            sb.append("]");
            return sb.toString();
        }
    }

    private static final class NumberMatcher extends MatcherBase {
        private final double _value;

        public NumberMatcher(double value, CmpOp operator) {
            super(operator);
            this._value = value;
        }

        /* access modifiers changed from: protected */
        public String getValueText() {
            return String.valueOf(this._value);
        }

        public boolean matches(ValueEval x) {
            if (x instanceof StringEval) {
                int code = getCode();
                if (code == 0 || code == 1) {
                    Double val = OperandResolver.parseDouble(((StringEval) x).getStringValue());
                    if (val != null && this._value == val.doubleValue()) {
                        return true;
                    }
                    return false;
                } else if (code != 2) {
                    return false;
                } else {
                    return true;
                }
            } else if (x instanceof NumberEval) {
                return evaluate(Double.compare(((NumberEval) x).getNumberValue(), this._value));
            } else {
                return false;
            }
        }
    }

    private static final class BooleanMatcher extends MatcherBase {
        private final int _value;

        public BooleanMatcher(boolean value, CmpOp operator) {
            super(operator);
            this._value = boolToInt(value);
        }

        /* access modifiers changed from: protected */
        public String getValueText() {
            return this._value == 1 ? "TRUE" : "FALSE";
        }

        private static int boolToInt(boolean value) {
            return value;
        }

        public boolean matches(ValueEval x) {
            if (!(x instanceof StringEval) && (x instanceof BoolEval)) {
                return evaluate(boolToInt(((BoolEval) x).getBooleanValue()) - this._value);
            }
            return false;
        }
    }

    private static final class ErrorMatcher extends MatcherBase {
        private final int _value;

        public ErrorMatcher(int errorCode, CmpOp operator) {
            super(operator);
            this._value = errorCode;
        }

        /* access modifiers changed from: protected */
        public String getValueText() {
            return ErrorConstants.getText(this._value);
        }

        public boolean matches(ValueEval x) {
            if (x instanceof ErrorEval) {
                return evaluate(((ErrorEval) x).getErrorCode() - this._value);
            }
            return false;
        }
    }

    private static final class StringMatcher extends MatcherBase {
        private final Pattern _pattern;
        private final String _value;

        public StringMatcher(String value, CmpOp operator) {
            super(operator);
            this._value = value;
            int code = operator.getCode();
            if (code == 0 || code == 1 || code == 2) {
                this._pattern = getWildCardPattern(value);
            } else {
                this._pattern = null;
            }
        }

        /* access modifiers changed from: protected */
        public String getValueText() {
            Pattern pattern = this._pattern;
            if (pattern == null) {
                return this._value;
            }
            return pattern.pattern();
        }

        public boolean matches(ValueEval x) {
            if (x instanceof BlankEval) {
                int code = getCode();
                if ((code == 0 || code == 1) && this._value.length() == 0) {
                    return true;
                }
                return false;
            } else if (!(x instanceof StringEval)) {
                return false;
            } else {
                String testedValue = ((StringEval) x).getStringValue();
                if (testedValue.length() >= 1 || this._value.length() >= 1) {
                    Pattern pattern = this._pattern;
                    if (pattern != null) {
                        return evaluate(pattern.matcher(testedValue).matches());
                    }
                    return evaluate(testedValue.compareTo(this._value));
                }
                int code2 = getCode();
                return code2 == 0 || code2 == 2;
            }
        }

        private static Pattern getWildCardPattern(String value) {
            char ch;
            int len = value.length();
            StringBuffer sb = new StringBuffer(len);
            boolean hasWildCard = false;
            int i = 0;
            while (i < len) {
                char ch2 = value.charAt(i);
                if (!(ch2 == '$' || ch2 == '.')) {
                    if (ch2 == '?') {
                        hasWildCard = true;
                        sb.append('.');
                    } else if (ch2 != '[') {
                        if (ch2 != '~') {
                            if (!(ch2 == ']' || ch2 == '^')) {
                                switch (ch2) {
                                    case '(':
                                    case ')':
                                        break;
                                    case '*':
                                        hasWildCard = true;
                                        sb.append(".*");
                                        continue;
                                    default:
                                        sb.append(ch2);
                                        continue;
                                }
                            }
                        } else if (i + 1 >= len || !((ch = value.charAt(i + 1)) == '*' || ch == '?')) {
                            sb.append('~');
                        } else {
                            hasWildCard = true;
                            sb.append('[');
                            sb.append(ch);
                            sb.append(']');
                            i++;
                        }
                    }
                    i++;
                }
                sb.append("\\");
                sb.append(ch2);
                i++;
            }
            if (hasWildCard) {
                return Pattern.compile(sb.toString());
            }
            return null;
        }
    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
        CountUtils.I_MatchPredicate mp = createCriteriaPredicate(arg1, srcRowIndex, srcColumnIndex);
        if (mp == null) {
            return NumberEval.ZERO;
        }
        return new NumberEval(countMatchingCellsInArea(arg0, mp));
    }

    private double countMatchingCellsInArea(ValueEval rangeArg, CountUtils.I_MatchPredicate criteriaPredicate) {
        if (rangeArg instanceof RefEval) {
            return (double) CountUtils.countMatchingCell((RefEval) rangeArg, criteriaPredicate);
        }
        if (rangeArg instanceof TwoDEval) {
            return (double) CountUtils.countMatchingCellsInArea((TwoDEval) rangeArg, criteriaPredicate);
        }
        throw new IllegalArgumentException("Bad range arg type (" + rangeArg.getClass().getName() + ")");
    }

    static CountUtils.I_MatchPredicate createCriteriaPredicate(ValueEval arg, int srcRowIndex, int srcColumnIndex) {
        ValueEval evaluatedCriteriaArg = evaluateCriteriaArg(arg, srcRowIndex, srcColumnIndex);
        if (evaluatedCriteriaArg instanceof NumberEval) {
            return new NumberMatcher(((NumberEval) evaluatedCriteriaArg).getNumberValue(), CmpOp.OP_NONE);
        }
        if (evaluatedCriteriaArg instanceof BoolEval) {
            return new BooleanMatcher(((BoolEval) evaluatedCriteriaArg).getBooleanValue(), CmpOp.OP_NONE);
        }
        if (evaluatedCriteriaArg instanceof StringEval) {
            return createGeneralMatchPredicate((StringEval) evaluatedCriteriaArg);
        }
        if (evaluatedCriteriaArg instanceof ErrorEval) {
            return new ErrorMatcher(((ErrorEval) evaluatedCriteriaArg).getErrorCode(), CmpOp.OP_NONE);
        }
        if (evaluatedCriteriaArg == BlankEval.instance) {
            return null;
        }
        throw new RuntimeException("Unexpected type for criteria (" + evaluatedCriteriaArg.getClass().getName() + ")");
    }

    private static ValueEval evaluateCriteriaArg(ValueEval arg, int srcRowIndex, int srcColumnIndex) {
        try {
            return OperandResolver.getSingleValue(arg, srcRowIndex, (short) srcColumnIndex);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    private static CountUtils.I_MatchPredicate createGeneralMatchPredicate(StringEval stringEval) {
        String value = stringEval.getStringValue();
        CmpOp operator = CmpOp.getOperator(value);
        String value2 = value.substring(operator.getLength());
        Boolean booleanVal = parseBoolean(value2);
        if (booleanVal != null) {
            return new BooleanMatcher(booleanVal.booleanValue(), operator);
        }
        Double doubleVal = OperandResolver.parseDouble(value2);
        if (doubleVal != null) {
            return new NumberMatcher(doubleVal.doubleValue(), operator);
        }
        ErrorEval ee = parseError(value2);
        if (ee != null) {
            return new ErrorMatcher(ee.getErrorCode(), operator);
        }
        return new StringMatcher(value2, operator);
    }

    private static ErrorEval parseError(String value) {
        if (value.length() < 4 || value.charAt(0) != '#') {
            return null;
        }
        if (value.equals("#NULL!")) {
            return ErrorEval.NULL_INTERSECTION;
        }
        if (value.equals("#DIV/0!")) {
            return ErrorEval.DIV_ZERO;
        }
        if (value.equals("#VALUE!")) {
            return ErrorEval.VALUE_INVALID;
        }
        if (value.equals("#REF!")) {
            return ErrorEval.REF_INVALID;
        }
        if (value.equals("#NAME?")) {
            return ErrorEval.NAME_INVALID;
        }
        if (value.equals("#NUM!")) {
            return ErrorEval.NUM_ERROR;
        }
        if (value.equals("#N/A")) {
            return ErrorEval.NA;
        }
        return null;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001c, code lost:
        if (r0 != 't') goto L_0x0035;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static java.lang.Boolean parseBoolean(java.lang.String r3) {
        /*
            int r0 = r3.length()
            r1 = 0
            r2 = 1
            if (r0 >= r2) goto L_0x0009
            return r1
        L_0x0009:
            r0 = 0
            char r0 = r3.charAt(r0)
            r2 = 70
            if (r0 == r2) goto L_0x002a
            r2 = 84
            if (r0 == r2) goto L_0x001f
            r2 = 102(0x66, float:1.43E-43)
            if (r0 == r2) goto L_0x002a
            r2 = 116(0x74, float:1.63E-43)
            if (r0 == r2) goto L_0x001f
            goto L_0x0035
        L_0x001f:
            java.lang.String r0 = "TRUE"
            boolean r0 = r0.equalsIgnoreCase(r3)
            if (r0 == 0) goto L_0x0035
            java.lang.Boolean r0 = java.lang.Boolean.TRUE
            return r0
        L_0x002a:
            java.lang.String r0 = "FALSE"
            boolean r0 = r0.equalsIgnoreCase(r3)
            if (r0 == 0) goto L_0x0035
            java.lang.Boolean r0 = java.lang.Boolean.FALSE
            return r0
        L_0x0035:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.hssf.record.formula.functions.Countif.parseBoolean(java.lang.String):java.lang.Boolean");
    }
}
