package org.apache.poi.hssf.record.formula.functions;

import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.formula.eval.NumberEval;
import org.apache.poi.hssf.record.formula.eval.RefEval;
import org.apache.poi.hssf.record.formula.eval.ValueEval;
import org.apache.poi.ss.formula.TwoDEval;

public final class Columns extends Fixed1ArgFunction {
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
        int result;
        if (arg0 instanceof TwoDEval) {
            result = ((TwoDEval) arg0).getWidth();
        } else if ((arg0 instanceof RefEval) == 0) {
            return ErrorEval.VALUE_INVALID;
        } else {
            result = 1;
        }
        return new NumberEval((double) result);
    }
}
