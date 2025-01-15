package org.apache.poi.ss.formula;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.hssf.record.formula.AbstractFunctionPtg;
import org.apache.poi.hssf.record.formula.AddPtg;
import org.apache.poi.hssf.record.formula.ConcatPtg;
import org.apache.poi.hssf.record.formula.DividePtg;
import org.apache.poi.hssf.record.formula.EqualPtg;
import org.apache.poi.hssf.record.formula.GreaterEqualPtg;
import org.apache.poi.hssf.record.formula.GreaterThanPtg;
import org.apache.poi.hssf.record.formula.IntersectionPtg;
import org.apache.poi.hssf.record.formula.LessEqualPtg;
import org.apache.poi.hssf.record.formula.LessThanPtg;
import org.apache.poi.hssf.record.formula.MultiplyPtg;
import org.apache.poi.hssf.record.formula.NotEqualPtg;
import org.apache.poi.hssf.record.formula.OperationPtg;
import org.apache.poi.hssf.record.formula.PercentPtg;
import org.apache.poi.hssf.record.formula.PowerPtg;
import org.apache.poi.hssf.record.formula.RangePtg;
import org.apache.poi.hssf.record.formula.SubtractPtg;
import org.apache.poi.hssf.record.formula.UnaryMinusPtg;
import org.apache.poi.hssf.record.formula.UnaryPlusPtg;
import org.apache.poi.hssf.record.formula.eval.ConcatEval;
import org.apache.poi.hssf.record.formula.eval.FunctionEval;
import org.apache.poi.hssf.record.formula.eval.IntersectionEval;
import org.apache.poi.hssf.record.formula.eval.PercentEval;
import org.apache.poi.hssf.record.formula.eval.RangeEval;
import org.apache.poi.hssf.record.formula.eval.RelationalOperationEval;
import org.apache.poi.hssf.record.formula.eval.TwoOperandNumericOperation;
import org.apache.poi.hssf.record.formula.eval.UnaryMinusEval;
import org.apache.poi.hssf.record.formula.eval.UnaryPlusEval;
import org.apache.poi.hssf.record.formula.eval.ValueEval;
import org.apache.poi.hssf.record.formula.functions.Function;
import org.apache.poi.hssf.record.formula.functions.Indirect;

final class OperationEvaluatorFactory {
    private static final Map<OperationPtg, Function> _instancesByPtgClass = initialiseInstancesMap();

    private OperationEvaluatorFactory() {
    }

    private static Map<OperationPtg, Function> initialiseInstancesMap() {
        Map<OperationPtg, Function> m = new HashMap<>(32);
        put(m, EqualPtg.instance, RelationalOperationEval.EqualEval);
        put(m, GreaterEqualPtg.instance, RelationalOperationEval.GreaterEqualEval);
        put(m, GreaterThanPtg.instance, RelationalOperationEval.GreaterThanEval);
        put(m, LessEqualPtg.instance, RelationalOperationEval.LessEqualEval);
        put(m, LessThanPtg.instance, RelationalOperationEval.LessThanEval);
        put(m, NotEqualPtg.instance, RelationalOperationEval.NotEqualEval);
        put(m, ConcatPtg.instance, ConcatEval.instance);
        put(m, AddPtg.instance, TwoOperandNumericOperation.AddEval);
        put(m, DividePtg.instance, TwoOperandNumericOperation.DivideEval);
        put(m, MultiplyPtg.instance, TwoOperandNumericOperation.MultiplyEval);
        put(m, PercentPtg.instance, PercentEval.instance);
        put(m, PowerPtg.instance, TwoOperandNumericOperation.PowerEval);
        put(m, SubtractPtg.instance, TwoOperandNumericOperation.SubtractEval);
        put(m, UnaryMinusPtg.instance, UnaryMinusEval.instance);
        put(m, UnaryPlusPtg.instance, UnaryPlusEval.instance);
        put(m, RangePtg.instance, RangeEval.instance);
        put(m, IntersectionPtg.instance, IntersectionEval.instance);
        return m;
    }

    private static void put(Map<OperationPtg, Function> m, OperationPtg ptgKey, Function instance) {
        Constructor[] cc = ptgKey.getClass().getDeclaredConstructors();
        if (cc.length > 1 || !Modifier.isPrivate(cc[0].getModifiers())) {
            throw new RuntimeException("Failed to verify instance (" + ptgKey.getClass().getName() + ") is a singleton.");
        }
        m.put(ptgKey, instance);
    }

    public static ValueEval evaluate(OperationPtg ptg, ValueEval[] args, OperationEvaluationContext ec) {
        if (ptg != null) {
            Function result = _instancesByPtgClass.get(ptg);
            if (result != null) {
                return result.evaluate(args, ec.getRowIndex(), (short) ec.getColumnIndex());
            }
            if (ptg instanceof AbstractFunctionPtg) {
                int functionIndex = ((AbstractFunctionPtg) ptg).getFunctionIndex();
                if (functionIndex == 148) {
                    return Indirect.instance.evaluate(args, ec);
                }
                if (functionIndex != 255) {
                    return FunctionEval.getBasicFunction(functionIndex).evaluate(args, ec.getRowIndex(), (short) ec.getColumnIndex());
                }
                return UserDefinedFunction.instance.evaluate(args, ec);
            }
            throw new RuntimeException("Unexpected operation ptg class (" + ptg.getClass().getName() + ")");
        }
        throw new IllegalArgumentException("ptg must not be null");
    }
}
