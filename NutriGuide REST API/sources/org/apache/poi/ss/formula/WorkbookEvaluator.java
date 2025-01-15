package org.apache.poi.ss.formula;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Stack;
import org.apache.poi.hssf.record.formula.Area3DPtg;
import org.apache.poi.hssf.record.formula.AreaErrPtg;
import org.apache.poi.hssf.record.formula.AreaPtg;
import org.apache.poi.hssf.record.formula.AttrPtg;
import org.apache.poi.hssf.record.formula.BoolPtg;
import org.apache.poi.hssf.record.formula.ControlPtg;
import org.apache.poi.hssf.record.formula.DeletedArea3DPtg;
import org.apache.poi.hssf.record.formula.DeletedRef3DPtg;
import org.apache.poi.hssf.record.formula.ErrPtg;
import org.apache.poi.hssf.record.formula.ExpPtg;
import org.apache.poi.hssf.record.formula.FuncVarPtg;
import org.apache.poi.hssf.record.formula.IntPtg;
import org.apache.poi.hssf.record.formula.MemAreaPtg;
import org.apache.poi.hssf.record.formula.MemErrPtg;
import org.apache.poi.hssf.record.formula.MemFuncPtg;
import org.apache.poi.hssf.record.formula.MissingArgPtg;
import org.apache.poi.hssf.record.formula.NamePtg;
import org.apache.poi.hssf.record.formula.NameXPtg;
import org.apache.poi.hssf.record.formula.NumberPtg;
import org.apache.poi.hssf.record.formula.OperationPtg;
import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.hssf.record.formula.Ref3DPtg;
import org.apache.poi.hssf.record.formula.RefErrorPtg;
import org.apache.poi.hssf.record.formula.RefPtg;
import org.apache.poi.hssf.record.formula.StringPtg;
import org.apache.poi.hssf.record.formula.UnionPtg;
import org.apache.poi.hssf.record.formula.UnknownPtg;
import org.apache.poi.hssf.record.formula.eval.BlankEval;
import org.apache.poi.hssf.record.formula.eval.BoolEval;
import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.formula.eval.EvaluationException;
import org.apache.poi.hssf.record.formula.eval.MissingArgEval;
import org.apache.poi.hssf.record.formula.eval.NameEval;
import org.apache.poi.hssf.record.formula.eval.NumberEval;
import org.apache.poi.hssf.record.formula.eval.OperandResolver;
import org.apache.poi.hssf.record.formula.eval.StringEval;
import org.apache.poi.hssf.record.formula.eval.ValueEval;
import org.apache.poi.hssf.record.formula.functions.Choose;
import org.apache.poi.hssf.record.formula.functions.FreeRefFunction;
import org.apache.poi.hssf.record.formula.functions.IfFunc;
import org.apache.poi.hssf.record.formula.udf.UDFFinder;
import org.apache.poi.hssf.usermodel.HSSFEvaluationWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.formula.CollaboratingWorkbooksEnvironment;
import org.apache.poi.ss.formula.eval.NotImplementedException;

public final class WorkbookEvaluator {
    private EvaluationCache _cache;
    private CollaboratingWorkbooksEnvironment _collaboratingWorkbookEnvironment;
    private final IEvaluationListener _evaluationListener;
    private final Map<String, Integer> _sheetIndexesByName;
    private final Map<EvaluationSheet, Integer> _sheetIndexesBySheet;
    private final IStabilityClassifier _stabilityClassifier;
    private final UDFFinder _udfFinder;
    private final EvaluationWorkbook _workbook;
    private int _workbookIx;

    public WorkbookEvaluator(EvaluationWorkbook workbook, IStabilityClassifier stabilityClassifier, UDFFinder udfFinder) {
        this(workbook, (IEvaluationListener) null, stabilityClassifier, udfFinder);
    }

    WorkbookEvaluator(EvaluationWorkbook workbook, IEvaluationListener evaluationListener, IStabilityClassifier stabilityClassifier, UDFFinder udfFinder) {
        this._workbook = workbook;
        this._evaluationListener = evaluationListener;
        this._cache = new EvaluationCache(evaluationListener);
        this._sheetIndexesBySheet = new IdentityHashMap();
        this._sheetIndexesByName = new IdentityHashMap();
        this._collaboratingWorkbookEnvironment = CollaboratingWorkbooksEnvironment.EMPTY;
        this._workbookIx = 0;
        this._stabilityClassifier = stabilityClassifier;
        this._udfFinder = udfFinder == null ? UDFFinder.DEFAULT : udfFinder;
    }

    /* access modifiers changed from: package-private */
    public String getSheetName(int sheetIndex) {
        return this._workbook.getSheetName(sheetIndex);
    }

    /* access modifiers changed from: package-private */
    public EvaluationSheet getSheet(int sheetIndex) {
        return this._workbook.getSheet(sheetIndex);
    }

    /* access modifiers changed from: package-private */
    public EvaluationName getName(String name, int sheetIndex) {
        NamePtg namePtg = null;
        EvaluationWorkbook evaluationWorkbook = this._workbook;
        if (evaluationWorkbook instanceof HSSFEvaluationWorkbook) {
            namePtg = ((HSSFEvaluationWorkbook) evaluationWorkbook).getName(name, sheetIndex).createPtg();
        }
        if (namePtg == null) {
            return null;
        }
        return this._workbook.getName(namePtg);
    }

    private static boolean isDebugLogEnabled() {
        return false;
    }

    private static void logDebug(String s) {
        if (isDebugLogEnabled()) {
            System.out.println(s);
        }
    }

    /* access modifiers changed from: package-private */
    public void attachToEnvironment(CollaboratingWorkbooksEnvironment collaboratingWorkbooksEnvironment, EvaluationCache cache, int workbookIx) {
        this._collaboratingWorkbookEnvironment = collaboratingWorkbooksEnvironment;
        this._cache = cache;
        this._workbookIx = workbookIx;
    }

    /* access modifiers changed from: package-private */
    public CollaboratingWorkbooksEnvironment getEnvironment() {
        return this._collaboratingWorkbookEnvironment;
    }

    /* access modifiers changed from: package-private */
    public void detachFromEnvironment() {
        this._collaboratingWorkbookEnvironment = CollaboratingWorkbooksEnvironment.EMPTY;
        this._cache = new EvaluationCache(this._evaluationListener);
        this._workbookIx = 0;
    }

    /* access modifiers changed from: package-private */
    public WorkbookEvaluator getOtherWorkbookEvaluator(String workbookName) throws CollaboratingWorkbooksEnvironment.WorkbookNotFoundException {
        return this._collaboratingWorkbookEnvironment.getWorkbookEvaluator(workbookName);
    }

    /* access modifiers changed from: package-private */
    public IEvaluationListener getEvaluationListener() {
        return this._evaluationListener;
    }

    public void clearAllCachedResultValues() {
        this._cache.clear();
        this._sheetIndexesBySheet.clear();
    }

    public void notifyUpdateCell(EvaluationCell cell) {
        this._cache.notifyUpdateCell(this._workbookIx, getSheetIndex(cell.getSheet()), cell);
    }

    public void notifyDeleteCell(EvaluationCell cell) {
        this._cache.notifyDeleteCell(this._workbookIx, getSheetIndex(cell.getSheet()), cell);
    }

    private int getSheetIndex(EvaluationSheet sheet) {
        Integer result = this._sheetIndexesBySheet.get(sheet);
        if (result == null) {
            int sheetIndex = this._workbook.getSheetIndex(sheet);
            if (sheetIndex >= 0) {
                result = Integer.valueOf(sheetIndex);
                this._sheetIndexesBySheet.put(sheet, result);
            } else {
                throw new RuntimeException("Specified sheet from a different book");
            }
        }
        return result.intValue();
    }

    public ValueEval evaluate(EvaluationCell srcCell) {
        return evaluateAny(srcCell, getSheetIndex(srcCell.getSheet()), srcCell.getRowIndex(), srcCell.getColumnIndex(), new EvaluationTracker(this._cache));
    }

    /* access modifiers changed from: package-private */
    public int getSheetIndex(String sheetName) {
        Integer result = this._sheetIndexesByName.get(sheetName);
        if (result == null) {
            int sheetIndex = this._workbook.getSheetIndex(sheetName);
            if (sheetIndex < 0) {
                return -1;
            }
            result = Integer.valueOf(sheetIndex);
            this._sheetIndexesByName.put(sheetName, result);
        }
        return result.intValue();
    }

    /* access modifiers changed from: package-private */
    public int getSheetIndexByExternIndex(int externSheetIndex) {
        return this._workbook.convertFromExternSheetIndex(externSheetIndex);
    }

    private ValueEval evaluateAny(EvaluationCell srcCell, int sheetIndex, int rowIndex, int columnIndex, EvaluationTracker tracker) {
        boolean shouldCellDependencyBeRecorded;
        ValueEval result;
        EvaluationCell evaluationCell = srcCell;
        int i = sheetIndex;
        int i2 = rowIndex;
        int i3 = columnIndex;
        EvaluationTracker evaluationTracker = tracker;
        IStabilityClassifier iStabilityClassifier = this._stabilityClassifier;
        boolean z = true;
        if (iStabilityClassifier != null && iStabilityClassifier.isCellFinal(i, i2, i3)) {
            z = false;
        }
        boolean shouldCellDependencyBeRecorded2 = z;
        if (evaluationCell == null) {
            shouldCellDependencyBeRecorded = shouldCellDependencyBeRecorded2;
        } else if (srcCell.getCellType() != 2) {
            shouldCellDependencyBeRecorded = shouldCellDependencyBeRecorded2;
        } else {
            FormulaCellCacheEntry cce = this._cache.getOrCreateFormulaCellEntry(evaluationCell);
            if (shouldCellDependencyBeRecorded2 || cce.isInputSensitive()) {
                evaluationTracker.acceptFormulaDependency(cce);
            }
            IEvaluationListener evalListener = this._evaluationListener;
            if (cce.getValue() != null) {
                IEvaluationListener evalListener2 = evalListener;
                if (evalListener2 != null) {
                    evalListener2.onCacheHit(i, i2, i3, cce.getValue());
                }
                return cce.getValue();
            } else if (!evaluationTracker.startEvaluate(cce)) {
                return ErrorEval.CIRCULAR_REF_ERROR;
            } else {
                boolean z2 = shouldCellDependencyBeRecorded2;
                IEvaluationListener evalListener3 = evalListener;
                OperationEvaluationContext ec = new OperationEvaluationContext(this, this._workbook, sheetIndex, rowIndex, columnIndex, tracker);
                try {
                    Ptg[] ptgs = this._workbook.getFormulaTokens(evaluationCell);
                    if (evalListener3 == null) {
                        result = evaluateFormula(ec, ptgs);
                    } else {
                        evalListener3.onStartEvaluate(evaluationCell, cce);
                        result = evaluateFormula(ec, ptgs);
                        try {
                            evalListener3.onEndEvaluate(cce, result);
                        } catch (NotImplementedException e) {
                            e = e;
                            try {
                                throw addExceptionInfo(e, i, i2, i3);
                            } catch (Throwable th) {
                                e = th;
                                evaluationTracker.endEvaluate(cce);
                                throw e;
                            }
                        }
                    }
                    evaluationTracker.updateCacheResult(result);
                    evaluationTracker.endEvaluate(cce);
                    if (isDebugLogEnabled()) {
                        String sheetName = getSheetName(i);
                        CellReference cr = new CellReference(i2, i3);
                        logDebug("Evaluated " + sheetName + "!" + cr.formatAsString() + " to " + result.toString());
                    }
                    return result;
                } catch (NotImplementedException e2) {
                    e = e2;
                    throw addExceptionInfo(e, i, i2, i3);
                } catch (Throwable th2) {
                    e = th2;
                    evaluationTracker.endEvaluate(cce);
                    throw e;
                }
            }
        }
        ValueEval result2 = getValueFromNonFormulaCell(srcCell);
        if (shouldCellDependencyBeRecorded) {
            tracker.acceptPlainValueDependency(this._workbookIx, sheetIndex, rowIndex, columnIndex, result2);
        }
        return result2;
    }

    private NotImplementedException addExceptionInfo(NotImplementedException inner, int sheetIndex, int rowIndex, int columnIndex) {
        try {
            CellReference cellReference = new CellReference(this._workbook.getSheetName(sheetIndex), rowIndex, columnIndex, false, false);
            return new NotImplementedException("Error evaluating cell " + cellReference.formatAsString(), inner);
        } catch (Exception e) {
            e.printStackTrace();
            return inner;
        }
    }

    static ValueEval getValueFromNonFormulaCell(EvaluationCell cell) {
        if (cell == null) {
            return BlankEval.instance;
        }
        int cellType = cell.getCellType();
        if (cellType == 0) {
            return new NumberEval(cell.getNumericCellValue());
        }
        if (cellType == 1) {
            return new StringEval(cell.getStringCellValue());
        }
        if (cellType == 3) {
            return BlankEval.instance;
        }
        if (cellType == 4) {
            return BoolEval.valueOf(cell.getBooleanCellValue());
        }
        if (cellType == 5) {
            return ErrorEval.valueOf(cell.getErrorCellValue());
        }
        throw new RuntimeException("Unexpected cell type (" + cellType + ")");
    }

    /* access modifiers changed from: package-private */
    public ValueEval evaluateFormula(OperationEvaluationContext operationEvaluationContext, Ptg[] ptgArr) {
        ValueEval valueEval;
        int i;
        Stack stack = new Stack();
        int length = ptgArr.length;
        int i2 = 0;
        while (i2 < length) {
            OperationPtg operationPtg = ptgArr[i2];
            if (operationPtg instanceof AttrPtg) {
                AttrPtg attrPtg = (AttrPtg) operationPtg;
                if (attrPtg.isSum()) {
                    operationPtg = FuncVarPtg.SUM;
                }
                if (attrPtg.isOptimizedChoose()) {
                    ValueEval valueEval2 = (ValueEval) stack.pop();
                    int[] jumpTable = attrPtg.getJumpTable();
                    int length2 = jumpTable.length;
                    try {
                        int evaluateFirstArg = Choose.evaluateFirstArg(valueEval2, operationEvaluationContext.getRowIndex(), operationEvaluationContext.getColumnIndex());
                        if (evaluateFirstArg >= 1) {
                            if (evaluateFirstArg <= length2) {
                                i = jumpTable[evaluateFirstArg - 1];
                                i2 += countTokensToBeSkipped(ptgArr, i2, i - ((length2 * 2) + 2));
                            }
                        }
                        stack.push(ErrorEval.VALUE_INVALID);
                        i = attrPtg.getChooseFuncOffset() + 4;
                    } catch (EvaluationException e) {
                        stack.push(e.getErrorEval());
                        i = attrPtg.getChooseFuncOffset() + 4;
                    }
                    i2 += countTokensToBeSkipped(ptgArr, i2, i - ((length2 * 2) + 2));
                } else if (attrPtg.isOptimizedIf()) {
                    try {
                        if (!IfFunc.evaluateFirstArg((ValueEval) stack.pop(), operationEvaluationContext.getRowIndex(), operationEvaluationContext.getColumnIndex())) {
                            i2 += countTokensToBeSkipped(ptgArr, i2, attrPtg.getData());
                            int i3 = i2 + 1;
                            Ptg ptg = ptgArr[i3];
                            if ((ptgArr[i2] instanceof AttrPtg) && (ptg instanceof FuncVarPtg)) {
                                stack.push(BoolEval.FALSE);
                                i2 = i3;
                            }
                        }
                    } catch (EvaluationException e2) {
                        stack.push(e2.getErrorEval());
                        int countTokensToBeSkipped = i2 + countTokensToBeSkipped(ptgArr, i2, attrPtg.getData());
                        i2 = countTokensToBeSkipped + countTokensToBeSkipped(ptgArr, countTokensToBeSkipped, ptgArr[countTokensToBeSkipped].getData() + 1);
                    }
                } else if (attrPtg.isSkip()) {
                    i2 += countTokensToBeSkipped(ptgArr, i2, attrPtg.getData() + 1);
                    if (stack.peek() == MissingArgEval.instance) {
                        stack.pop();
                        stack.push(BlankEval.instance);
                    }
                }
                i2++;
            }
            if (!(operationPtg instanceof ControlPtg) && !(operationPtg instanceof MemFuncPtg) && !(operationPtg instanceof MemAreaPtg) && !(operationPtg instanceof MemErrPtg)) {
                if (operationPtg instanceof OperationPtg) {
                    OperationPtg operationPtg2 = operationPtg;
                    if (operationPtg2 instanceof UnionPtg) {
                        continue;
                    } else {
                        int numberOfOperands = operationPtg2.getNumberOfOperands();
                        ValueEval[] valueEvalArr = new ValueEval[numberOfOperands];
                        for (int i4 = numberOfOperands - 1; i4 >= 0; i4--) {
                            valueEvalArr[i4] = (ValueEval) stack.pop();
                        }
                        valueEval = OperationEvaluatorFactory.evaluate(operationPtg2, valueEvalArr, operationEvaluationContext);
                    }
                } else {
                    valueEval = getEvalForPtg(operationPtg, operationEvaluationContext);
                }
                if (valueEval != null) {
                    stack.push(valueEval);
                } else {
                    throw new RuntimeException("Evaluation result must not be null");
                }
            }
            i2++;
        }
        ValueEval valueEval3 = (ValueEval) stack.pop();
        if (stack.isEmpty()) {
            return dereferenceResult(valueEval3, operationEvaluationContext.getRowIndex(), operationEvaluationContext.getColumnIndex());
        }
        throw new IllegalStateException("evaluation stack not empty");
    }

    private static int countTokensToBeSkipped(Ptg[] ptgs, int startIndex, int distInBytes) {
        int remBytes = distInBytes;
        int index = startIndex;
        while (remBytes != 0) {
            index++;
            remBytes -= ptgs[index].getSize();
            if (remBytes < 0) {
                throw new RuntimeException("Bad skip distance (wrong token size calculation).");
            } else if (index >= ptgs.length) {
                throw new RuntimeException("Skip distance too far (ran out of formula tokens).");
            }
        }
        return index - startIndex;
    }

    public static ValueEval dereferenceResult(ValueEval evaluationResult, int srcRowNum, int srcColNum) {
        try {
            ValueEval value = OperandResolver.getSingleValue(evaluationResult, srcRowNum, srcColNum);
            if (value == BlankEval.instance) {
                return NumberEval.ZERO;
            }
            return value;
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    private ValueEval getEvalForPtg(Ptg ptg, OperationEvaluationContext ec) {
        if (ptg instanceof NamePtg) {
            EvaluationName nameRecord = this._workbook.getName((NamePtg) ptg);
            if (nameRecord.isFunctionName()) {
                return new NameEval(nameRecord.getNameText());
            }
            if (nameRecord.hasFormula()) {
                return evaluateNameFormula(nameRecord.getNameDefinition(), ec);
            }
            throw new RuntimeException("Don't now how to evalate name '" + nameRecord.getNameText() + "'");
        } else if (ptg instanceof NameXPtg) {
            return ec.getNameXEval((NameXPtg) ptg);
        } else {
            if (ptg instanceof IntPtg) {
                return new NumberEval((double) ((IntPtg) ptg).getValue());
            }
            if (ptg instanceof NumberPtg) {
                return new NumberEval(((NumberPtg) ptg).getValue());
            }
            if (ptg instanceof StringPtg) {
                return new StringEval(((StringPtg) ptg).getValue());
            }
            if (ptg instanceof BoolPtg) {
                return BoolEval.valueOf(((BoolPtg) ptg).getValue());
            }
            if (ptg instanceof ErrPtg) {
                return ErrorEval.valueOf(((ErrPtg) ptg).getErrorCode());
            }
            if (ptg instanceof MissingArgPtg) {
                return MissingArgEval.instance;
            }
            if ((ptg instanceof AreaErrPtg) || (ptg instanceof RefErrorPtg) || (ptg instanceof DeletedArea3DPtg) || (ptg instanceof DeletedRef3DPtg)) {
                return ErrorEval.REF_INVALID;
            }
            if (ptg instanceof Ref3DPtg) {
                Ref3DPtg rptg = (Ref3DPtg) ptg;
                return ec.getRef3DEval(rptg.getRow(), rptg.getColumn(), rptg.getExternSheetIndex());
            } else if (ptg instanceof Area3DPtg) {
                Area3DPtg aptg = (Area3DPtg) ptg;
                return ec.getArea3DEval(aptg.getFirstRow(), aptg.getFirstColumn(), aptg.getLastRow(), aptg.getLastColumn(), aptg.getExternSheetIndex());
            } else if (ptg instanceof RefPtg) {
                RefPtg rptg2 = (RefPtg) ptg;
                return ec.getRefEval(rptg2.getRow(), rptg2.getColumn());
            } else if (ptg instanceof AreaPtg) {
                AreaPtg aptg2 = (AreaPtg) ptg;
                return ec.getAreaEval(aptg2.getFirstRow(), aptg2.getFirstColumn(), aptg2.getLastRow(), aptg2.getLastColumn());
            } else if (ptg instanceof UnknownPtg) {
                throw new RuntimeException("UnknownPtg not allowed");
            } else if (ptg instanceof ExpPtg) {
                throw new RuntimeException("ExpPtg currently not supported");
            } else {
                throw new RuntimeException("Unexpected ptg class (" + ptg.getClass().getName() + ")");
            }
        }
    }

    /* access modifiers changed from: package-private */
    public ValueEval evaluateNameFormula(Ptg[] ptgs, OperationEvaluationContext ec) {
        if (ptgs.length <= 1) {
            return getEvalForPtg(ptgs[0], ec);
        }
        throw new RuntimeException("Complex name formulas not supported yet");
    }

    /* access modifiers changed from: package-private */
    public ValueEval evaluateReference(EvaluationSheet sheet, int sheetIndex, int rowIndex, int columnIndex, EvaluationTracker tracker) {
        return evaluateAny(sheet.getCell(rowIndex, columnIndex), sheetIndex, rowIndex, columnIndex, tracker);
    }

    public FreeRefFunction findUserDefinedFunction(String functionName) {
        return this._udfFinder.findFunction(functionName);
    }
}
