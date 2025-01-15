package org.apache.poi.hssf.record.formula.functions;

import org.apache.poi.hssf.record.formula.eval.AreaEval;
import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.formula.eval.RefEval;
import org.apache.poi.hssf.record.formula.eval.StringEval;
import org.apache.poi.hssf.record.formula.eval.ValueEval;

public final class T extends Fixed1ArgFunction {
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
        ValueEval arg = arg0;
        if (arg instanceof RefEval) {
            arg = ((RefEval) arg).getInnerValueEval();
        } else if (arg instanceof AreaEval) {
            arg = ((AreaEval) arg).getRelativeValue(0, 0);
        }
        if (!(arg instanceof StringEval) && !(arg instanceof ErrorEval)) {
            return StringEval.EMPTY_INSTANCE;
        }
        return arg;
    }
}
