package org.apache.poi.hssf.record.formula.udf;

import org.apache.poi.hssf.record.formula.atp.AnalysisToolPak;
import org.apache.poi.hssf.record.formula.functions.FreeRefFunction;

public interface UDFFinder {
    public static final UDFFinder DEFAULT = new AggregatingUDFFinder(AnalysisToolPak.instance);

    FreeRefFunction findFunction(String str);
}
