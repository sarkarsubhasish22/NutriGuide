package org.apache.poi.hssf.record.formula.functions;

import org.apache.poi.hssf.record.formula.eval.AreaEval;
import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.formula.eval.EvaluationException;
import org.apache.poi.hssf.record.formula.eval.OperandResolver;
import org.apache.poi.hssf.record.formula.eval.RefEval;
import org.apache.poi.hssf.record.formula.eval.ValueEval;

public final class Offset implements Function {
    private static final int LAST_VALID_COLUMN_INDEX = 255;
    private static final int LAST_VALID_ROW_INDEX = 65535;

    static final class LinearOffsetRange {
        private final int _length;
        private final int _offset;

        public LinearOffsetRange(int offset, int length) {
            if (length != 0) {
                this._offset = offset;
                this._length = length;
                return;
            }
            throw new RuntimeException("length may not be zero");
        }

        public short getFirstIndex() {
            return (short) this._offset;
        }

        public short getLastIndex() {
            return (short) ((this._offset + this._length) - 1);
        }

        public LinearOffsetRange normaliseAndTranslate(int translationAmount) {
            int i = this._length;
            if (i <= 0) {
                return new LinearOffsetRange(this._offset + translationAmount + i + 1, -i);
            }
            if (translationAmount == 0) {
                return this;
            }
            return new LinearOffsetRange(this._offset + translationAmount, i);
        }

        public boolean isOutOfBounds(int lowValidIx, int highValidIx) {
            if (this._offset >= lowValidIx && getLastIndex() <= highValidIx) {
                return false;
            }
            return true;
        }

        public String toString() {
            StringBuffer sb = new StringBuffer(64);
            sb.append(getClass().getName());
            sb.append(" [");
            sb.append(this._offset);
            sb.append("...");
            sb.append(getLastIndex());
            sb.append("]");
            return sb.toString();
        }
    }

    private static final class BaseRef {
        private final AreaEval _areaEval;
        private final int _firstColumnIndex;
        private final int _firstRowIndex;
        private final int _height;
        private final RefEval _refEval;
        private final int _width;

        public BaseRef(RefEval re) {
            this._refEval = re;
            this._areaEval = null;
            this._firstRowIndex = re.getRow();
            this._firstColumnIndex = re.getColumn();
            this._height = 1;
            this._width = 1;
        }

        public BaseRef(AreaEval ae) {
            this._refEval = null;
            this._areaEval = ae;
            this._firstRowIndex = ae.getFirstRow();
            this._firstColumnIndex = ae.getFirstColumn();
            this._height = (ae.getLastRow() - ae.getFirstRow()) + 1;
            this._width = (ae.getLastColumn() - ae.getFirstColumn()) + 1;
        }

        public int getWidth() {
            return this._width;
        }

        public int getHeight() {
            return this._height;
        }

        public int getFirstRowIndex() {
            return this._firstRowIndex;
        }

        public int getFirstColumnIndex() {
            return this._firstColumnIndex;
        }

        public AreaEval offset(int relFirstRowIx, int relLastRowIx, int relFirstColIx, int relLastColIx) {
            RefEval refEval = this._refEval;
            if (refEval == null) {
                return this._areaEval.offset(relFirstRowIx, relLastRowIx, relFirstColIx, relLastColIx);
            }
            return refEval.offset(relFirstRowIx, relLastRowIx, relFirstColIx, relLastColIx);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x003d A[Catch:{ EvaluationException -> 0x0052 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.apache.poi.hssf.record.formula.eval.ValueEval evaluate(org.apache.poi.hssf.record.formula.eval.ValueEval[] r10, int r11, int r12) {
        /*
            r9 = this;
            int r0 = r10.length
            r1 = 3
            if (r0 < r1) goto L_0x0058
            int r0 = r10.length
            r2 = 5
            if (r0 <= r2) goto L_0x0009
            goto L_0x0058
        L_0x0009:
            r0 = 0
            r0 = r10[r0]     // Catch:{ EvaluationException -> 0x0052 }
            org.apache.poi.hssf.record.formula.functions.Offset$BaseRef r0 = evaluateBaseRef(r0)     // Catch:{ EvaluationException -> 0x0052 }
            r3 = 1
            r3 = r10[r3]     // Catch:{ EvaluationException -> 0x0052 }
            int r3 = evaluateIntArg(r3, r11, r12)     // Catch:{ EvaluationException -> 0x0052 }
            r4 = 2
            r4 = r10[r4]     // Catch:{ EvaluationException -> 0x0052 }
            int r4 = evaluateIntArg(r4, r11, r12)     // Catch:{ EvaluationException -> 0x0052 }
            int r5 = r0.getHeight()     // Catch:{ EvaluationException -> 0x0052 }
            int r6 = r0.getWidth()     // Catch:{ EvaluationException -> 0x0052 }
            int r7 = r10.length     // Catch:{ EvaluationException -> 0x0052 }
            r8 = 4
            if (r7 == r8) goto L_0x0034
            if (r7 == r2) goto L_0x002d
            goto L_0x003b
        L_0x002d:
            r2 = r10[r8]     // Catch:{ EvaluationException -> 0x0052 }
            int r2 = evaluateIntArg(r2, r11, r12)     // Catch:{ EvaluationException -> 0x0052 }
            r6 = r2
        L_0x0034:
            r1 = r10[r1]     // Catch:{ EvaluationException -> 0x0052 }
            int r1 = evaluateIntArg(r1, r11, r12)     // Catch:{ EvaluationException -> 0x0052 }
            r5 = r1
        L_0x003b:
            if (r5 == 0) goto L_0x004f
            if (r6 != 0) goto L_0x0040
            goto L_0x004f
        L_0x0040:
            org.apache.poi.hssf.record.formula.functions.Offset$LinearOffsetRange r1 = new org.apache.poi.hssf.record.formula.functions.Offset$LinearOffsetRange     // Catch:{ EvaluationException -> 0x0052 }
            r1.<init>(r3, r5)     // Catch:{ EvaluationException -> 0x0052 }
            org.apache.poi.hssf.record.formula.functions.Offset$LinearOffsetRange r2 = new org.apache.poi.hssf.record.formula.functions.Offset$LinearOffsetRange     // Catch:{ EvaluationException -> 0x0052 }
            r2.<init>(r4, r6)     // Catch:{ EvaluationException -> 0x0052 }
            org.apache.poi.hssf.record.formula.eval.AreaEval r7 = createOffset(r0, r1, r2)     // Catch:{ EvaluationException -> 0x0052 }
            return r7
        L_0x004f:
            org.apache.poi.hssf.record.formula.eval.ErrorEval r1 = org.apache.poi.hssf.record.formula.eval.ErrorEval.REF_INVALID     // Catch:{ EvaluationException -> 0x0052 }
            return r1
        L_0x0052:
            r0 = move-exception
            org.apache.poi.hssf.record.formula.eval.ErrorEval r1 = r0.getErrorEval()
            return r1
        L_0x0058:
            org.apache.poi.hssf.record.formula.eval.ErrorEval r0 = org.apache.poi.hssf.record.formula.eval.ErrorEval.VALUE_INVALID
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.hssf.record.formula.functions.Offset.evaluate(org.apache.poi.hssf.record.formula.eval.ValueEval[], int, int):org.apache.poi.hssf.record.formula.eval.ValueEval");
    }

    private static AreaEval createOffset(BaseRef baseRef, LinearOffsetRange orRow, LinearOffsetRange orCol) throws EvaluationException {
        LinearOffsetRange absRows = orRow.normaliseAndTranslate(baseRef.getFirstRowIndex());
        LinearOffsetRange absCols = orCol.normaliseAndTranslate(baseRef.getFirstColumnIndex());
        if (absRows.isOutOfBounds(0, 65535)) {
            throw new EvaluationException(ErrorEval.REF_INVALID);
        } else if (!absCols.isOutOfBounds(0, 255)) {
            return baseRef.offset(orRow.getFirstIndex(), orRow.getLastIndex(), orCol.getFirstIndex(), orCol.getLastIndex());
        } else {
            throw new EvaluationException(ErrorEval.REF_INVALID);
        }
    }

    private static BaseRef evaluateBaseRef(ValueEval eval) throws EvaluationException {
        if (eval instanceof RefEval) {
            return new BaseRef((RefEval) eval);
        }
        if (eval instanceof AreaEval) {
            return new BaseRef((AreaEval) eval);
        }
        if (eval instanceof ErrorEval) {
            throw new EvaluationException((ErrorEval) eval);
        }
        throw new EvaluationException(ErrorEval.VALUE_INVALID);
    }

    static int evaluateIntArg(ValueEval eval, int srcCellRow, int srcCellCol) throws EvaluationException {
        return OperandResolver.coerceValueToInt(OperandResolver.getSingleValue(eval, srcCellRow, srcCellCol));
    }
}
