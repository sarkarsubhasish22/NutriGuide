package org.apache.poi.hssf.record.formula.functions;

import org.apache.poi.hssf.record.formula.eval.ValueEval;

public interface Function0Arg extends Function {
    ValueEval evaluate(int i, int i2);
}
