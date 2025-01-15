package org.apache.poi.hssf.usermodel;

import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.ss.formula.EvaluationSheet;

final class HSSFEvaluationSheet implements EvaluationSheet {
    private final HSSFSheet _hs;

    public HSSFEvaluationSheet(HSSFSheet hs) {
        this._hs = hs;
    }

    public HSSFSheet getHSSFSheet() {
        return this._hs;
    }

    public EvaluationCell getCell(int rowIndex, int columnIndex) {
        HSSFCell cell;
        HSSFRow row = this._hs.getRow(rowIndex);
        if (row == null || (cell = row.getCell(columnIndex)) == null) {
            return null;
        }
        return new HSSFEvaluationCell(cell, this);
    }
}
