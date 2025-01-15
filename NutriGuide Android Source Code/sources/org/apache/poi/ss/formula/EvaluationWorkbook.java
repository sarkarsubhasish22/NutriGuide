package org.apache.poi.ss.formula;

import org.apache.poi.hssf.record.formula.NamePtg;
import org.apache.poi.hssf.record.formula.NameXPtg;
import org.apache.poi.hssf.record.formula.Ptg;

public interface EvaluationWorkbook {
    int convertFromExternSheetIndex(int i);

    ExternalName getExternalName(int i, int i2);

    ExternalSheet getExternalSheet(int i);

    Ptg[] getFormulaTokens(EvaluationCell evaluationCell);

    EvaluationName getName(NamePtg namePtg);

    EvaluationSheet getSheet(int i);

    int getSheetIndex(String str);

    int getSheetIndex(EvaluationSheet evaluationSheet);

    String getSheetName(int i);

    String resolveNameXText(NameXPtg nameXPtg);

    public static class ExternalSheet {
        private final String _sheetName;
        private final String _workbookName;

        public ExternalSheet(String workbookName, String sheetName) {
            this._workbookName = workbookName;
            this._sheetName = sheetName;
        }

        public String getWorkbookName() {
            return this._workbookName;
        }

        public String getSheetName() {
            return this._sheetName;
        }
    }

    public static class ExternalName {
        private final int _ix;
        private final String _nameName;
        private final int _nameNumber;

        public ExternalName(String nameName, int nameNumber, int ix) {
            this._nameName = nameName;
            this._nameNumber = nameNumber;
            this._ix = ix;
        }

        public String getName() {
            return this._nameName;
        }

        public int getNumber() {
            return this._nameNumber;
        }

        public int getIx() {
            return this._ix;
        }
    }
}
