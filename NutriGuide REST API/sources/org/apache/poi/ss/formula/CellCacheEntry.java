package org.apache.poi.ss.formula;

import org.apache.poi.hssf.record.formula.eval.BlankEval;
import org.apache.poi.hssf.record.formula.eval.BoolEval;
import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.formula.eval.NumberEval;
import org.apache.poi.hssf.record.formula.eval.StringEval;
import org.apache.poi.hssf.record.formula.eval.ValueEval;
import org.apache.poi.ss.formula.IEvaluationListener;

abstract class CellCacheEntry implements IEvaluationListener.ICacheEntry {
    public static final CellCacheEntry[] EMPTY_ARRAY = new CellCacheEntry[0];
    private final FormulaCellCacheEntrySet _consumingCells = new FormulaCellCacheEntrySet();
    private ValueEval _value;

    protected CellCacheEntry() {
    }

    /* access modifiers changed from: protected */
    public final void clearValue() {
        this._value = null;
    }

    public final boolean updateValue(ValueEval value) {
        if (value != null) {
            boolean result = !areValuesEqual(this._value, value);
            this._value = value;
            return result;
        }
        throw new IllegalArgumentException("Did not expect to update to null");
    }

    public final ValueEval getValue() {
        return this._value;
    }

    private static boolean areValuesEqual(ValueEval a, ValueEval b) {
        Class<?> cls;
        if (a == null || (cls = a.getClass()) != b.getClass()) {
            return false;
        }
        if (a == BlankEval.instance) {
            if (b == a) {
                return true;
            }
            return false;
        } else if (cls == NumberEval.class) {
            if (((NumberEval) a).getNumberValue() == ((NumberEval) b).getNumberValue()) {
                return true;
            }
            return false;
        } else if (cls == StringEval.class) {
            return ((StringEval) a).getStringValue().equals(((StringEval) b).getStringValue());
        } else {
            if (cls == BoolEval.class) {
                if (((BoolEval) a).getBooleanValue() == ((BoolEval) b).getBooleanValue()) {
                    return true;
                }
                return false;
            } else if (cls != ErrorEval.class) {
                throw new IllegalStateException("Unexpected value class (" + cls.getName() + ")");
            } else if (((ErrorEval) a).getErrorCode() == ((ErrorEval) b).getErrorCode()) {
                return true;
            } else {
                return false;
            }
        }
    }

    public final void addConsumingCell(FormulaCellCacheEntry cellLoc) {
        this._consumingCells.add(cellLoc);
    }

    public final FormulaCellCacheEntry[] getConsumingCells() {
        return this._consumingCells.toArray();
    }

    public final void clearConsumingCell(FormulaCellCacheEntry cce) {
        if (!this._consumingCells.remove(cce)) {
            throw new IllegalStateException("Specified formula cell is not consumed by this cell");
        }
    }

    public final void recurseClearCachedFormulaResults(IEvaluationListener listener) {
        if (listener == null) {
            recurseClearCachedFormulaResults();
            return;
        }
        listener.onClearCachedValue(this);
        recurseClearCachedFormulaResults(listener, 1);
    }

    /* access modifiers changed from: protected */
    public final void recurseClearCachedFormulaResults() {
        FormulaCellCacheEntry[] formulaCells = getConsumingCells();
        for (FormulaCellCacheEntry fc : formulaCells) {
            fc.clearFormulaEntry();
            fc.recurseClearCachedFormulaResults();
        }
    }

    /* access modifiers changed from: protected */
    public final void recurseClearCachedFormulaResults(IEvaluationListener listener, int depth) {
        FormulaCellCacheEntry[] formulaCells = getConsumingCells();
        listener.sortDependentCachedValues(formulaCells);
        for (FormulaCellCacheEntry fc : formulaCells) {
            listener.onClearDependentCachedValue(fc, depth);
            fc.clearFormulaEntry();
            fc.recurseClearCachedFormulaResults(listener, depth + 1);
        }
    }
}
