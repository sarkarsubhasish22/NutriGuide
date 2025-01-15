package org.apache.poi.hssf.record.formula.eval;

public final class MissingArgEval implements ValueEval {
    public static final MissingArgEval instance = new MissingArgEval();

    private MissingArgEval() {
    }
}
