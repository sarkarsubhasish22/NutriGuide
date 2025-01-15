package org.apache.poi.hssf.record.formula.functions;

import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.formula.eval.ValueEval;

public final class Na extends Fixed0ArgFunction {
    public ValueEval evaluate(int srcCellRow, int srcCellCol) {
        return ErrorEval.NA;
    }
}
