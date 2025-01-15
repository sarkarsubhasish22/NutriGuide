package org.apache.poi.hssf.record.formula.functions;

import org.apache.poi.hssf.record.formula.eval.BlankEval;
import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.formula.eval.EvaluationException;
import org.apache.poi.hssf.record.formula.eval.MissingArgEval;
import org.apache.poi.hssf.record.formula.eval.OperandResolver;
import org.apache.poi.hssf.record.formula.eval.ValueEval;
import org.apache.poi.ss.formula.OperationEvaluationContext;

public final class Indirect implements FreeRefFunction {
    public static final FreeRefFunction instance = new Indirect();

    private Indirect() {
    }

    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        boolean isA1style;
        if (args.length < 1) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            String text = OperandResolver.coerceValueToString(OperandResolver.getSingleValue(args[0], ec.getRowIndex(), ec.getColumnIndex()));
            try {
                int length = args.length;
                if (length == 1) {
                    isA1style = true;
                } else if (length != 2) {
                    try {
                        return ErrorEval.VALUE_INVALID;
                    } catch (EvaluationException e) {
                        e = e;
                        return e.getErrorEval();
                    }
                } else {
                    isA1style = evaluateBooleanArg(args[1], ec);
                }
                return evaluateIndirect(ec, text, isA1style);
            } catch (EvaluationException e2) {
                e = e2;
                return e.getErrorEval();
            }
        } catch (EvaluationException e3) {
            e = e3;
            return e.getErrorEval();
        }
    }

    private static boolean evaluateBooleanArg(ValueEval arg, OperationEvaluationContext ec) throws EvaluationException {
        ValueEval ve = OperandResolver.getSingleValue(arg, ec.getRowIndex(), ec.getColumnIndex());
        if (ve == BlankEval.instance || ve == MissingArgEval.instance) {
            return false;
        }
        return OperandResolver.coerceValueToBoolean(ve, false).booleanValue();
    }

    private static ValueEval evaluateIndirect(OperationEvaluationContext ec, String text, boolean isA1style) {
        String sheetName;
        String workbookName;
        String workbookName2;
        String refStrPart2;
        String refStrPart1;
        int plingPos = text.lastIndexOf(33);
        if (plingPos < 0) {
            workbookName2 = null;
            workbookName = null;
            sheetName = text;
        } else {
            String[] parts = parseWorkbookAndSheetName(text.subSequence(0, plingPos));
            if (parts == null) {
                return ErrorEval.REF_INVALID;
            }
            String workbookName3 = parts[0];
            String sheetName2 = parts[1];
            workbookName2 = workbookName3;
            workbookName = sheetName2;
            sheetName = text.substring(plingPos + 1);
        }
        int colonPos = sheetName.indexOf(58);
        if (colonPos < 0) {
            refStrPart1 = sheetName.trim();
            refStrPart2 = null;
        } else {
            refStrPart1 = sheetName.substring(0, colonPos).trim();
            refStrPart2 = sheetName.substring(colonPos + 1).trim();
        }
        return ec.getDynamicReference(workbookName2, workbookName, refStrPart1, refStrPart2, isA1style);
    }

    private static String[] parseWorkbookAndSheetName(CharSequence text) {
        String wbName;
        int rbPos;
        int lastIx = text.length() - 1;
        if (lastIx < 0 || canTrim(text)) {
            return null;
        }
        char firstChar = text.charAt(0);
        if (Character.isWhitespace(firstChar)) {
            return null;
        }
        if (firstChar == '\'') {
            if (text.charAt(lastIx) != '\'') {
                return null;
            }
            char firstChar2 = text.charAt(1);
            if (Character.isWhitespace(firstChar2)) {
                return null;
            }
            if (firstChar2 == '[') {
                int rbPos2 = text.toString().lastIndexOf(93);
                if (rbPos2 < 0 || (wbName = unescapeString(text.subSequence(2, rbPos2))) == null || canTrim(wbName)) {
                    return null;
                }
                rbPos = rbPos2 + 1;
            } else {
                wbName = null;
                rbPos = 1;
            }
            String sheetName = unescapeString(text.subSequence(rbPos, lastIx));
            if (sheetName == null) {
                return null;
            }
            return new String[]{wbName, sheetName};
        } else if (firstChar == '[') {
            int rbPos3 = text.toString().lastIndexOf(93);
            if (rbPos3 < 0) {
                return null;
            }
            CharSequence wbName2 = text.subSequence(1, rbPos3);
            if (canTrim(wbName2)) {
                return null;
            }
            CharSequence sheetName2 = text.subSequence(rbPos3 + 1, text.length());
            if (canTrim(sheetName2)) {
                return null;
            }
            return new String[]{wbName2.toString(), sheetName2.toString()};
        } else {
            return new String[]{null, text.toString()};
        }
    }

    private static String unescapeString(CharSequence text) {
        int len = text.length();
        StringBuilder sb = new StringBuilder(len);
        int i = 0;
        while (i < len) {
            char ch = text.charAt(i);
            if (ch == '\'' && ((i = i + 1) >= len || (ch = text.charAt(i)) != '\'')) {
                return null;
            }
            sb.append(ch);
            i++;
        }
        return sb.toString();
    }

    private static boolean canTrim(CharSequence text) {
        int lastIx = text.length() - 1;
        if (lastIx < 0) {
            return false;
        }
        if (!Character.isWhitespace(text.charAt(0)) && !Character.isWhitespace(text.charAt(lastIx))) {
            return false;
        }
        return true;
    }
}
