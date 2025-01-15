package org.apache.poi.hssf.record.formula.functions;

import java.util.GregorianCalendar;
import org.apache.poi.hssf.record.formula.eval.NumberEval;
import org.apache.poi.hssf.record.formula.eval.ValueEval;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;

public final class Today extends Fixed0ArgFunction {
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.set(gregorianCalendar.get(1), gregorianCalendar.get(2), gregorianCalendar.get(5), 0, 0, 0);
        gregorianCalendar.set(14, 0);
        return new NumberEval(HSSFDateUtil.getExcelDate(gregorianCalendar.getTime()));
    }
}
