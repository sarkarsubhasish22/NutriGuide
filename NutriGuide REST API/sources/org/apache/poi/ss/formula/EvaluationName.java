package org.apache.poi.ss.formula;

import org.apache.poi.hssf.record.formula.NamePtg;
import org.apache.poi.hssf.record.formula.Ptg;

public interface EvaluationName {
    NamePtg createPtg();

    Ptg[] getNameDefinition();

    String getNameText();

    boolean hasFormula();

    boolean isFunctionName();

    boolean isRange();
}
