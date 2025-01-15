package org.apache.poi.ss.formula;

public interface EvaluationCell {
    boolean getBooleanCellValue();

    int getCellType();

    int getColumnIndex();

    int getErrorCellValue();

    Object getIdentityKey();

    double getNumericCellValue();

    int getRowIndex();

    EvaluationSheet getSheet();

    String getStringCellValue();
}
