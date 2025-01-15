package org.apache.poi.hssf.record.formula.functions;

import org.apache.poi.hssf.record.formula.eval.AreaEval;
import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.formula.eval.NumberEval;
import org.apache.poi.hssf.record.formula.eval.RefEval;
import org.apache.poi.hssf.record.formula.eval.ValueEval;

public final class RowFunc implements Function0Arg, Function1Arg {
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex) {
        return new NumberEval((double) (srcRowIndex + 1));
    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
        int rnum;
        if (arg0 instanceof AreaEval) {
            rnum = ((AreaEval) arg0).getFirstRow();
        } else if ((arg0 instanceof RefEval) == 0) {
            return ErrorEval.VALUE_INVALID;
        } else {
            rnum = ((RefEval) arg0).getRow();
        }
        return new NumberEval((double) (rnum + 1));
    }

    public ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        int length = args.length;
        if (length == 0) {
            return new NumberEval((double) (srcRowIndex + 1));
        }
        if (length != 1) {
            return ErrorEval.VALUE_INVALID;
        }
        return evaluate(srcRowIndex, srcColumnIndex, args[0]);
    }
}
