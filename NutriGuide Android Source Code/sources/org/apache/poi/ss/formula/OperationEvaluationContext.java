package org.apache.poi.ss.formula;

import org.apache.poi.hssf.record.formula.Area3DPtg;
import org.apache.poi.hssf.record.formula.NameXPtg;
import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.hssf.record.formula.Ref3DPtg;
import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.formula.eval.NameXEval;
import org.apache.poi.hssf.record.formula.eval.ValueEval;
import org.apache.poi.hssf.record.formula.functions.FreeRefFunction;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.CollaboratingWorkbooksEnvironment;
import org.apache.poi.ss.formula.EvaluationWorkbook;
import org.apache.poi.ss.util.CellReference;

public final class OperationEvaluationContext {
    public static final FreeRefFunction UDF = UserDefinedFunction.instance;
    private final WorkbookEvaluator _bookEvaluator;
    private final int _columnIndex;
    private final int _rowIndex;
    private final int _sheetIndex;
    private final EvaluationTracker _tracker;
    private final EvaluationWorkbook _workbook;

    public OperationEvaluationContext(WorkbookEvaluator bookEvaluator, EvaluationWorkbook workbook, int sheetIndex, int srcRowNum, int srcColNum, EvaluationTracker tracker) {
        this._bookEvaluator = bookEvaluator;
        this._workbook = workbook;
        this._sheetIndex = sheetIndex;
        this._rowIndex = srcRowNum;
        this._columnIndex = srcColNum;
        this._tracker = tracker;
    }

    public EvaluationWorkbook getWorkbook() {
        return this._workbook;
    }

    public int getRowIndex() {
        return this._rowIndex;
    }

    public int getColumnIndex() {
        return this._columnIndex;
    }

    /* access modifiers changed from: package-private */
    public SheetRefEvaluator createExternSheetRefEvaluator(ExternSheetReferenceToken ptg) {
        return createExternSheetRefEvaluator(ptg.getExternSheetIndex());
    }

    /* access modifiers changed from: package-private */
    public SheetRefEvaluator createExternSheetRefEvaluator(int externSheetIndex) {
        WorkbookEvaluator targetEvaluator;
        int otherSheetIndex;
        EvaluationWorkbook.ExternalSheet externalSheet = this._workbook.getExternalSheet(externSheetIndex);
        if (externalSheet == null) {
            otherSheetIndex = this._workbook.convertFromExternSheetIndex(externSheetIndex);
            targetEvaluator = this._bookEvaluator;
        } else {
            String workbookName = externalSheet.getWorkbookName();
            try {
                targetEvaluator = this._bookEvaluator.getOtherWorkbookEvaluator(workbookName);
                int otherSheetIndex2 = targetEvaluator.getSheetIndex(externalSheet.getSheetName());
                if (otherSheetIndex2 >= 0) {
                    otherSheetIndex = otherSheetIndex2;
                } else {
                    throw new RuntimeException("Invalid sheet name '" + externalSheet.getSheetName() + "' in bool '" + workbookName + "'.");
                }
            } catch (CollaboratingWorkbooksEnvironment.WorkbookNotFoundException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return new SheetRefEvaluator(targetEvaluator, this._tracker, otherSheetIndex);
    }

    private SheetRefEvaluator createExternSheetRefEvaluator(String workbookName, String sheetName) {
        WorkbookEvaluator targetEvaluator;
        if (workbookName == null) {
            targetEvaluator = this._bookEvaluator;
        } else if (sheetName != null) {
            try {
                targetEvaluator = this._bookEvaluator.getOtherWorkbookEvaluator(workbookName);
            } catch (CollaboratingWorkbooksEnvironment.WorkbookNotFoundException e) {
                return null;
            }
        } else {
            throw new IllegalArgumentException("sheetName must not be null if workbookName is provided");
        }
        int otherSheetIndex = sheetName == null ? this._sheetIndex : targetEvaluator.getSheetIndex(sheetName);
        if (otherSheetIndex < 0) {
            return null;
        }
        return new SheetRefEvaluator(targetEvaluator, this._tracker, otherSheetIndex);
    }

    public SheetRefEvaluator getRefEvaluatorForCurrentSheet() {
        return new SheetRefEvaluator(this._bookEvaluator, this._tracker, this._sheetIndex);
    }

    public ValueEval getDynamicReference(String workbookName, String sheetName, String refStrPart1, String refStrPart2, boolean isA1Style) {
        int firstCol;
        int firstRow;
        int lastCol;
        int lastRow;
        String str = refStrPart1;
        String str2 = refStrPart2;
        if (isA1Style) {
            SheetRefEvaluator sre = createExternSheetRefEvaluator(workbookName, sheetName);
            if (sre == null) {
                return ErrorEval.REF_INVALID;
            }
            SpreadsheetVersion ssVersion = ((FormulaParsingWorkbook) this._workbook).getSpreadsheetVersion();
            CellReference.NameType part1refType = classifyCellReference(str, ssVersion);
            int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$util$CellReference$NameType[part1refType.ordinal()];
            if (i == 1) {
                return ErrorEval.REF_INVALID;
            }
            if (i == 2) {
                EvaluationName nm = ((FormulaParsingWorkbook) this._workbook).getName(str, this._sheetIndex);
                if (nm.isRange()) {
                    return this._bookEvaluator.evaluateNameFormula(nm.getNameDefinition(), this);
                }
                throw new RuntimeException("Specified name '" + str + "' is not a range as expected.");
            } else if (str2 == null) {
                int i2 = AnonymousClass1.$SwitchMap$org$apache$poi$ss$util$CellReference$NameType[part1refType.ordinal()];
                if (i2 == 3 || i2 == 4) {
                    return ErrorEval.REF_INVALID;
                }
                if (i2 == 5) {
                    CellReference cr = new CellReference(str);
                    return new LazyRefEval(cr.getRow(), cr.getCol(), sre);
                }
                throw new IllegalStateException("Unexpected reference classification of '" + str + "'.");
            } else {
                CellReference.NameType part2refType = classifyCellReference(str, ssVersion);
                int i3 = AnonymousClass1.$SwitchMap$org$apache$poi$ss$util$CellReference$NameType[part2refType.ordinal()];
                if (i3 == 1) {
                    return ErrorEval.REF_INVALID;
                }
                if (i3 == 2) {
                    throw new RuntimeException("Cannot evaluate '" + str + "'. Indirect evaluation of defined names not supported yet");
                } else if (part2refType != part1refType) {
                    return ErrorEval.REF_INVALID;
                } else {
                    int i4 = AnonymousClass1.$SwitchMap$org$apache$poi$ss$util$CellReference$NameType[part1refType.ordinal()];
                    if (i4 == 3) {
                        int lastRow2 = ssVersion.getLastRowIndex();
                        int firstCol2 = parseColRef(refStrPart1);
                        lastRow = lastRow2;
                        lastCol = parseColRef(refStrPart2);
                        firstRow = 0;
                        firstCol = firstCol2;
                    } else if (i4 == 4) {
                        int lastCol2 = ssVersion.getLastColumnIndex();
                        int firstRow2 = parseRowRef(refStrPart1);
                        lastRow = parseRowRef(refStrPart2);
                        lastCol = lastCol2;
                        firstRow = firstRow2;
                        firstCol = 0;
                    } else if (i4 == 5) {
                        CellReference cr2 = new CellReference(str);
                        int firstRow3 = cr2.getRow();
                        int col = cr2.getCol();
                        CellReference cr3 = new CellReference(str2);
                        lastRow = cr3.getRow();
                        lastCol = cr3.getCol();
                        firstRow = firstRow3;
                        firstCol = col;
                    } else {
                        throw new IllegalStateException("Unexpected reference classification of '" + str + "'.");
                    }
                    return new LazyAreaEval(firstRow, firstCol, lastRow, lastCol, sre);
                }
            }
        } else {
            throw new RuntimeException("R1C1 style not supported yet");
        }
    }

    /* renamed from: org.apache.poi.ss.formula.OperationEvaluationContext$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$ss$util$CellReference$NameType;

        static {
            int[] iArr = new int[CellReference.NameType.values().length];
            $SwitchMap$org$apache$poi$ss$util$CellReference$NameType = iArr;
            try {
                iArr[CellReference.NameType.BAD_CELL_OR_NAMED_RANGE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$util$CellReference$NameType[CellReference.NameType.NAMED_RANGE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$util$CellReference$NameType[CellReference.NameType.COLUMN.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$util$CellReference$NameType[CellReference.NameType.ROW.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$util$CellReference$NameType[CellReference.NameType.CELL.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    private static int parseRowRef(String refStrPart) {
        return CellReference.convertColStringToIndex(refStrPart);
    }

    private static int parseColRef(String refStrPart) {
        return Integer.parseInt(refStrPart) - 1;
    }

    private static CellReference.NameType classifyCellReference(String str, SpreadsheetVersion ssVersion) {
        if (str.length() < 1) {
            return CellReference.NameType.BAD_CELL_OR_NAMED_RANGE;
        }
        return CellReference.classifyCellReference(str, ssVersion);
    }

    public FreeRefFunction findUserDefinedFunction(String functionName) {
        return this._bookEvaluator.findUserDefinedFunction(functionName);
    }

    public ValueEval getRefEval(int rowIndex, int columnIndex) {
        return new LazyRefEval(rowIndex, columnIndex, getRefEvaluatorForCurrentSheet());
    }

    public ValueEval getRef3DEval(int rowIndex, int columnIndex, int extSheetIndex) {
        return new LazyRefEval(rowIndex, columnIndex, createExternSheetRefEvaluator(extSheetIndex));
    }

    public ValueEval getAreaEval(int firstRowIndex, int firstColumnIndex, int lastRowIndex, int lastColumnIndex) {
        return new LazyAreaEval(firstRowIndex, firstColumnIndex, lastRowIndex, lastColumnIndex, getRefEvaluatorForCurrentSheet());
    }

    public ValueEval getArea3DEval(int firstRowIndex, int firstColumnIndex, int lastRowIndex, int lastColumnIndex, int extSheetIndex) {
        return new LazyAreaEval(firstRowIndex, firstColumnIndex, lastRowIndex, lastColumnIndex, createExternSheetRefEvaluator(extSheetIndex));
    }

    public ValueEval getNameXEval(NameXPtg nameXPtg) {
        EvaluationWorkbook.ExternalSheet externSheet = this._workbook.getExternalSheet(nameXPtg.getSheetRefIndex());
        if (externSheet == null) {
            return new NameXEval(nameXPtg);
        }
        NameXPtg nameXPtg2 = nameXPtg;
        String workbookName = externSheet.getWorkbookName();
        EvaluationWorkbook.ExternalName externName = this._workbook.getExternalName(nameXPtg.getSheetRefIndex(), nameXPtg.getNameIndex());
        try {
            WorkbookEvaluator refWorkbookEvaluator = this._bookEvaluator.getOtherWorkbookEvaluator(workbookName);
            EvaluationName evaluationName = refWorkbookEvaluator.getName(externName.getName(), externName.getIx() - 1);
            if (evaluationName != null && evaluationName.hasFormula()) {
                if (evaluationName.getNameDefinition().length <= 1) {
                    Ptg ptg = evaluationName.getNameDefinition()[0];
                    if (ptg instanceof Ref3DPtg) {
                        Ref3DPtg ref3D = (Ref3DPtg) ptg;
                        return new LazyRefEval(ref3D.getRow(), ref3D.getColumn(), createExternSheetRefEvaluator(workbookName, refWorkbookEvaluator.getSheetName(refWorkbookEvaluator.getSheetIndexByExternIndex(ref3D.getExternSheetIndex()))));
                    } else if (ptg instanceof Area3DPtg) {
                        Area3DPtg area3D = (Area3DPtg) ptg;
                        return new LazyAreaEval(area3D.getFirstRow(), area3D.getFirstColumn(), area3D.getLastRow(), area3D.getLastColumn(), createExternSheetRefEvaluator(workbookName, refWorkbookEvaluator.getSheetName(refWorkbookEvaluator.getSheetIndexByExternIndex(area3D.getExternSheetIndex()))));
                    }
                } else {
                    throw new RuntimeException("Complex name formulas not supported yet");
                }
            }
            return ErrorEval.REF_INVALID;
        } catch (CollaboratingWorkbooksEnvironment.WorkbookNotFoundException e) {
            return ErrorEval.REF_INVALID;
        }
    }
}
