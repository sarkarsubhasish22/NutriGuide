package org.apache.poi.ss.formula;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.poi.hssf.record.formula.eval.ValueEval;
import org.apache.poi.ss.formula.FormulaUsedBlankCellSet;

final class FormulaCellCacheEntry extends CellCacheEntry {
    private CellCacheEntry[] _sensitiveInputCells;
    private FormulaUsedBlankCellSet _usedBlankCellGroup;

    public boolean isInputSensitive() {
        CellCacheEntry[] cellCacheEntryArr = this._sensitiveInputCells;
        if (cellCacheEntryArr != null && cellCacheEntryArr.length > 0) {
            return true;
        }
        FormulaUsedBlankCellSet formulaUsedBlankCellSet = this._usedBlankCellGroup;
        if (formulaUsedBlankCellSet != null && !formulaUsedBlankCellSet.isEmpty()) {
            return true;
        }
        return false;
    }

    public void setSensitiveInputCells(CellCacheEntry[] sensitiveInputCells) {
        changeConsumingCells(sensitiveInputCells == null ? CellCacheEntry.EMPTY_ARRAY : sensitiveInputCells);
        this._sensitiveInputCells = sensitiveInputCells;
    }

    public void clearFormulaEntry() {
        CellCacheEntry[] usedCells = this._sensitiveInputCells;
        if (usedCells != null) {
            for (int i = usedCells.length - 1; i >= 0; i--) {
                usedCells[i].clearConsumingCell(this);
            }
        }
        this._sensitiveInputCells = null;
        clearValue();
    }

    private void changeConsumingCells(CellCacheEntry[] usedCells) {
        Set<CellCacheEntry> usedSet;
        CellCacheEntry[] prevUsedCells = this._sensitiveInputCells;
        for (CellCacheEntry addConsumingCell : usedCells) {
            addConsumingCell.addConsumingCell(this);
        }
        if (prevUsedCells != null && (nPrevUsed = prevUsedCells.length) >= 1) {
            if (nUsed < 1) {
                usedSet = Collections.emptySet();
            } else {
                usedSet = new HashSet<>((nUsed * 3) / 2);
                for (CellCacheEntry add : usedCells) {
                    usedSet.add(add);
                }
            }
            for (CellCacheEntry prevUsed : prevUsedCells) {
                if (!usedSet.contains(prevUsed)) {
                    prevUsed.clearConsumingCell(this);
                }
            }
        }
    }

    public void updateFormulaResult(ValueEval result, CellCacheEntry[] sensitiveInputCells, FormulaUsedBlankCellSet usedBlankAreas) {
        updateValue(result);
        setSensitiveInputCells(sensitiveInputCells);
        this._usedBlankCellGroup = usedBlankAreas;
    }

    public void notifyUpdatedBlankCell(FormulaUsedBlankCellSet.BookSheetKey bsk, int rowIndex, int columnIndex, IEvaluationListener evaluationListener) {
        FormulaUsedBlankCellSet formulaUsedBlankCellSet = this._usedBlankCellGroup;
        if (formulaUsedBlankCellSet != null && formulaUsedBlankCellSet.containsCell(bsk, rowIndex, columnIndex)) {
            clearFormulaEntry();
            recurseClearCachedFormulaResults(evaluationListener);
        }
    }
}
