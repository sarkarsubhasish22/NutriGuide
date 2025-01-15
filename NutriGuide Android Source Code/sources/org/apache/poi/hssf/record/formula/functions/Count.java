package org.apache.poi.hssf.record.formula.functions;

import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.formula.eval.MissingArgEval;
import org.apache.poi.hssf.record.formula.eval.NumberEval;
import org.apache.poi.hssf.record.formula.eval.ValueEval;
import org.apache.poi.hssf.record.formula.functions.CountUtils;

public final class Count implements Function {
    private static final CountUtils.I_MatchPredicate predicate = new CountUtils.I_MatchPredicate() {
        public boolean matches(ValueEval valueEval) {
            if (!(valueEval instanceof NumberEval) && valueEval != MissingArgEval.instance) {
                return false;
            }
            return true;
        }
    };

    public ValueEval evaluate(ValueEval[] args, int srcCellRow, int srcCellCol) {
        if (nArgs < 1) {
            return ErrorEval.VALUE_INVALID;
        }
        if (nArgs > 30) {
            return ErrorEval.VALUE_INVALID;
        }
        int temp = 0;
        for (ValueEval countArg : args) {
            temp += CountUtils.countArg(countArg, predicate);
        }
        return new NumberEval((double) temp);
    }
}
