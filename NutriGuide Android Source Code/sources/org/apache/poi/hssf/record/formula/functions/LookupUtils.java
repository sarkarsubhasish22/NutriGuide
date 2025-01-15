package org.apache.poi.hssf.record.formula.functions;

import org.apache.poi.hssf.record.formula.eval.BlankEval;
import org.apache.poi.hssf.record.formula.eval.BoolEval;
import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.formula.eval.EvaluationException;
import org.apache.poi.hssf.record.formula.eval.NumberEval;
import org.apache.poi.hssf.record.formula.eval.NumericValueEval;
import org.apache.poi.hssf.record.formula.eval.OperandResolver;
import org.apache.poi.hssf.record.formula.eval.RefEval;
import org.apache.poi.hssf.record.formula.eval.StringEval;
import org.apache.poi.hssf.record.formula.eval.ValueEval;
import org.apache.poi.ss.formula.TwoDEval;

final class LookupUtils {

    public interface LookupValueComparer {
        CompareResult compareTo(ValueEval valueEval);
    }

    public interface ValueVector {
        ValueEval getItem(int i);

        int getSize();
    }

    LookupUtils() {
    }

    private static final class RowVector implements ValueVector {
        private final int _rowIndex;
        private final int _size;
        private final TwoDEval _tableArray;

        public RowVector(TwoDEval tableArray, int rowIndex) {
            this._rowIndex = rowIndex;
            int lastRowIx = tableArray.getHeight() - 1;
            if (rowIndex < 0 || rowIndex > lastRowIx) {
                throw new IllegalArgumentException("Specified row index (" + rowIndex + ") is outside the allowed range (0.." + lastRowIx + ")");
            }
            this._tableArray = tableArray;
            this._size = tableArray.getWidth();
        }

        public ValueEval getItem(int index) {
            if (index <= this._size) {
                return this._tableArray.getValue(this._rowIndex, index);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Specified index (");
            sb.append(index);
            sb.append(") is outside the allowed range (0..");
            sb.append(this._size - 1);
            sb.append(")");
            throw new ArrayIndexOutOfBoundsException(sb.toString());
        }

        public int getSize() {
            return this._size;
        }
    }

    private static final class ColumnVector implements ValueVector {
        private final int _columnIndex;
        private final int _size;
        private final TwoDEval _tableArray;

        public ColumnVector(TwoDEval tableArray, int columnIndex) {
            this._columnIndex = columnIndex;
            int lastColIx = tableArray.getWidth() - 1;
            if (columnIndex < 0 || columnIndex > lastColIx) {
                throw new IllegalArgumentException("Specified column index (" + columnIndex + ") is outside the allowed range (0.." + lastColIx + ")");
            }
            this._tableArray = tableArray;
            this._size = tableArray.getHeight();
        }

        public ValueEval getItem(int index) {
            if (index <= this._size) {
                return this._tableArray.getValue(index, this._columnIndex);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Specified index (");
            sb.append(index);
            sb.append(") is outside the allowed range (0..");
            sb.append(this._size - 1);
            sb.append(")");
            throw new ArrayIndexOutOfBoundsException(sb.toString());
        }

        public int getSize() {
            return this._size;
        }
    }

    public static ValueVector createRowVector(TwoDEval tableArray, int relativeRowIndex) {
        return new RowVector(tableArray, relativeRowIndex);
    }

    public static ValueVector createColumnVector(TwoDEval tableArray, int relativeColumnIndex) {
        return new ColumnVector(tableArray, relativeColumnIndex);
    }

    public static ValueVector createVector(TwoDEval ae) {
        if (ae.isColumn()) {
            return createColumnVector(ae, 0);
        }
        if (ae.isRow()) {
            return createRowVector(ae, 0);
        }
        return null;
    }

    public static final class CompareResult {
        public static final CompareResult EQUAL = new CompareResult(false, 0);
        public static final CompareResult GREATER_THAN = new CompareResult(false, 1);
        public static final CompareResult LESS_THAN = new CompareResult(false, -1);
        public static final CompareResult TYPE_MISMATCH = new CompareResult(true, 0);
        private final boolean _isEqual;
        private final boolean _isGreaterThan;
        private final boolean _isLessThan;
        private final boolean _isTypeMismatch;

        private CompareResult(boolean isTypeMismatch, int simpleCompareResult) {
            boolean z = true;
            if (isTypeMismatch) {
                this._isTypeMismatch = true;
                this._isLessThan = false;
                this._isEqual = false;
                this._isGreaterThan = false;
                return;
            }
            this._isTypeMismatch = false;
            this._isLessThan = simpleCompareResult < 0;
            this._isEqual = simpleCompareResult == 0;
            this._isGreaterThan = simpleCompareResult <= 0 ? false : z;
        }

        public static final CompareResult valueOf(int simpleCompareResult) {
            if (simpleCompareResult < 0) {
                return LESS_THAN;
            }
            if (simpleCompareResult > 0) {
                return GREATER_THAN;
            }
            return EQUAL;
        }

        public boolean isTypeMismatch() {
            return this._isTypeMismatch;
        }

        public boolean isLessThan() {
            return this._isLessThan;
        }

        public boolean isEqual() {
            return this._isEqual;
        }

        public boolean isGreaterThan() {
            return this._isGreaterThan;
        }

        public String toString() {
            StringBuffer sb = new StringBuffer(64);
            sb.append(getClass().getName());
            sb.append(" [");
            sb.append(formatAsString());
            sb.append("]");
            return sb.toString();
        }

        private String formatAsString() {
            if (this._isTypeMismatch) {
                return "TYPE_MISMATCH";
            }
            if (this._isLessThan) {
                return "LESS_THAN";
            }
            if (this._isEqual) {
                return "EQUAL";
            }
            if (this._isGreaterThan) {
                return "GREATER_THAN";
            }
            return "??error??";
        }
    }

    private static abstract class LookupValueComparerBase implements LookupValueComparer {
        private final Class<? extends ValueEval> _targetClass;

        /* access modifiers changed from: protected */
        public abstract CompareResult compareSameType(ValueEval valueEval);

        /* access modifiers changed from: protected */
        public abstract String getValueAsString();

        protected LookupValueComparerBase(ValueEval targetValue) {
            if (targetValue != null) {
                this._targetClass = targetValue.getClass();
                return;
            }
            throw new RuntimeException("targetValue cannot be null");
        }

        public final CompareResult compareTo(ValueEval other) {
            if (other == null) {
                throw new RuntimeException("compare to value cannot be null");
            } else if (this._targetClass != other.getClass()) {
                return CompareResult.TYPE_MISMATCH;
            } else {
                return compareSameType(other);
            }
        }

        public String toString() {
            StringBuffer sb = new StringBuffer(64);
            sb.append(getClass().getName());
            sb.append(" [");
            sb.append(getValueAsString());
            sb.append("]");
            return sb.toString();
        }
    }

    private static final class StringLookupComparer extends LookupValueComparerBase {
        private String _value;

        protected StringLookupComparer(StringEval se) {
            super(se);
            this._value = se.getStringValue();
        }

        /* access modifiers changed from: protected */
        public CompareResult compareSameType(ValueEval other) {
            return CompareResult.valueOf(this._value.compareToIgnoreCase(((StringEval) other).getStringValue()));
        }

        /* access modifiers changed from: protected */
        public String getValueAsString() {
            return this._value;
        }
    }

    private static final class NumberLookupComparer extends LookupValueComparerBase {
        private double _value;

        protected NumberLookupComparer(NumberEval ne) {
            super(ne);
            this._value = ne.getNumberValue();
        }

        /* access modifiers changed from: protected */
        public CompareResult compareSameType(ValueEval other) {
            return CompareResult.valueOf(Double.compare(this._value, ((NumberEval) other).getNumberValue()));
        }

        /* access modifiers changed from: protected */
        public String getValueAsString() {
            return String.valueOf(this._value);
        }
    }

    private static final class BooleanLookupComparer extends LookupValueComparerBase {
        private boolean _value;

        protected BooleanLookupComparer(BoolEval be) {
            super(be);
            this._value = be.getBooleanValue();
        }

        /* access modifiers changed from: protected */
        public CompareResult compareSameType(ValueEval other) {
            boolean otherVal = ((BoolEval) other).getBooleanValue();
            boolean z = this._value;
            if (z == otherVal) {
                return CompareResult.EQUAL;
            }
            if (z) {
                return CompareResult.GREATER_THAN;
            }
            return CompareResult.LESS_THAN;
        }

        /* access modifiers changed from: protected */
        public String getValueAsString() {
            return String.valueOf(this._value);
        }
    }

    public static int resolveRowOrColIndexArg(ValueEval rowColIndexArg, int srcCellRow, int srcCellCol) throws EvaluationException {
        if (rowColIndexArg != null) {
            try {
                ValueEval veRowColIndexArg = OperandResolver.getSingleValue(rowColIndexArg, srcCellRow, (short) srcCellCol);
                if (!(veRowColIndexArg instanceof StringEval) || OperandResolver.parseDouble(((StringEval) veRowColIndexArg).getStringValue()) != null) {
                    int oneBasedIndex = OperandResolver.coerceValueToInt(veRowColIndexArg);
                    if (oneBasedIndex >= 1) {
                        return oneBasedIndex - 1;
                    }
                    throw EvaluationException.invalidValue();
                }
                throw EvaluationException.invalidRef();
            } catch (EvaluationException e) {
                throw EvaluationException.invalidRef();
            }
        } else {
            throw new IllegalArgumentException("argument must not be null");
        }
    }

    public static TwoDEval resolveTableArrayArg(ValueEval eval) throws EvaluationException {
        if (eval instanceof TwoDEval) {
            return (TwoDEval) eval;
        }
        if (eval instanceof RefEval) {
            return ((RefEval) eval).offset(0, 0, 0, 0);
        }
        throw EvaluationException.invalidValue();
    }

    public static boolean resolveRangeLookupArg(ValueEval rangeLookupArg, int srcCellRow, int srcCellCol) throws EvaluationException {
        ValueEval valEval = OperandResolver.getSingleValue(rangeLookupArg, srcCellRow, srcCellCol);
        if (valEval instanceof BlankEval) {
            return false;
        }
        if (valEval instanceof BoolEval) {
            return ((BoolEval) valEval).getBooleanValue();
        }
        if (valEval instanceof StringEval) {
            String stringValue = ((StringEval) valEval).getStringValue();
            if (stringValue.length() >= 1) {
                Boolean b = Countif.parseBoolean(stringValue);
                if (b != null) {
                    return b.booleanValue();
                }
                throw EvaluationException.invalidValue();
            }
            throw EvaluationException.invalidValue();
        } else if (!(valEval instanceof NumericValueEval)) {
            throw new RuntimeException("Unexpected eval type (" + valEval.getClass().getName() + ")");
        } else if (0.0d != ((NumericValueEval) valEval).getNumberValue()) {
            return true;
        } else {
            return false;
        }
    }

    public static int lookupIndexOfValue(ValueEval lookupValue, ValueVector vector, boolean isRangeLookup) throws EvaluationException {
        int result;
        LookupValueComparer lookupComparer = createLookupComparer(lookupValue);
        if (isRangeLookup) {
            result = performBinarySearch(vector, lookupComparer);
        } else {
            result = lookupIndexOfExactValue(lookupComparer, vector);
        }
        if (result >= 0) {
            return result;
        }
        throw new EvaluationException(ErrorEval.NA);
    }

    private static int lookupIndexOfExactValue(LookupValueComparer lookupComparer, ValueVector vector) {
        int size = vector.getSize();
        for (int i = 0; i < size; i++) {
            if (lookupComparer.compareTo(vector.getItem(i)).isEqual()) {
                return i;
            }
        }
        return -1;
    }

    private static final class BinarySearchIndexes {
        private int _highIx;
        private int _lowIx = -1;

        public BinarySearchIndexes(int highIx) {
            this._highIx = highIx;
        }

        public int getMidIx() {
            int i = this._highIx;
            int i2 = this._lowIx;
            int ixDiff = i - i2;
            if (ixDiff < 2) {
                return -1;
            }
            return i2 + (ixDiff / 2);
        }

        public int getLowIx() {
            return this._lowIx;
        }

        public int getHighIx() {
            return this._highIx;
        }

        public void narrowSearch(int midIx, boolean isLessThan) {
            if (isLessThan) {
                this._highIx = midIx;
            } else {
                this._lowIx = midIx;
            }
        }
    }

    private static int performBinarySearch(ValueVector vector, LookupValueComparer lookupComparer) {
        BinarySearchIndexes bsi = new BinarySearchIndexes(vector.getSize());
        while (true) {
            int midIx = bsi.getMidIx();
            if (midIx < 0) {
                return bsi.getLowIx();
            }
            CompareResult cr = lookupComparer.compareTo(vector.getItem(midIx));
            if (cr.isTypeMismatch()) {
                int newMidIx = handleMidValueTypeMismatch(lookupComparer, vector, bsi, midIx);
                if (newMidIx < 0) {
                    continue;
                } else {
                    midIx = newMidIx;
                    cr = lookupComparer.compareTo(vector.getItem(midIx));
                }
            }
            if (cr.isEqual() != 0) {
                return findLastIndexInRunOfEqualValues(lookupComparer, vector, midIx, bsi.getHighIx());
            }
            bsi.narrowSearch(midIx, cr.isLessThan());
        }
    }

    private static int handleMidValueTypeMismatch(LookupValueComparer lookupComparer, ValueVector vector, BinarySearchIndexes bsi, int midIx) {
        CompareResult cr;
        int newMid = midIx;
        int highIx = bsi.getHighIx();
        do {
            newMid++;
            if (newMid == highIx) {
                bsi.narrowSearch(midIx, true);
                return -1;
            }
            cr = lookupComparer.compareTo(vector.getItem(newMid));
            if (cr.isLessThan() && newMid == highIx - 1) {
                bsi.narrowSearch(midIx, true);
                return -1;
            }
        } while (cr.isTypeMismatch());
        if (cr.isEqual()) {
            return newMid;
        }
        bsi.narrowSearch(newMid, cr.isLessThan());
        return -1;
    }

    private static int findLastIndexInRunOfEqualValues(LookupValueComparer lookupComparer, ValueVector vector, int firstFoundIndex, int maxIx) {
        for (int i = firstFoundIndex + 1; i < maxIx; i++) {
            if (!lookupComparer.compareTo(vector.getItem(i)).isEqual()) {
                return i - 1;
            }
        }
        return maxIx - 1;
    }

    public static LookupValueComparer createLookupComparer(ValueEval lookupValue) {
        if (lookupValue == BlankEval.instance) {
            return new NumberLookupComparer(NumberEval.ZERO);
        }
        if (lookupValue instanceof StringEval) {
            return new StringLookupComparer((StringEval) lookupValue);
        }
        if (lookupValue instanceof NumberEval) {
            return new NumberLookupComparer((NumberEval) lookupValue);
        }
        if (lookupValue instanceof BoolEval) {
            return new BooleanLookupComparer((BoolEval) lookupValue);
        }
        throw new IllegalArgumentException("Bad lookup value type (" + lookupValue.getClass().getName() + ")");
    }
}
