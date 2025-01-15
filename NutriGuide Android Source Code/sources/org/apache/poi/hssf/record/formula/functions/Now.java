package org.apache.poi.hssf.record.formula.functions;

import java.util.Date;
import org.apache.poi.hssf.record.formula.eval.NumberEval;
import org.apache.poi.hssf.record.formula.eval.ValueEval;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;

public final class Now extends Fixed0ArgFunction {
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex) {
        return new NumberEval(HSSFDateUtil.getExcelDate(new Date(System.currentTimeMillis())));
    }
}
