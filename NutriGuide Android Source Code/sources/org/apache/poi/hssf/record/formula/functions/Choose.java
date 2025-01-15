package org.apache.poi.hssf.record.formula.functions;

import org.apache.poi.hssf.record.formula.eval.BlankEval;
import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.formula.eval.EvaluationException;
import org.apache.poi.hssf.record.formula.eval.MissingArgEval;
import org.apache.poi.hssf.record.formula.eval.OperandResolver;
import org.apache.poi.hssf.record.formula.eval.ValueEval;

public final class Choose implements Function {
    public ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        if (args.length < 2) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            int ix = evaluateFirstArg(args[0], srcRowIndex, srcColumnIndex);
            if (ix >= 1) {
                if (ix < args.length) {
                    ValueEval result = OperandResolver.getSingleValue(args[ix], srcRowIndex, srcColumnIndex);
                    if (result == MissingArgEval.instance) {
                        return BlankEval.instance;
                    }
                    return result;
                }
            }
            return ErrorEval.VALUE_INVALID;
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    public static int evaluateFirstArg(ValueEval arg0, int srcRowIndex, int srcColumnIndex) throws EvaluationException {
        return OperandResolver.coerceValueToInt(OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex));
    }
}
