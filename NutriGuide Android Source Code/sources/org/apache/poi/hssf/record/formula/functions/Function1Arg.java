package org.apache.poi.hssf.record.formula.functions;

import org.apache.poi.hssf.record.formula.eval.ValueEval;

public interface Function1Arg extends Function {
    ValueEval evaluate(int i, int i2, ValueEval valueEval);
}
