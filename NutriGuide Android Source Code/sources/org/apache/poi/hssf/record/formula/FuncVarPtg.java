package org.apache.poi.hssf.record.formula;

import org.apache.poi.hssf.record.formula.function.FunctionMetadata;
import org.apache.poi.hssf.record.formula.function.FunctionMetadataRegistry;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

public final class FuncVarPtg extends AbstractFunctionPtg {
    private static final int SIZE = 4;
    public static final OperationPtg SUM = create("SUM", 1);
    public static final byte sid = 34;

    private FuncVarPtg(int functionIndex, int returnClass, byte[] paramClasses, int numArgs) {
        super(functionIndex, returnClass, paramClasses, numArgs);
    }

    public static FuncVarPtg create(LittleEndianInput in) {
        return create((int) in.readByte(), (int) in.readShort());
    }

    public static FuncVarPtg create(String pName, int numArgs) {
        return create(numArgs, (int) lookupIndex(pName));
    }

    private static FuncVarPtg create(int numArgs, int functionIndex) {
        FunctionMetadata fm = FunctionMetadataRegistry.getFunctionByIndex(functionIndex);
        if (fm != null) {
            return new FuncVarPtg(functionIndex, fm.getReturnClassCode(), fm.getParameterClassCodes(), numArgs);
        }
        return new FuncVarPtg(functionIndex, 32, new byte[]{32}, numArgs);
    }

    public void write(LittleEndianOutput out) {
        out.writeByte(getPtgClass() + 34);
        out.writeByte(getNumberOfOperands());
        out.writeShort(getFunctionIndex());
    }

    public int getSize() {
        return 4;
    }
}
