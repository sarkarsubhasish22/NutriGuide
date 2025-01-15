package org.apache.poi.ss.formula.eval.forked;

import java.util.HashMap;
import java.util.Map;
import org.apache.poi.hssf.record.formula.NamePtg;
import org.apache.poi.hssf.record.formula.NameXPtg;
import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.ss.formula.EvaluationName;
import org.apache.poi.ss.formula.EvaluationSheet;
import org.apache.poi.ss.formula.EvaluationWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

final class ForkedEvaluationWorkbook implements EvaluationWorkbook {
    private final EvaluationWorkbook _masterBook;
    private final Map<String, ForkedEvaluationSheet> _sharedSheetsByName = new HashMap();

    public ForkedEvaluationWorkbook(EvaluationWorkbook master) {
        this._masterBook = master;
    }

    public ForkedEvaluationCell getOrCreateUpdatableCell(String sheetName, int rowIndex, int columnIndex) {
        return getSharedSheet(sheetName).getOrCreateUpdatableCell(rowIndex, columnIndex);
    }

    public EvaluationCell getEvaluationCell(String sheetName, int rowIndex, int columnIndex) {
        return getSharedSheet(sheetName).getCell(rowIndex, columnIndex);
    }

    private ForkedEvaluationSheet getSharedSheet(String sheetName) {
        ForkedEvaluationSheet result = this._sharedSheetsByName.get(sheetName);
        if (result != null) {
            return result;
        }
        EvaluationWorkbook evaluationWorkbook = this._masterBook;
        ForkedEvaluationSheet result2 = new ForkedEvaluationSheet(evaluationWorkbook.getSheet(evaluationWorkbook.getSheetIndex(sheetName)));
        this._sharedSheetsByName.put(sheetName, result2);
        return result2;
    }

    public void copyUpdatedCells(Workbook workbook) {
        String[] sheetNames = new String[this._sharedSheetsByName.size()];
        this._sharedSheetsByName.keySet().toArray(sheetNames);
        OrderedSheet[] oss = new OrderedSheet[sheetNames.length];
        for (int i = 0; i < sheetNames.length; i++) {
            String sheetName = sheetNames[i];
            oss[i] = new OrderedSheet(sheetName, this._masterBook.getSheetIndex(sheetName));
        }
        for (OrderedSheet sheetName2 : oss) {
            String sheetName3 = sheetName2.getSheetName();
            this._sharedSheetsByName.get(sheetName3).copyUpdatedCells(workbook.getSheet(sheetName3));
        }
    }

    public int convertFromExternSheetIndex(int externSheetIndex) {
        return this._masterBook.convertFromExternSheetIndex(externSheetIndex);
    }

    public EvaluationWorkbook.ExternalSheet getExternalSheet(int externSheetIndex) {
        return this._masterBook.getExternalSheet(externSheetIndex);
    }

    public Ptg[] getFormulaTokens(EvaluationCell cell) {
        if (!(cell instanceof ForkedEvaluationCell)) {
            return this._masterBook.getFormulaTokens(cell);
        }
        throw new RuntimeException("Updated formulas not supported yet");
    }

    public EvaluationName getName(NamePtg namePtg) {
        return this._masterBook.getName(namePtg);
    }

    public EvaluationSheet getSheet(int sheetIndex) {
        return getSharedSheet(getSheetName(sheetIndex));
    }

    public EvaluationWorkbook.ExternalName getExternalName(int externSheetIndex, int externNameIndex) {
        return this._masterBook.getExternalName(externSheetIndex, externNameIndex);
    }

    public int getSheetIndex(EvaluationSheet sheet) {
        if (sheet instanceof ForkedEvaluationSheet) {
            return ((ForkedEvaluationSheet) sheet).getSheetIndex(this._masterBook);
        }
        return this._masterBook.getSheetIndex(sheet);
    }

    public int getSheetIndex(String sheetName) {
        return this._masterBook.getSheetIndex(sheetName);
    }

    public String getSheetName(int sheetIndex) {
        return this._masterBook.getSheetName(sheetIndex);
    }

    public String resolveNameXText(NameXPtg ptg) {
        return this._masterBook.resolveNameXText(ptg);
    }

    private static final class OrderedSheet implements Comparable<OrderedSheet> {
        private final int _index;
        private final String _sheetName;

        public OrderedSheet(String sheetName, int index) {
            this._sheetName = sheetName;
            this._index = index;
        }

        public String getSheetName() {
            return this._sheetName;
        }

        public int compareTo(OrderedSheet o) {
            return this._index - o._index;
        }
    }
}
