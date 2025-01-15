package org.apache.poi.ss.formula;

import java.util.Stack;
import org.apache.poi.hssf.record.formula.AttrPtg;
import org.apache.poi.hssf.record.formula.MemAreaPtg;
import org.apache.poi.hssf.record.formula.MemErrPtg;
import org.apache.poi.hssf.record.formula.MemFuncPtg;
import org.apache.poi.hssf.record.formula.OperationPtg;
import org.apache.poi.hssf.record.formula.ParenthesisPtg;
import org.apache.poi.hssf.record.formula.Ptg;

public class FormulaRenderer {
    public static String toFormulaString(FormulaRenderingWorkbook book, Ptg[] ptgs) {
        if (ptgs == null || ptgs.length == 0) {
            throw new IllegalArgumentException("ptgs must not be null");
        }
        Stack<String> stack = new Stack<>();
        for (AttrPtg attrPtg : ptgs) {
            if (!(attrPtg instanceof MemAreaPtg) && !(attrPtg instanceof MemFuncPtg) && !(attrPtg instanceof MemErrPtg)) {
                if (attrPtg instanceof ParenthesisPtg) {
                    stack.push("(" + stack.pop() + ")");
                } else if (attrPtg instanceof AttrPtg) {
                    AttrPtg attrPtg2 = attrPtg;
                    if (!attrPtg2.isOptimizedIf() && !attrPtg2.isOptimizedChoose() && !attrPtg2.isSkip() && !attrPtg2.isSpace() && !attrPtg2.isSemiVolatile()) {
                        if (attrPtg2.isSum()) {
                            stack.push(attrPtg2.toFormulaString(getOperands(stack, attrPtg2.getNumberOfOperands())));
                        } else {
                            throw new RuntimeException("Unexpected tAttr: " + attrPtg2.toString());
                        }
                    }
                } else if (attrPtg instanceof WorkbookDependentFormula) {
                    stack.push(((WorkbookDependentFormula) attrPtg).toFormulaString(book));
                } else if (!(attrPtg instanceof OperationPtg)) {
                    stack.push(attrPtg.toFormulaString());
                } else {
                    OperationPtg o = attrPtg;
                    stack.push(o.toFormulaString(getOperands(stack, o.getNumberOfOperands())));
                }
            }
        }
        if (stack.isEmpty() == 0) {
            String result = stack.pop();
            if (stack.isEmpty()) {
                return result;
            }
            throw new IllegalStateException("too much stuff left on the stack");
        }
        throw new IllegalStateException("Stack underflow");
    }

    private static String[] getOperands(Stack<String> stack, int nOperands) {
        String[] operands = new String[nOperands];
        int j = nOperands - 1;
        while (j >= 0) {
            if (!stack.isEmpty()) {
                operands[j] = stack.pop();
                j--;
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Too few arguments supplied to operation. Expected (");
                sb.append(nOperands);
                sb.append(") operands but got (");
                sb.append((nOperands - j) - 1);
                sb.append(")");
                throw new IllegalStateException(sb.toString());
            }
        }
        return operands;
    }
}
