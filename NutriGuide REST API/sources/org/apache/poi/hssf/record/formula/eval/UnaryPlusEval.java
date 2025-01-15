package org.apache.poi.hssf.record.formula.eval;

import org.apache.poi.hssf.record.formula.functions.Fixed1ArgFunction;
import org.apache.poi.hssf.record.formula.functions.Function;

public final class UnaryPlusEval extends Fixed1ArgFunction {
    public static final Function instance = new UnaryPlusEval();

    private UnaryPlusEval() {
    }

    public ValueEval evaluate(int srcCellRow, int srcCellCol, ValueEval arg0) {
        try {
            ValueEval ve = OperandResolver.getSingleValue(arg0, srcCellRow, srcCellCol);
            if (ve instanceof StringEval) {
                return ve;
            }
            return new NumberEval(OperandResolver.coerceValueToDouble(ve));
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
}
