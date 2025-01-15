package org.apache.poi.hssf.record.formula.functions;

import org.apache.poi.hssf.record.formula.eval.ValueEval;
import org.apache.poi.ss.formula.eval.NotImplementedException;

public final class NotImplementedFunction implements Function {
    private final String _functionName;

    protected NotImplementedFunction() {
        this._functionName = getClass().getName();
    }

    public NotImplementedFunction(String name) {
        this._functionName = name;
    }

    public ValueEval evaluate(ValueEval[] operands, int srcRow, int srcCol) {
        throw new NotImplementedException(this._functionName);
    }

    public String getFunctionName() {
        return this._functionName;
    }
}
