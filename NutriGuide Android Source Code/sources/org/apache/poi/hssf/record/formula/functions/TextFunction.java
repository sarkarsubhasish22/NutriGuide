package org.apache.poi.hssf.record.formula.functions;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import org.apache.poi.hssf.record.formula.eval.BoolEval;
import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.formula.eval.EvaluationException;
import org.apache.poi.hssf.record.formula.eval.NumberEval;
import org.apache.poi.hssf.record.formula.eval.OperandResolver;
import org.apache.poi.hssf.record.formula.eval.StringEval;
import org.apache.poi.hssf.record.formula.eval.ValueEval;

public abstract class TextFunction implements Function {
    public static final Function CONCATENATE = new Function() {
        public ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            int iSize = args.length;
            while (i < iSize) {
                try {
                    sb.append(TextFunction.evaluateStringArg(args[i], srcRowIndex, srcColumnIndex));
                    i++;
                } catch (EvaluationException e) {
                    return e.getErrorEval();
                }
            }
            return new StringEval(sb.toString());
        }
    };
    protected static final String EMPTY_STRING = "";
    public static final Function EXACT = new Fixed2ArgFunction() {
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
            try {
                try {
                    return BoolEval.valueOf(TextFunction.evaluateStringArg(arg0, srcRowIndex, srcColumnIndex).equals(TextFunction.evaluateStringArg(arg1, srcRowIndex, srcColumnIndex)));
                } catch (EvaluationException e) {
                    e = e;
                    return e.getErrorEval();
                }
            } catch (EvaluationException e2) {
                e = e2;
                return e.getErrorEval();
            }
        }
    };
    public static final Function FIND = new SearchFind(true);
    public static final Function LEFT = new LeftRight(true);
    public static final Function LEN = new SingleArgTextFunc() {
        /* access modifiers changed from: protected */
        public ValueEval evaluate(String arg) {
            return new NumberEval((double) arg.length());
        }
    };
    public static final Function LOWER = new SingleArgTextFunc() {
        /* access modifiers changed from: protected */
        public ValueEval evaluate(String arg) {
            return new StringEval(arg.toLowerCase());
        }
    };
    public static final Function MID = new Fixed3ArgFunction() {
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, ValueEval arg2) {
            int startCharNum;
            try {
                String text = TextFunction.evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
                try {
                    startCharNum = TextFunction.evaluateIntArg(arg1, srcRowIndex, srcColumnIndex);
                } catch (EvaluationException e) {
                    e = e;
                    return e.getErrorEval();
                }
                try {
                    int numChars = TextFunction.evaluateIntArg(arg2, srcRowIndex, srcColumnIndex);
                    int startIx = startCharNum - 1;
                    if (startIx < 0) {
                        return ErrorEval.VALUE_INVALID;
                    }
                    if (numChars < 0) {
                        return ErrorEval.VALUE_INVALID;
                    }
                    int len = text.length();
                    if (numChars < 0 || startIx > len) {
                        return new StringEval("");
                    }
                    return new StringEval(text.substring(startIx, Math.min(startIx + numChars, len)));
                } catch (EvaluationException e2) {
                    e = e2;
                    return e.getErrorEval();
                }
            } catch (EvaluationException e3) {
                e = e3;
                return e.getErrorEval();
            }
        }
    };
    public static final Function RIGHT = new LeftRight(false);
    public static final Function SEARCH = new SearchFind(false);
    public static final Function TEXT = new Fixed2ArgFunction() {
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
            String[] fractParts;
            int i = srcRowIndex;
            int i2 = srcColumnIndex;
            try {
                double s0 = TextFunction.evaluateDoubleArg(arg0, i, i2);
                try {
                    String s1 = TextFunction.evaluateStringArg(arg1, i, i2);
                    if (s1.matches("[\\d,\\#,\\.,\\$,\\,]+")) {
                        return new StringEval(new DecimalFormat(s1).format(s0));
                    }
                    if (s1.indexOf("/") != s1.lastIndexOf("/") || s1.indexOf("/") < 0 || s1.contains("-")) {
                        double s02 = s0;
                        try {
                            DateFormat dateFormatter = new SimpleDateFormat(s1);
                            GregorianCalendar gregorianCalendar = new GregorianCalendar(1899, 11, 30, 0, 0, 0);
                            gregorianCalendar.add(5, (int) Math.floor(s02));
                            gregorianCalendar.add(14, (int) Math.round(24.0d * (s02 - Math.floor(s02)) * 60.0d * 60.0d * 1000.0d));
                            return new StringEval(dateFormatter.format(gregorianCalendar.getTime()));
                        } catch (Exception e) {
                            return ErrorEval.VALUE_INVALID;
                        }
                    } else {
                        double wholePart = Math.floor(s0);
                        double decPart = s0 - wholePart;
                        if (wholePart * decPart == 0.0d) {
                            return new StringEval("0");
                        }
                        String[] parts = s1.split(" ");
                        if (parts.length == 2) {
                            fractParts = parts[1].split("/");
                        } else {
                            fractParts = s1.split("/");
                        }
                        if (fractParts.length != 2) {
                            return ErrorEval.VALUE_INVALID;
                        }
                        double minVal = 1.0d;
                        double d = s0;
                        double d2 = 10.0d;
                        double currDenom = Math.pow(10.0d, (double) fractParts[1].length()) - 1.0d;
                        int i3 = (int) (Math.pow(10.0d, (double) fractParts[1].length()) - 1.0d);
                        double currNeum = 0.0d;
                        double currNeum2 = currDenom;
                        while (i3 > 0) {
                            for (int i22 = (int) (Math.pow(d2, (double) fractParts[1].length()) - 1.0d); i22 > 0; i22--) {
                                double d3 = (double) i22;
                                double d4 = (double) i3;
                                Double.isNaN(d3);
                                Double.isNaN(d4);
                                if (minVal >= Math.abs((d3 / d4) - decPart)) {
                                    double currDenom2 = (double) i22;
                                    double currNeum3 = (double) i3;
                                    Double.isNaN(currDenom2);
                                    Double.isNaN(currNeum3);
                                    minVal = Math.abs((currDenom2 / currNeum3) - decPart);
                                    currNeum = (double) i22;
                                    currNeum2 = (double) i3;
                                }
                            }
                            i3--;
                            ValueEval valueEval = arg1;
                            d2 = 10.0d;
                        }
                        NumberFormat neumFormatter = new DecimalFormat(fractParts[0]);
                        NumberFormat denomFormatter = new DecimalFormat(fractParts[1]);
                        if (parts.length == 2) {
                            DecimalFormat decimalFormat = new DecimalFormat(parts[0]);
                            StringBuilder sb = new StringBuilder();
                            sb.append(decimalFormat.format(wholePart));
                            sb.append(" ");
                            sb.append(neumFormatter.format(currNeum));
                            sb.append("/");
                            DecimalFormat decimalFormat2 = decimalFormat;
                            sb.append(denomFormatter.format(currNeum2));
                            return new StringEval(sb.toString());
                        }
                        double currDenom3 = currNeum2;
                        StringBuilder sb2 = new StringBuilder();
                        double d5 = wholePart;
                        sb2.append(neumFormatter.format(currNeum + (currDenom3 * wholePart)));
                        sb2.append("/");
                        sb2.append(denomFormatter.format(currDenom3));
                        return new StringEval(sb2.toString());
                    }
                } catch (EvaluationException e2) {
                    e = e2;
                    double d6 = s0;
                    return e.getErrorEval();
                }
            } catch (EvaluationException e3) {
                e = e3;
                return e.getErrorEval();
            }
        }
    };
    public static final Function TRIM = new SingleArgTextFunc() {
        /* access modifiers changed from: protected */
        public ValueEval evaluate(String arg) {
            return new StringEval(arg.trim());
        }
    };
    public static final Function UPPER = new SingleArgTextFunc() {
        /* access modifiers changed from: protected */
        public ValueEval evaluate(String arg) {
            return new StringEval(arg.toUpperCase());
        }
    };

    /* access modifiers changed from: protected */
    public abstract ValueEval evaluateFunc(ValueEval[] valueEvalArr, int i, int i2) throws EvaluationException;

    protected static final String evaluateStringArg(ValueEval eval, int srcRow, int srcCol) throws EvaluationException {
        return OperandResolver.coerceValueToString(OperandResolver.getSingleValue(eval, srcRow, srcCol));
    }

    protected static final int evaluateIntArg(ValueEval arg, int srcCellRow, int srcCellCol) throws EvaluationException {
        return OperandResolver.coerceValueToInt(OperandResolver.getSingleValue(arg, srcCellRow, srcCellCol));
    }

    protected static final double evaluateDoubleArg(ValueEval arg, int srcCellRow, int srcCellCol) throws EvaluationException {
        return OperandResolver.coerceValueToDouble(OperandResolver.getSingleValue(arg, srcCellRow, srcCellCol));
    }

    public final ValueEval evaluate(ValueEval[] args, int srcCellRow, int srcCellCol) {
        try {
            return evaluateFunc(args, srcCellRow, srcCellCol);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    private static abstract class SingleArgTextFunc extends Fixed1ArgFunction {
        /* access modifiers changed from: protected */
        public abstract ValueEval evaluate(String str);

        protected SingleArgTextFunc() {
        }

        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
            try {
                return evaluate(TextFunction.evaluateStringArg(arg0, srcRowIndex, srcColumnIndex));
            } catch (EvaluationException e) {
                return e.getErrorEval();
            }
        }
    }

    private static final class LeftRight extends Var1or2ArgFunction {
        private static final ValueEval DEFAULT_ARG1 = new NumberEval(1.0d);
        private final boolean _isLeft;

        protected LeftRight(boolean isLeft) {
            this._isLeft = isLeft;
        }

        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
            return evaluate(srcRowIndex, srcColumnIndex, arg0, DEFAULT_ARG1);
        }

        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
            String result;
            try {
                String arg = TextFunction.evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
                try {
                    int index = TextFunction.evaluateIntArg(arg1, srcRowIndex, srcColumnIndex);
                    if (index < 0) {
                        return ErrorEval.VALUE_INVALID;
                    }
                    if (this._isLeft) {
                        result = arg.substring(0, Math.min(arg.length(), index));
                    } else {
                        result = arg.substring(Math.max(0, arg.length() - index));
                    }
                    return new StringEval(result);
                } catch (EvaluationException e) {
                    e = e;
                    return e.getErrorEval();
                }
            } catch (EvaluationException e2) {
                e = e2;
                return e.getErrorEval();
            }
        }
    }

    private static final class SearchFind extends Var2or3ArgFunction {
        private final boolean _isCaseSensitive;

        public SearchFind(boolean isCaseSensitive) {
            this._isCaseSensitive = isCaseSensitive;
        }

        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
            try {
                return eval(TextFunction.evaluateStringArg(arg1, srcRowIndex, srcColumnIndex), TextFunction.evaluateStringArg(arg0, srcRowIndex, srcColumnIndex), 0);
            } catch (EvaluationException e) {
                return e.getErrorEval();
            }
        }

        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, ValueEval arg2) {
            try {
                String needle = TextFunction.evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
                String haystack = TextFunction.evaluateStringArg(arg1, srcRowIndex, srcColumnIndex);
                int startpos = TextFunction.evaluateIntArg(arg2, srcRowIndex, srcColumnIndex) - 1;
                if (startpos < 0) {
                    return ErrorEval.VALUE_INVALID;
                }
                return eval(haystack, needle, startpos);
            } catch (EvaluationException e) {
                return e.getErrorEval();
            }
        }

        private ValueEval eval(String haystack, String needle, int startIndex) {
            int result;
            if (this._isCaseSensitive) {
                result = haystack.indexOf(needle, startIndex);
            } else {
                result = haystack.toUpperCase().indexOf(needle.toUpperCase(), startIndex);
            }
            if (result == -1) {
                return ErrorEval.VALUE_INVALID;
            }
            return new NumberEval((double) (result + 1));
        }
    }
}
