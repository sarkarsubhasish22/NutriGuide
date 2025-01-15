package org.apache.poi.ss.formula;

import org.apache.poi.hssf.record.formula.NamePtg;
import org.apache.poi.hssf.record.formula.NameXPtg;
import org.apache.poi.ss.formula.EvaluationWorkbook;

public interface FormulaRenderingWorkbook {
    EvaluationWorkbook.ExternalSheet getExternalSheet(int i);

    String getNameText(NamePtg namePtg);

    String getSheetNameByExternSheet(int i);

    String resolveNameXText(NameXPtg nameXPtg);
}
