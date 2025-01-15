package org.apache.poi.hssf.record.formula;

import org.apache.poi.ss.formula.EvaluationWorkbook;
import org.apache.poi.ss.formula.FormulaRenderingWorkbook;

final class ExternSheetNameResolver {
    private ExternSheetNameResolver() {
    }

    public static String prependSheetName(FormulaRenderingWorkbook book, int field_1_index_extern_sheet, String cellRefText) {
        StringBuffer sb;
        EvaluationWorkbook.ExternalSheet externalSheet = book.getExternalSheet(field_1_index_extern_sheet);
        if (externalSheet != null) {
            String wbName = externalSheet.getWorkbookName();
            String sheetName = externalSheet.getSheetName();
            sb = new StringBuffer(wbName.length() + sheetName.length() + cellRefText.length() + 4);
            SheetNameFormatter.appendFormat(sb, wbName, sheetName);
        } else {
            String sheetName2 = book.getSheetNameByExternSheet(field_1_index_extern_sheet);
            sb = new StringBuffer(sheetName2.length() + cellRefText.length() + 4);
            if (sheetName2.length() < 1) {
                sb.append("#REF");
            } else {
                SheetNameFormatter.appendFormat(sb, sheetName2);
            }
        }
        sb.append('!');
        sb.append(cellRefText);
        return sb.toString();
    }
}
