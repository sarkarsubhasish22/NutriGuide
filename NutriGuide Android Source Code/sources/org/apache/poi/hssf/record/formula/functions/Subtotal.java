package org.apache.poi.hssf.record.formula.functions;

import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.formula.eval.EvaluationException;
import org.apache.poi.hssf.record.formula.eval.OperandResolver;
import org.apache.poi.hssf.record.formula.eval.ValueEval;
import org.apache.poi.ss.formula.eval.NotImplementedException;

public class Subtotal implements Function {
    private static Function findFunction(int functionCode) throws EvaluationException {
        switch (functionCode) {
            case 1:
                return AggregateFunction.AVERAGE;
            case 2:
                return new Count();
            case 3:
                return new Counta();
            case 4:
                return AggregateFunction.MAX;
            case 5:
                return AggregateFunction.MIN;
            case 6:
                return AggregateFunction.PRODUCT;
            case 7:
                return AggregateFunction.STDEV;
            case 8:
                throw new NotImplementedException("STDEVP");
            case 9:
                return AggregateFunction.SUM;
            case 10:
                throw new NotImplementedException("VAR");
            case 11:
                throw new NotImplementedException("VARP");
            default:
                if (functionCode <= 100 || functionCode >= 112) {
                    throw EvaluationException.invalidValue();
                }
                throw new NotImplementedException("SUBTOTAL - with 'exclude hidden values' option");
        }
    }

    public ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        int nInnerArgs = args.length - 1;
        if (nInnerArgs < 1) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            Function innerFunc = findFunction(OperandResolver.coerceValueToInt(OperandResolver.getSingleValue(args[0], srcRowIndex, srcColumnIndex)));
            ValueEval[] innerArgs = new ValueEval[nInnerArgs];
            System.arraycopy(args, 1, innerArgs, 0, nInnerArgs);
            return innerFunc.evaluate(innerArgs, srcRowIndex, srcColumnIndex);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
}
