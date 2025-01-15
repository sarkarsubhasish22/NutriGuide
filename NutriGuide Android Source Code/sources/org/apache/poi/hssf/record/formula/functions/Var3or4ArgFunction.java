package org.apache.poi.hssf.record.formula.functions;

import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.formula.eval.ValueEval;

abstract class Var3or4ArgFunction implements Function3Arg, Function4Arg {
    Var3or4ArgFunction() {
    }

    public final ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        int length = args.length;
        if (length == 3) {
            return evaluate(srcRowIndex, srcColumnIndex, args[0], args[1], args[2]);
        } else if (length != 4) {
            return ErrorEval.VALUE_INVALID;
        } else {
            return evaluate(srcRowIndex, srcColumnIndex, args[0], args[1], args[2], args[3]);
        }
    }
}
