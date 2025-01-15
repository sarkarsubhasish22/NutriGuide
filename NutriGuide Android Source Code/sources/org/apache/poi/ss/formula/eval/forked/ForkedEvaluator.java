package org.apache.poi.ss.formula.eval.forked;

import org.apache.poi.hssf.record.formula.eval.BoolEval;
import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.formula.eval.NumberEval;
import org.apache.poi.hssf.record.formula.eval.StringEval;
import org.apache.poi.hssf.record.formula.eval.ValueEval;
import org.apache.poi.hssf.record.formula.udf.UDFFinder;
import org.apache.poi.hssf.usermodel.HSSFEvaluationWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.CollaboratingWorkbooksEnvironment;
import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.ss.formula.EvaluationWorkbook;
import org.apache.poi.ss.formula.IStabilityClassifier;
import org.apache.poi.ss.formula.WorkbookEvaluator;
import org.apache.poi.ss.usermodel.Workbook;

public final class ForkedEvaluator {
    private WorkbookEvaluator _evaluator;
    private ForkedEvaluationWorkbook _sewb;

    private ForkedEvaluator(EvaluationWorkbook masterWorkbook, IStabilityClassifier stabilityClassifier, UDFFinder udfFinder) {
        ForkedEvaluationWorkbook forkedEvaluationWorkbook = new ForkedEvaluationWorkbook(masterWorkbook);
        this._sewb = forkedEvaluationWorkbook;
        this._evaluator = new WorkbookEvaluator(forkedEvaluationWorkbook, stabilityClassifier, udfFinder);
    }

    private static EvaluationWorkbook createEvaluationWorkbook(Workbook wb) {
        if (wb instanceof HSSFWorkbook) {
            return HSSFEvaluationWorkbook.create((HSSFWorkbook) wb);
        }
        throw new IllegalArgumentException("Unexpected workbook type (" + wb.getClass().getName() + ")");
    }

    public static ForkedEvaluator create(Workbook wb, IStabilityClassifier stabilityClassifier) {
        return create(wb, stabilityClassifier, (UDFFinder) null);
    }

    public static ForkedEvaluator create(Workbook wb, IStabilityClassifier stabilityClassifier, UDFFinder udfFinder) {
        return new ForkedEvaluator(createEvaluationWorkbook(wb), stabilityClassifier, udfFinder);
    }

    public void updateCell(String sheetName, int rowIndex, int columnIndex, ValueEval value) {
        ForkedEvaluationCell cell = this._sewb.getOrCreateUpdatableCell(sheetName, rowIndex, columnIndex);
        cell.setValue(value);
        this._evaluator.notifyUpdateCell(cell);
    }

    public void copyUpdatedCells(Workbook workbook) {
        this._sewb.copyUpdatedCells(workbook);
    }

    public ValueEval evaluate(String sheetName, int rowIndex, int columnIndex) {
        EvaluationCell cell = this._sewb.getEvaluationCell(sheetName, rowIndex, columnIndex);
        int cellType = cell.getCellType();
        if (cellType == 0) {
            return new NumberEval(cell.getNumericCellValue());
        }
        if (cellType == 1) {
            return new StringEval(cell.getStringCellValue());
        }
        if (cellType == 2) {
            return this._evaluator.evaluate(cell);
        }
        if (cellType == 3) {
            return null;
        }
        if (cellType == 4) {
            return BoolEval.valueOf(cell.getBooleanCellValue());
        }
        if (cellType == 5) {
            return ErrorEval.valueOf(cell.getErrorCellValue());
        }
        throw new IllegalStateException("Bad cell type (" + cell.getCellType() + ")");
    }

    public static void setupEnvironment(String[] workbookNames, ForkedEvaluator[] evaluators) {
        WorkbookEvaluator[] wbEvals = new WorkbookEvaluator[evaluators.length];
        for (int i = 0; i < wbEvals.length; i++) {
            wbEvals[i] = evaluators[i]._evaluator;
        }
        CollaboratingWorkbooksEnvironment.setup(workbookNames, wbEvals);
    }
}
