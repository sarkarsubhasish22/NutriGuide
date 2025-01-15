package org.apache.poi.hssf.record.formula.functions;

import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.formula.eval.EvaluationException;
import org.apache.poi.hssf.record.formula.eval.NumberEval;
import org.apache.poi.hssf.record.formula.eval.OperandResolver;
import org.apache.poi.hssf.record.formula.eval.ValueEval;

public final class Errortype extends Fixed1ArgFunction {
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
        try {
            OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
            return ErrorEval.NA;
        } catch (EvaluationException e) {
            return new NumberEval((double) translateErrorCodeToErrorTypeValue(e.getErrorEval().getErrorCode()));
        }
    }

    private int translateErrorCodeToErrorTypeValue(int errorCode) {
        if (errorCode == 0) {
            return 1;
        }
        if (errorCode == 7) {
            return 2;
        }
        if (errorCode == 15) {
            return 3;
        }
        if (errorCode == 23) {
            return 4;
        }
        if (errorCode == 29) {
            return 5;
        }
        if (errorCode == 36) {
            return 6;
        }
        if (errorCode == 42) {
            return 7;
        }
        throw new IllegalArgumentException("Invalid error code (" + errorCode + ")");
    }
}
