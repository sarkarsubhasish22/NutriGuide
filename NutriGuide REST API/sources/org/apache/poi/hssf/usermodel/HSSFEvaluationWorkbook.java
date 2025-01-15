package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.record.NameRecord;
import org.apache.poi.hssf.record.aggregates.FormulaRecordAggregate;
import org.apache.poi.hssf.record.formula.NamePtg;
import org.apache.poi.hssf.record.formula.NameXPtg;
import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.ss.formula.EvaluationName;
import org.apache.poi.ss.formula.EvaluationSheet;
import org.apache.poi.ss.formula.EvaluationWorkbook;
import org.apache.poi.ss.formula.FormulaParsingWorkbook;
import org.apache.poi.ss.formula.FormulaRenderingWorkbook;
import org.apache.poi.ss.usermodel.Sheet;

public final class HSSFEvaluationWorkbook implements FormulaRenderingWorkbook, EvaluationWorkbook, FormulaParsingWorkbook {
    private final InternalWorkbook _iBook;
    private final HSSFWorkbook _uBook;

    public static HSSFEvaluationWorkbook create(HSSFWorkbook book) {
        if (book == null) {
            return null;
        }
        return new HSSFEvaluationWorkbook(book);
    }

    private HSSFEvaluationWorkbook(HSSFWorkbook book) {
        this._uBook = book;
        this._iBook = book.getWorkbook();
    }

    public int getExternalSheetIndex(String sheetName) {
        return this._iBook.checkExternSheet(this._uBook.getSheetIndex(sheetName));
    }

    public int getExternalSheetIndex(String workbookName, String sheetName) {
        return this._iBook.getExternalSheetIndex(workbookName, sheetName);
    }

    public NameXPtg getNameXPtg(String name) {
        return this._iBook.getNameXPtg(name);
    }

    public EvaluationName getName(String name, int sheetIndex) {
        for (int i = 0; i < this._iBook.getNumNames(); i++) {
            NameRecord nr = this._iBook.getNameRecord(i);
            if (nr.getSheetNumber() == sheetIndex + 1 && name.equalsIgnoreCase(nr.getNameText())) {
                return new Name(nr, i);
            }
        }
        if (sheetIndex == -1) {
            return null;
        }
        return getName(name, -1);
    }

    public int getSheetIndex(EvaluationSheet evalSheet) {
        return this._uBook.getSheetIndex((Sheet) ((HSSFEvaluationSheet) evalSheet).getHSSFSheet());
    }

    public int getSheetIndex(String sheetName) {
        return this._uBook.getSheetIndex(sheetName);
    }

    public String getSheetName(int sheetIndex) {
        return this._uBook.getSheetName(sheetIndex);
    }

    public EvaluationSheet getSheet(int sheetIndex) {
        return new HSSFEvaluationSheet(this._uBook.getSheetAt(sheetIndex));
    }

    public int convertFromExternSheetIndex(int externSheetIndex) {
        return this._iBook.getSheetIndexFromExternSheetIndex(externSheetIndex);
    }

    public EvaluationWorkbook.ExternalSheet getExternalSheet(int externSheetIndex) {
        return this._iBook.getExternalSheet(externSheetIndex);
    }

    public EvaluationWorkbook.ExternalName getExternalName(int externSheetIndex, int externNameIndex) {
        return this._iBook.getExternalName(externSheetIndex, externNameIndex);
    }

    public String resolveNameXText(NameXPtg n) {
        return this._iBook.resolveNameXText(n.getSheetRefIndex(), n.getNameIndex());
    }

    public String getSheetNameByExternSheet(int externSheetIndex) {
        return this._iBook.findSheetNameFromExternSheet(externSheetIndex);
    }

    public String getNameText(NamePtg namePtg) {
        return this._iBook.getNameRecord(namePtg.getIndex()).getNameText();
    }

    public EvaluationName getName(NamePtg namePtg) {
        int ix = namePtg.getIndex();
        return new Name(this._iBook.getNameRecord(ix), ix);
    }

    public Ptg[] getFormulaTokens(EvaluationCell evalCell) {
        return ((FormulaRecordAggregate) ((HSSFEvaluationCell) evalCell).getHSSFCell().getCellValueRecord()).getFormulaTokens();
    }

    private static final class Name implements EvaluationName {
        private final int _index;
        private final NameRecord _nameRecord;

        public Name(NameRecord nameRecord, int index) {
            this._nameRecord = nameRecord;
            this._index = index;
        }

        public Ptg[] getNameDefinition() {
            return this._nameRecord.getNameDefinition();
        }

        public String getNameText() {
            return this._nameRecord.getNameText();
        }

        public boolean hasFormula() {
            return this._nameRecord.hasFormula();
        }

        public boolean isFunctionName() {
            return this._nameRecord.isFunctionName();
        }

        public boolean isRange() {
            return this._nameRecord.hasFormula();
        }

        public NamePtg createPtg() {
            return new NamePtg(this._index);
        }
    }

    public SpreadsheetVersion getSpreadsheetVersion() {
        return SpreadsheetVersion.EXCEL97;
    }
}
