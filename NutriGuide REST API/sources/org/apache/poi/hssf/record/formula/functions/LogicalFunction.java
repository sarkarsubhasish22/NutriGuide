package org.apache.poi.hssf.record.formula.functions;

import org.apache.poi.hssf.record.formula.eval.AreaEval;
import org.apache.poi.hssf.record.formula.eval.BlankEval;
import org.apache.poi.hssf.record.formula.eval.BoolEval;
import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.formula.eval.EvaluationException;
import org.apache.poi.hssf.record.formula.eval.NumberEval;
import org.apache.poi.hssf.record.formula.eval.OperandResolver;
import org.apache.poi.hssf.record.formula.eval.RefEval;
import org.apache.poi.hssf.record.formula.eval.StringEval;
import org.apache.poi.hssf.record.formula.eval.ValueEval;

public abstract class LogicalFunction extends Fixed1ArgFunction {
    public static final Function ISBLANK = new LogicalFunction() {
        /* access modifiers changed from: protected */
        public boolean evaluate(ValueEval arg) {
            return arg instanceof BlankEval;
        }
    };
    public static final Function ISERROR = new LogicalFunction() {
        /* access modifiers changed from: protected */
        public boolean evaluate(ValueEval arg) {
            return arg instanceof ErrorEval;
        }
    };
    public static final Function ISLOGICAL = new LogicalFunction() {
        /* access modifiers changed from: protected */
        public boolean evaluate(ValueEval arg) {
            return arg instanceof BoolEval;
        }
    };
    public static final Function ISNA = new LogicalFunction() {
        /* access modifiers changed from: protected */
        public boolean evaluate(ValueEval arg) {
            return arg == ErrorEval.NA;
        }
    };
    public static final Function ISNONTEXT = new LogicalFunction() {
        /* access modifiers changed from: protected */
        public boolean evaluate(ValueEval arg) {
            return !(arg instanceof StringEval);
        }
    };
    public static final Function ISNUMBER = new LogicalFunction() {
        /* access modifiers changed from: protected */
        public boolean evaluate(ValueEval arg) {
            return arg instanceof NumberEval;
        }
    };
    public static final Function ISREF = new Fixed1ArgFunction() {
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
            if ((arg0 instanceof RefEval) || (arg0 instanceof AreaEval)) {
                return BoolEval.TRUE;
            }
            return BoolEval.FALSE;
        }
    };
    public static final Function ISTEXT = new LogicalFunction() {
        /* access modifiers changed from: protected */
        public boolean evaluate(ValueEval arg) {
            return arg instanceof StringEval;
        }
    };

    /* access modifiers changed from: protected */
    public abstract boolean evaluate(ValueEval valueEval);

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
        ValueEval ve;
        try {
            ve = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
        } catch (EvaluationException e) {
            ve = e.getErrorEval();
        }
        return BoolEval.valueOf(evaluate(ve));
    }
}
