package org.apache.poi.hssf.record.formula.eval;

import org.apache.poi.hssf.record.formula.functions.Fixed2ArgFunction;
import org.apache.poi.hssf.record.formula.functions.Function;

public final class IntersectionEval extends Fixed2ArgFunction {
    public static final Function instance = new IntersectionEval();

    private IntersectionEval() {
    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
        try {
            AreaEval result = resolveRange(evaluateRef(arg0), evaluateRef(arg1));
            if (result == null) {
                return ErrorEval.NULL_INTERSECTION;
            }
            return result;
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    private static AreaEval resolveRange(AreaEval aeA, AreaEval aeB) {
        int aeBfc;
        int aeBlr;
        int aeBfr;
        int aeAlr;
        int aeAfr = aeA.getFirstRow();
        int aeAfc = aeA.getFirstColumn();
        int aeBlc = aeB.getLastColumn();
        if (aeAfc > aeBlc || (aeBfc = aeB.getFirstColumn()) > aeA.getLastColumn() || aeAfr > (aeBlr = aeB.getLastRow()) || (aeBfr = aeB.getFirstRow()) > (aeAlr = aeA.getLastRow())) {
            return null;
        }
        return aeA.offset(Math.max(aeAfr, aeBfr) - aeAfr, Math.min(aeAlr, aeBlr) - aeAfr, Math.max(aeAfc, aeBfc) - aeAfc, Math.min(aeA.getLastColumn(), aeBlc) - aeAfc);
    }

    private static AreaEval evaluateRef(ValueEval arg) throws EvaluationException {
        if (arg instanceof AreaEval) {
            return (AreaEval) arg;
        }
        if (arg instanceof RefEval) {
            return ((RefEval) arg).offset(0, 0, 0, 0);
        }
        if (arg instanceof ErrorEval) {
            throw new EvaluationException((ErrorEval) arg);
        }
        throw new IllegalArgumentException("Unexpected ref arg class (" + arg.getClass().getName() + ")");
    }
}
