package org.apache.poi.hssf.record.formula.eval;

public final class NameEval implements ValueEval {
    private final String _functionName;

    public NameEval(String functionName) {
        this._functionName = functionName;
    }

    public String getFunctionName() {
        return this._functionName;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(64);
        sb.append(getClass().getName());
        sb.append(" [");
        sb.append(this._functionName);
        sb.append("]");
        return sb.toString();
    }
}
