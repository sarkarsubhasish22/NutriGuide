package org.apache.poi.hssf.model;

import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.hssf.usermodel.HSSFEvaluationWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaParsingWorkbook;
import org.apache.poi.ss.formula.FormulaRenderer;

public final class HSSFFormulaParser {
    private static FormulaParsingWorkbook createParsingWorkbook(HSSFWorkbook book) {
        return HSSFEvaluationWorkbook.create(book);
    }

    private HSSFFormulaParser() {
    }

    public static Ptg[] parse(String formula, HSSFWorkbook workbook) throws FormulaParseException {
        return parse(formula, workbook, 0);
    }

    public static Ptg[] parse(String formula, HSSFWorkbook workbook, int formulaType) throws FormulaParseException {
        return parse(formula, workbook, formulaType, -1);
    }

    public static Ptg[] parse(String formula, HSSFWorkbook workbook, int formulaType, int sheetIndex) throws FormulaParseException {
        return FormulaParser.parse(formula, createParsingWorkbook(workbook), formulaType, sheetIndex);
    }

    public static String toFormulaString(HSSFWorkbook book, Ptg[] ptgs) {
        return FormulaRenderer.toFormulaString(HSSFEvaluationWorkbook.create(book), ptgs);
    }
}
