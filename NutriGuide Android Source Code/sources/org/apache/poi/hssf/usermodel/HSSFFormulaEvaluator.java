package org.apache.poi.hssf.usermodel;

import java.util.Iterator;
import org.apache.poi.hssf.record.formula.eval.BoolEval;
import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.formula.eval.NumberEval;
import org.apache.poi.hssf.record.formula.eval.StringEval;
import org.apache.poi.hssf.record.formula.eval.ValueEval;
import org.apache.poi.hssf.record.formula.udf.UDFFinder;
import org.apache.poi.ss.formula.CollaboratingWorkbooksEnvironment;
import org.apache.poi.ss.formula.IStabilityClassifier;
import org.apache.poi.ss.formula.WorkbookEvaluator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;

public class HSSFFormulaEvaluator implements FormulaEvaluator {
    private WorkbookEvaluator _bookEvaluator;

    public HSSFFormulaEvaluator(HSSFSheet sheet, HSSFWorkbook workbook) {
        this(workbook);
    }

    public HSSFFormulaEvaluator(HSSFWorkbook workbook) {
        this(workbook, (IStabilityClassifier) null);
    }

    public HSSFFormulaEvaluator(HSSFWorkbook workbook, IStabilityClassifier stabilityClassifier) {
        this(workbook, stabilityClassifier, (UDFFinder) null);
    }

    private HSSFFormulaEvaluator(HSSFWorkbook workbook, IStabilityClassifier stabilityClassifier, UDFFinder udfFinder) {
        this._bookEvaluator = new WorkbookEvaluator(HSSFEvaluationWorkbook.create(workbook), stabilityClassifier, udfFinder);
    }

    public static HSSFFormulaEvaluator create(HSSFWorkbook workbook, IStabilityClassifier stabilityClassifier, UDFFinder udfFinder) {
        return new HSSFFormulaEvaluator(workbook, stabilityClassifier, udfFinder);
    }

    public static void setupEnvironment(String[] workbookNames, HSSFFormulaEvaluator[] evaluators) {
        WorkbookEvaluator[] wbEvals = new WorkbookEvaluator[evaluators.length];
        for (int i = 0; i < wbEvals.length; i++) {
            wbEvals[i] = evaluators[i]._bookEvaluator;
        }
        CollaboratingWorkbooksEnvironment.setup(workbookNames, wbEvals);
    }

    public void setCurrentRow(HSSFRow row) {
    }

    public void clearAllCachedResultValues() {
        this._bookEvaluator.clearAllCachedResultValues();
    }

    public void notifyUpdateCell(HSSFCell cell) {
        this._bookEvaluator.notifyUpdateCell(new HSSFEvaluationCell(cell));
    }

    public void notifyUpdateCell(Cell cell) {
        this._bookEvaluator.notifyUpdateCell(new HSSFEvaluationCell((HSSFCell) cell));
    }

    public void notifyDeleteCell(HSSFCell cell) {
        this._bookEvaluator.notifyDeleteCell(new HSSFEvaluationCell(cell));
    }

    public void notifyDeleteCell(Cell cell) {
        this._bookEvaluator.notifyDeleteCell(new HSSFEvaluationCell((HSSFCell) cell));
    }

    public void notifySetFormula(Cell cell) {
        this._bookEvaluator.notifyUpdateCell(new HSSFEvaluationCell((HSSFCell) cell));
    }

    public CellValue evaluate(Cell cell) {
        if (cell == null) {
            return null;
        }
        int cellType = cell.getCellType();
        if (cellType == 0) {
            return new CellValue(cell.getNumericCellValue());
        }
        if (cellType == 1) {
            return new CellValue(cell.getRichStringCellValue().getString());
        }
        if (cellType == 2) {
            return evaluateFormulaCellValue(cell);
        }
        if (cellType == 3) {
            return null;
        }
        if (cellType == 4) {
            return CellValue.valueOf(cell.getBooleanCellValue());
        }
        if (cellType == 5) {
            return CellValue.getError(cell.getErrorCellValue());
        }
        throw new IllegalStateException("Bad cell type (" + cell.getCellType() + ")");
    }

    public int evaluateFormulaCell(Cell cell) {
        if (cell == null || cell.getCellType() != 2) {
            return -1;
        }
        CellValue cv = evaluateFormulaCellValue(cell);
        setCellValue(cell, cv);
        return cv.getCellType();
    }

    public HSSFCell evaluateInCell(Cell cell) {
        if (cell == null) {
            return null;
        }
        HSSFCell result = (HSSFCell) cell;
        if (cell.getCellType() == 2) {
            CellValue cv = evaluateFormulaCellValue(cell);
            setCellValue(cell, cv);
            setCellType(cell, cv);
        }
        return result;
    }

    private static void setCellType(Cell cell, CellValue cv) {
        int cellType = cv.getCellType();
        if (cellType == 0 || cellType == 1 || cellType == 4 || cellType == 5) {
            cell.setCellType(cellType);
            return;
        }
        throw new IllegalStateException("Unexpected cell value type (" + cellType + ")");
    }

    private static void setCellValue(Cell cell, CellValue cv) {
        int cellType = cv.getCellType();
        if (cellType == 0) {
            cell.setCellValue(cv.getNumberValue());
        } else if (cellType == 1) {
            cell.setCellValue((RichTextString) new HSSFRichTextString(cv.getStringValue()));
        } else if (cellType == 4) {
            cell.setCellValue(cv.getBooleanValue());
        } else if (cellType == 5) {
            cell.setCellErrorValue(cv.getErrorValue());
        } else {
            throw new IllegalStateException("Unexpected cell value type (" + cellType + ")");
        }
    }

    public static void evaluateAllFormulaCells(HSSFWorkbook wb) {
        HSSFFormulaEvaluator evaluator = new HSSFFormulaEvaluator(wb);
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            Iterator<Row> rit = wb.getSheetAt(i).rowIterator();
            while (rit.hasNext()) {
                Iterator<Cell> cit = rit.next().cellIterator();
                while (cit.hasNext()) {
                    Cell c = cit.next();
                    if (c.getCellType() == 2) {
                        evaluator.evaluateFormulaCell(c);
                    }
                }
            }
        }
    }

    private CellValue evaluateFormulaCellValue(Cell cell) {
        ValueEval eval = this._bookEvaluator.evaluate(new HSSFEvaluationCell((HSSFCell) cell));
        if (eval instanceof NumberEval) {
            return new CellValue(((NumberEval) eval).getNumberValue());
        }
        if (eval instanceof BoolEval) {
            return CellValue.valueOf(((BoolEval) eval).getBooleanValue());
        }
        if (eval instanceof StringEval) {
            return new CellValue(((StringEval) eval).getStringValue());
        }
        if (eval instanceof ErrorEval) {
            return CellValue.getError(((ErrorEval) eval).getErrorCode());
        }
        throw new RuntimeException("Unexpected eval class (" + eval.getClass().getName() + ")");
    }
}
