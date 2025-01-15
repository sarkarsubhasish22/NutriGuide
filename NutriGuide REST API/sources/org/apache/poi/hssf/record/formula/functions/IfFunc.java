package org.apache.poi.hssf.record.formula.functions;

import org.apache.poi.hssf.record.formula.eval.BlankEval;
import org.apache.poi.hssf.record.formula.eval.BoolEval;
import org.apache.poi.hssf.record.formula.eval.EvaluationException;
import org.apache.poi.hssf.record.formula.eval.MissingArgEval;
import org.apache.poi.hssf.record.formula.eval.OperandResolver;
import org.apache.poi.hssf.record.formula.eval.ValueEval;

public final class IfFunc extends Var2or3ArgFunction {
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
        try {
            if (!evaluateFirstArg(arg0, srcRowIndex, srcColumnIndex)) {
                return BoolEval.FALSE;
            }
            if (arg1 == MissingArgEval.instance) {
                return BlankEval.instance;
            }
            return arg1;
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, ValueEval arg2) {
        try {
            if (evaluateFirstArg(arg0, srcRowIndex, srcColumnIndex)) {
                if (arg1 == MissingArgEval.instance) {
                    return BlankEval.instance;
                }
                return arg1;
            } else if (arg2 == MissingArgEval.instance) {
                return BlankEval.instance;
            } else {
                return arg2;
            }
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    public static boolean evaluateFirstArg(ValueEval arg, int srcCellRow, int srcCellCol) throws EvaluationException {
        Boolean b = OperandResolver.coerceValueToBoolean(OperandResolver.getSingleValue(arg, srcCellRow, srcCellCol), false);
        if (b == null) {
            return false;
        }
        return b.booleanValue();
    }
}
