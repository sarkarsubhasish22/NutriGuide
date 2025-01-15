package org.apache.poi.hssf.record.formula.atp;

import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.formula.eval.EvaluationException;
import org.apache.poi.hssf.record.formula.eval.NumberEval;
import org.apache.poi.hssf.record.formula.eval.OperandResolver;
import org.apache.poi.hssf.record.formula.eval.ValueEval;
import org.apache.poi.hssf.record.formula.functions.FreeRefFunction;
import org.apache.poi.ss.formula.OperationEvaluationContext;

final class RandBetween implements FreeRefFunction {
    public static final FreeRefFunction instance = new RandBetween();

    private RandBetween() {
    }

    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        if (args.length != 2) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            double bottom = OperandResolver.coerceValueToDouble(OperandResolver.getSingleValue(args[0], ec.getRowIndex(), ec.getColumnIndex()));
            try {
                double top = OperandResolver.coerceValueToDouble(OperandResolver.getSingleValue(args[1], ec.getRowIndex(), ec.getColumnIndex()));
                if (bottom > top) {
                    try {
                        return ErrorEval.NUM_ERROR;
                    } catch (EvaluationException e) {
                        return ErrorEval.VALUE_INVALID;
                    }
                } else {
                    double bottom2 = Math.ceil(bottom);
                    double top2 = Math.floor(top);
                    if (bottom2 > top2) {
                        top2 = bottom2;
                    }
                    double random = (double) ((int) (Math.random() * ((top2 - bottom2) + 1.0d)));
                    Double.isNaN(random);
                    return new NumberEval(random + bottom2);
                }
            } catch (EvaluationException e2) {
                e = e2;
                EvaluationException evaluationException = e;
                return ErrorEval.VALUE_INVALID;
            }
        } catch (EvaluationException e3) {
            e = e3;
            EvaluationException evaluationException2 = e;
            return ErrorEval.VALUE_INVALID;
        }
    }
}
