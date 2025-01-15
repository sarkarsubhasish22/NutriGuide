package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.record.NameCommentRecord;
import org.apache.poi.hssf.record.NameRecord;
import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.ss.usermodel.Name;

public final class HSSFName implements Name {
    private HSSFWorkbook _book;
    private NameCommentRecord _commentRec;
    private NameRecord _definedNameRec;

    HSSFName(HSSFWorkbook book, NameRecord name) {
        this(book, name, (NameCommentRecord) null);
    }

    HSSFName(HSSFWorkbook book, NameRecord name, NameCommentRecord comment) {
        this._book = book;
        this._definedNameRec = name;
        this._commentRec = comment;
    }

    public String getSheetName() {
        return this._book.getWorkbook().findSheetNameFromExternSheet(this._definedNameRec.getExternSheetNumber());
    }

    public String getNameName() {
        return this._definedNameRec.getNameText();
    }

    public void setNameName(String nameName) {
        validateName(nameName);
        InternalWorkbook wb = this._book.getWorkbook();
        this._definedNameRec.setNameText(nameName);
        int sheetNumber = this._definedNameRec.getSheetNumber();
        int i = wb.getNumNames() - 1;
        while (i >= 0) {
            NameRecord rec = wb.getNameRecord(i);
            if (rec == this._definedNameRec || !rec.getNameText().equalsIgnoreCase(nameName) || sheetNumber != rec.getSheetNumber()) {
                i--;
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("The ");
                sb.append(sheetNumber == 0 ? "workbook" : "sheet");
                sb.append(" already contains this name: ");
                sb.append(nameName);
                String msg = sb.toString();
                this._definedNameRec.setNameText(nameName + "(2)");
                throw new IllegalArgumentException(msg);
            }
        }
        NameCommentRecord nameCommentRecord = this._commentRec;
        if (nameCommentRecord != null) {
            String nameText = nameCommentRecord.getNameText();
            this._commentRec.setNameText(nameName);
            this._book.getWorkbook().updateNameCommentRecordCache(this._commentRec);
        }
    }

    private static void validateName(String name) {
        if (name.length() != 0) {
            char c = name.charAt(0);
            if ((c != '_' && !Character.isLetter(c)) || name.indexOf(32) != -1) {
                throw new IllegalArgumentException("Invalid name: '" + name + "'; Names must begin with a letter or underscore and not contain spaces");
            }
            return;
        }
        throw new IllegalArgumentException("Name cannot be blank");
    }

    public String getReference() {
        return getRefersToFormula();
    }

    public void setReference(String ref) {
        setRefersToFormula(ref);
    }

    public void setRefersToFormula(String formulaText) {
        this._definedNameRec.setNameDefinition(HSSFFormulaParser.parse(formulaText, this._book, 4, getSheetIndex()));
    }

    public String getRefersToFormula() {
        if (!this._definedNameRec.isFunctionName()) {
            Ptg[] ptgs = this._definedNameRec.getNameDefinition();
            if (ptgs.length < 1) {
                return null;
            }
            return HSSFFormulaParser.toFormulaString(this._book, ptgs);
        }
        throw new IllegalStateException("Only applicable to named ranges");
    }

    public boolean isDeleted() {
        return Ptg.doesFormulaReferToDeletedCell(this._definedNameRec.getNameDefinition());
    }

    public boolean isFunctionName() {
        return this._definedNameRec.isFunctionName();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(64);
        sb.append(getClass().getName());
        sb.append(" [");
        sb.append(this._definedNameRec.getNameText());
        sb.append("]");
        return sb.toString();
    }

    public void setSheetIndex(int index) {
        String str;
        int lastSheetIx = this._book.getNumberOfSheets() - 1;
        if (index < -1 || index > lastSheetIx) {
            StringBuilder sb = new StringBuilder();
            sb.append("Sheet index (");
            sb.append(index);
            sb.append(") is out of range");
            if (lastSheetIx == -1) {
                str = "";
            } else {
                str = " (0.." + lastSheetIx + ")";
            }
            sb.append(str);
            throw new IllegalArgumentException(sb.toString());
        }
        this._definedNameRec.setSheetNumber(index + 1);
    }

    public int getSheetIndex() {
        return this._definedNameRec.getSheetNumber() - 1;
    }

    public String getComment() {
        NameCommentRecord nameCommentRecord = this._commentRec;
        if (nameCommentRecord == null || nameCommentRecord.getCommentText() == null || this._commentRec.getCommentText().length() <= 0) {
            return this._definedNameRec.getDescriptionText();
        }
        return this._commentRec.getCommentText();
    }

    public void setComment(String comment) {
        this._definedNameRec.setDescriptionText(comment);
        NameCommentRecord nameCommentRecord = this._commentRec;
        if (nameCommentRecord != null) {
            nameCommentRecord.setCommentText(comment);
        }
    }

    public void setFunction(boolean value) {
        this._definedNameRec.setFunction(value);
    }
}
