package org.apache.poi.hssf.record.formula.functions;

import org.apache.poi.hssf.record.formula.eval.EvaluationException;
import org.apache.poi.hssf.record.formula.eval.OperandResolver;
import org.apache.poi.hssf.record.formula.eval.ValueEval;
import org.apache.poi.hssf.record.formula.functions.LookupUtils;
import org.apache.poi.ss.formula.TwoDEval;

public final class Lookup extends Var2or3ArgFunction {
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
        throw new RuntimeException("Two arg version of LOOKUP not supported yet");
    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, ValueEval arg2) {
        try {
            ValueEval lookupValue = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
            TwoDEval aeLookupVector = LookupUtils.resolveTableArrayArg(arg1);
            TwoDEval aeResultVector = LookupUtils.resolveTableArrayArg(arg2);
            LookupUtils.ValueVector lookupVector = createVector(aeLookupVector);
            LookupUtils.ValueVector resultVector = createVector(aeResultVector);
            if (lookupVector.getSize() <= resultVector.getSize()) {
                return resultVector.getItem(LookupUtils.lookupIndexOfValue(lookupValue, lookupVector, true));
            }
            throw new RuntimeException("Lookup vector and result vector of differing sizes not supported yet");
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    private static LookupUtils.ValueVector createVector(TwoDEval ae) {
        LookupUtils.ValueVector result = LookupUtils.createVector(ae);
        if (result != null) {
            return result;
        }
        throw new RuntimeException("non-vector lookup or result areas not supported yet");
    }
}
