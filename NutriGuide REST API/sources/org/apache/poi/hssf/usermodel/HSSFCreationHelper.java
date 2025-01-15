package org.apache.poi.hssf.usermodel;

import org.apache.poi.ss.usermodel.CreationHelper;

public class HSSFCreationHelper implements CreationHelper {
    private HSSFDataFormat dataFormat = new HSSFDataFormat(this.workbook.getWorkbook());
    private HSSFWorkbook workbook;

    HSSFCreationHelper(HSSFWorkbook wb) {
        this.workbook = wb;
    }

    public HSSFRichTextString createRichTextString(String text) {
        return new HSSFRichTextString(text);
    }

    public HSSFDataFormat createDataFormat() {
        return this.dataFormat;
    }

    public HSSFHyperlink createHyperlink(int type) {
        return new HSSFHyperlink(type);
    }

    public HSSFFormulaEvaluator createFormulaEvaluator() {
        return new HSSFFormulaEvaluator(this.workbook);
    }

    public HSSFClientAnchor createClientAnchor() {
        return new HSSFClientAnchor();
    }
}
