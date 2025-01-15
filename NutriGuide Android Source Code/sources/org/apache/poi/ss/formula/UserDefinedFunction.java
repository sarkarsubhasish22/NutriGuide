package org.apache.poi.ss.formula;

import org.apache.poi.hssf.record.formula.eval.NameEval;
import org.apache.poi.hssf.record.formula.eval.NameXEval;
import org.apache.poi.hssf.record.formula.eval.ValueEval;
import org.apache.poi.hssf.record.formula.functions.FreeRefFunction;
import org.apache.poi.ss.formula.eval.NotImplementedException;

final class UserDefinedFunction implements FreeRefFunction {
    public static final FreeRefFunction instance = new UserDefinedFunction();

    private UserDefinedFunction() {
    }

    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        String functionName;
        int nIncomingArgs = args.length;
        if (nIncomingArgs >= 1) {
            NameEval nameEval = args[0];
            if (nameEval instanceof NameEval) {
                functionName = nameEval.getFunctionName();
            } else if (nameEval instanceof NameXEval) {
                functionName = ec.getWorkbook().resolveNameXText(nameEval.getPtg());
            } else {
                throw new RuntimeException("First argument should be a NameEval, but got (" + nameEval.getClass().getName() + ")");
            }
            FreeRefFunction targetFunc = ec.findUserDefinedFunction(functionName);
            if (targetFunc != null) {
                int nOutGoingArgs = nIncomingArgs - 1;
                ValueEval[] outGoingArgs = new ValueEval[nOutGoingArgs];
                System.arraycopy(args, 1, outGoingArgs, 0, nOutGoingArgs);
                return targetFunc.evaluate(outGoingArgs, ec);
            }
            throw new NotImplementedException(functionName);
        }
        throw new RuntimeException("function name argument missing");
    }
}
