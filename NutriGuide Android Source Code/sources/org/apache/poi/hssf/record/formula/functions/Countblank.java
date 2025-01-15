package org.apache.poi.hssf.record.formula.functions;

import org.apache.poi.hssf.record.formula.eval.BlankEval;
import org.apache.poi.hssf.record.formula.eval.NumberEval;
import org.apache.poi.hssf.record.formula.eval.RefEval;
import org.apache.poi.hssf.record.formula.eval.ValueEval;
import org.apache.poi.hssf.record.formula.functions.CountUtils;
import org.apache.poi.ss.formula.TwoDEval;

public final class Countblank extends Fixed1ArgFunction {
    private static final CountUtils.I_MatchPredicate predicate = new CountUtils.I_MatchPredicate() {
        public boolean matches(ValueEval valueEval) {
            return valueEval == BlankEval.instance;
        }
    };

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
        double result;
        if (arg0 instanceof RefEval) {
            result = (double) CountUtils.countMatchingCell((RefEval) arg0, predicate);
        } else if (arg0 instanceof TwoDEval) {
            result = (double) CountUtils.countMatchingCellsInArea((TwoDEval) arg0, predicate);
        } else {
            throw new IllegalArgumentException("Bad range arg type (" + arg0.getClass().getName() + ")");
        }
        return new NumberEval(result);
    }
}
