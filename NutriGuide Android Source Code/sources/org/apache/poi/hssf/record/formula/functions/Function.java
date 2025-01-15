package org.apache.poi.hssf.record.formula.functions;

import org.apache.poi.hssf.record.formula.eval.ValueEval;

public interface Function {
    ValueEval evaluate(ValueEval[] valueEvalArr, int i, int i2);
}
