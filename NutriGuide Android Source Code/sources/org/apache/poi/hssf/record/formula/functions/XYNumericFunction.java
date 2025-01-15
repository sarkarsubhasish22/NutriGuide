package org.apache.poi.hssf.record.formula.functions;

import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.formula.eval.EvaluationException;
import org.apache.poi.hssf.record.formula.eval.NumberEval;
import org.apache.poi.hssf.record.formula.eval.RefEval;
import org.apache.poi.hssf.record.formula.eval.ValueEval;
import org.apache.poi.hssf.record.formula.functions.LookupUtils;
import org.apache.poi.ss.formula.TwoDEval;

public abstract class XYNumericFunction extends Fixed2ArgFunction {

    protected interface Accumulator {
        double accumulate(double d, double d2);
    }

    /* access modifiers changed from: protected */
    public abstract Accumulator createAccumulator();

    private static abstract class ValueArray implements LookupUtils.ValueVector {
        private final int _size;

        /* access modifiers changed from: protected */
        public abstract ValueEval getItemInternal(int i);

        protected ValueArray(int size) {
            this._size = size;
        }

        public ValueEval getItem(int index) {
            if (index >= 0 && index <= this._size) {
                return getItemInternal(index);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Specified index ");
            sb.append(index);
            sb.append(" is outside range (0..");
            sb.append(this._size - 1);
            sb.append(")");
            throw new IllegalArgumentException(sb.toString());
        }

        public final int getSize() {
            return this._size;
        }
    }

    private static final class SingleCellValueArray extends ValueArray {
        private final ValueEval _value;

        public SingleCellValueArray(ValueEval value) {
            super(1);
            this._value = value;
        }

        /* access modifiers changed from: protected */
        public ValueEval getItemInternal(int index) {
            return this._value;
        }
    }

    private static final class RefValueArray extends ValueArray {
        private final RefEval _ref;

        public RefValueArray(RefEval ref) {
            super(1);
            this._ref = ref;
        }

        /* access modifiers changed from: protected */
        public ValueEval getItemInternal(int index) {
            return this._ref.getInnerValueEval();
        }
    }

    private static final class AreaValueArray extends ValueArray {
        private final TwoDEval _ae;
        private final int _width;

        public AreaValueArray(TwoDEval ae) {
            super(ae.getWidth() * ae.getHeight());
            this._ae = ae;
            this._width = ae.getWidth();
        }

        /* access modifiers changed from: protected */
        public ValueEval getItemInternal(int index) {
            int i = this._width;
            return this._ae.getValue(index / i, index % i);
        }
    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
        try {
            LookupUtils.ValueVector vvX = createValueVector(arg0);
            LookupUtils.ValueVector vvY = createValueVector(arg1);
            int size = vvX.getSize();
            if (size != 0) {
                if (vvY.getSize() == size) {
                    double result = evaluateInternal(vvX, vvY, size);
                    if (Double.isNaN(result) || Double.isInfinite(result)) {
                        return ErrorEval.NUM_ERROR;
                    }
                    return new NumberEval(result);
                }
            }
            return ErrorEval.NA;
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    /* JADX WARNING: type inference failed for: r9v0, types: [org.apache.poi.hssf.record.formula.eval.ValueEval] */
    /* JADX WARNING: type inference failed for: r11v0, types: [org.apache.poi.hssf.record.formula.eval.ValueEval] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 2 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private double evaluateInternal(org.apache.poi.hssf.record.formula.functions.LookupUtils.ValueVector r17, org.apache.poi.hssf.record.formula.functions.LookupUtils.ValueVector r18, int r19) throws org.apache.poi.hssf.record.formula.eval.EvaluationException {
        /*
            r16 = this;
            org.apache.poi.hssf.record.formula.functions.XYNumericFunction$Accumulator r0 = r16.createAccumulator()
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            r6 = 0
        L_0x000a:
            r7 = r19
            if (r6 >= r7) goto L_0x004d
            r8 = r17
            org.apache.poi.hssf.record.formula.eval.ValueEval r9 = r8.getItem(r6)
            r10 = r18
            org.apache.poi.hssf.record.formula.eval.ValueEval r11 = r10.getItem(r6)
            boolean r12 = r9 instanceof org.apache.poi.hssf.record.formula.eval.ErrorEval
            if (r12 == 0) goto L_0x0024
            if (r1 != 0) goto L_0x0024
            r1 = r9
            org.apache.poi.hssf.record.formula.eval.ErrorEval r1 = (org.apache.poi.hssf.record.formula.eval.ErrorEval) r1
            goto L_0x004a
        L_0x0024:
            boolean r12 = r11 instanceof org.apache.poi.hssf.record.formula.eval.ErrorEval
            if (r12 == 0) goto L_0x002e
            if (r2 != 0) goto L_0x002e
            r2 = r11
            org.apache.poi.hssf.record.formula.eval.ErrorEval r2 = (org.apache.poi.hssf.record.formula.eval.ErrorEval) r2
            goto L_0x004a
        L_0x002e:
            boolean r12 = r9 instanceof org.apache.poi.hssf.record.formula.eval.NumberEval
            if (r12 == 0) goto L_0x004a
            boolean r12 = r11 instanceof org.apache.poi.hssf.record.formula.eval.NumberEval
            if (r12 == 0) goto L_0x004a
            r3 = 1
            r12 = r9
            org.apache.poi.hssf.record.formula.eval.NumberEval r12 = (org.apache.poi.hssf.record.formula.eval.NumberEval) r12
            r13 = r11
            org.apache.poi.hssf.record.formula.eval.NumberEval r13 = (org.apache.poi.hssf.record.formula.eval.NumberEval) r13
            double r14 = r12.getNumberValue()
            double r7 = r13.getNumberValue()
            double r7 = r0.accumulate(r14, r7)
            double r4 = r4 + r7
        L_0x004a:
            int r6 = r6 + 1
            goto L_0x000a
        L_0x004d:
            r10 = r18
            if (r1 != 0) goto L_0x0064
            if (r2 != 0) goto L_0x005e
            if (r3 == 0) goto L_0x0056
            return r4
        L_0x0056:
            org.apache.poi.hssf.record.formula.eval.EvaluationException r6 = new org.apache.poi.hssf.record.formula.eval.EvaluationException
            org.apache.poi.hssf.record.formula.eval.ErrorEval r7 = org.apache.poi.hssf.record.formula.eval.ErrorEval.DIV_ZERO
            r6.<init>(r7)
            throw r6
        L_0x005e:
            org.apache.poi.hssf.record.formula.eval.EvaluationException r6 = new org.apache.poi.hssf.record.formula.eval.EvaluationException
            r6.<init>(r2)
            throw r6
        L_0x0064:
            org.apache.poi.hssf.record.formula.eval.EvaluationException r6 = new org.apache.poi.hssf.record.formula.eval.EvaluationException
            r6.<init>(r1)
            goto L_0x006b
        L_0x006a:
            throw r6
        L_0x006b:
            goto L_0x006a
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.hssf.record.formula.functions.XYNumericFunction.evaluateInternal(org.apache.poi.hssf.record.formula.functions.LookupUtils$ValueVector, org.apache.poi.hssf.record.formula.functions.LookupUtils$ValueVector, int):double");
    }

    private static LookupUtils.ValueVector createValueVector(ValueEval arg) throws EvaluationException {
        if (arg instanceof ErrorEval) {
            throw new EvaluationException((ErrorEval) arg);
        } else if (arg instanceof TwoDEval) {
            return new AreaValueArray((TwoDEval) arg);
        } else {
            if (arg instanceof RefEval) {
                return new RefValueArray((RefEval) arg);
            }
            return new SingleCellValueArray(arg);
        }
    }
}
