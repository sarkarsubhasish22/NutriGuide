package org.apache.poi.ss.formula;

import org.apache.poi.hssf.record.formula.AbstractFunctionPtg;
import org.apache.poi.hssf.record.formula.AttrPtg;
import org.apache.poi.hssf.record.formula.ControlPtg;
import org.apache.poi.hssf.record.formula.FuncVarPtg;
import org.apache.poi.hssf.record.formula.MemAreaPtg;
import org.apache.poi.hssf.record.formula.MemFuncPtg;
import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.hssf.record.formula.RangePtg;
import org.apache.poi.hssf.record.formula.UnionPtg;
import org.apache.poi.hssf.record.formula.ValueOperatorPtg;

final class OperandClassTransformer {
    private final int _formulaType;

    public OperandClassTransformer(int formulaType) {
        this._formulaType = formulaType;
    }

    public void transformFormula(ParseNode rootNode) {
        byte rootNodeOperandClass;
        int i = this._formulaType;
        if (i == 0) {
            rootNodeOperandClass = 32;
        } else if (i == 2) {
            rootNodeOperandClass = Ptg.CLASS_ARRAY;
        } else if (i == 4 || i == 5) {
            rootNodeOperandClass = 0;
        } else {
            throw new RuntimeException("Incomplete code - formula type (" + this._formulaType + ") not supported yet");
        }
        transformNode(rootNode, rootNodeOperandClass, false);
    }

    private void transformNode(ParseNode node, byte desiredOperandClass, boolean callerForceArrayFlag) {
        Ptg token = node.getToken();
        ParseNode[] children = node.getChildren();
        if (isSimpleValueFunction(token)) {
            boolean localForceArray = desiredOperandClass == 64;
            for (ParseNode transformNode : children) {
                transformNode(transformNode, desiredOperandClass, localForceArray);
            }
            setSimpleValueFuncClass((AbstractFunctionPtg) token, desiredOperandClass, callerForceArrayFlag);
            return;
        }
        if (isSingleArgSum(token)) {
            token = FuncVarPtg.SUM;
        }
        if ((token instanceof ValueOperatorPtg) || (token instanceof ControlPtg) || (token instanceof MemFuncPtg) || (token instanceof MemAreaPtg) || (token instanceof UnionPtg)) {
            byte localDesiredOperandClass = desiredOperandClass == 0 ? 32 : desiredOperandClass;
            for (ParseNode transformNode2 : children) {
                transformNode(transformNode2, localDesiredOperandClass, callerForceArrayFlag);
            }
        } else if (token instanceof AbstractFunctionPtg) {
            transformFunctionNode((AbstractFunctionPtg) token, children, desiredOperandClass, callerForceArrayFlag);
        } else if (children.length > 0) {
            if (token != RangePtg.instance) {
                throw new IllegalStateException("Node should not have any children");
            }
        } else if (!token.isBaseToken()) {
            token.setClass(transformClass(token.getPtgClass(), desiredOperandClass, callerForceArrayFlag));
        }
    }

    private static boolean isSingleArgSum(Ptg token) {
        if (token instanceof AttrPtg) {
            return ((AttrPtg) token).isSum();
        }
        return false;
    }

    private static boolean isSimpleValueFunction(Ptg token) {
        if (!(token instanceof AbstractFunctionPtg)) {
            return false;
        }
        AbstractFunctionPtg aptg = (AbstractFunctionPtg) token;
        if (aptg.getDefaultOperandClass() != 32) {
            return false;
        }
        for (int i = aptg.getNumberOfOperands() - 1; i >= 0; i--) {
            if (aptg.getParameterClass(i) != 32) {
                return false;
            }
        }
        return true;
    }

    private byte transformClass(byte currentOperandClass, byte desiredOperandClass, boolean callerForceArrayFlag) {
        if (desiredOperandClass != 0) {
            if (desiredOperandClass != 32) {
                if (desiredOperandClass != 64) {
                    throw new IllegalStateException("Unexpected operand class (" + desiredOperandClass + ")");
                }
            } else if (!callerForceArrayFlag) {
                return 32;
            }
            return Ptg.CLASS_ARRAY;
        } else if (!callerForceArrayFlag) {
            return currentOperandClass;
        } else {
            return 0;
        }
    }

    private void transformFunctionNode(AbstractFunctionPtg afp, ParseNode[] children, byte desiredOperandClass, boolean callerForceArrayFlag) {
        boolean localForceArrayFlag;
        byte defaultReturnOperandClass = afp.getDefaultOperandClass();
        boolean z = false;
        if (callerForceArrayFlag) {
            if (defaultReturnOperandClass == 0) {
                if (desiredOperandClass == 0) {
                    afp.setClass((byte) 0);
                } else {
                    afp.setClass(Ptg.CLASS_ARRAY);
                }
                localForceArrayFlag = false;
            } else if (defaultReturnOperandClass == 32) {
                afp.setClass(Ptg.CLASS_ARRAY);
                localForceArrayFlag = true;
            } else if (defaultReturnOperandClass == 64) {
                afp.setClass(Ptg.CLASS_ARRAY);
                localForceArrayFlag = false;
            } else {
                throw new IllegalStateException("Unexpected operand class (" + defaultReturnOperandClass + ")");
            }
        } else if (defaultReturnOperandClass == desiredOperandClass) {
            localForceArrayFlag = false;
            afp.setClass(defaultReturnOperandClass);
        } else if (desiredOperandClass == 0) {
            if (defaultReturnOperandClass == 32) {
                afp.setClass((byte) 32);
            } else if (defaultReturnOperandClass == 64) {
                afp.setClass(Ptg.CLASS_ARRAY);
            } else {
                throw new IllegalStateException("Unexpected operand class (" + defaultReturnOperandClass + ")");
            }
            localForceArrayFlag = false;
        } else if (desiredOperandClass == 32) {
            afp.setClass((byte) 32);
            localForceArrayFlag = false;
        } else if (desiredOperandClass == 64) {
            if (defaultReturnOperandClass == 0) {
                afp.setClass((byte) 0);
            } else if (defaultReturnOperandClass == 32) {
                afp.setClass(Ptg.CLASS_ARRAY);
            } else {
                throw new IllegalStateException("Unexpected operand class (" + defaultReturnOperandClass + ")");
            }
            if (defaultReturnOperandClass == 32) {
                z = true;
            }
            localForceArrayFlag = z;
        } else {
            throw new IllegalStateException("Unexpected operand class (" + desiredOperandClass + ")");
        }
        for (int i = 0; i < children.length; i++) {
            transformNode(children[i], afp.getParameterClass(i), localForceArrayFlag);
        }
    }

    private void setSimpleValueFuncClass(AbstractFunctionPtg afp, byte desiredOperandClass, boolean callerForceArrayFlag) {
        if (callerForceArrayFlag || desiredOperandClass == 64) {
            afp.setClass(Ptg.CLASS_ARRAY);
        } else {
            afp.setClass((byte) 32);
        }
    }
}
