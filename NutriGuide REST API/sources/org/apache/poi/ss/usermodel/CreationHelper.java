package org.apache.poi.ss.usermodel;

public interface CreationHelper {
    ClientAnchor createClientAnchor();

    DataFormat createDataFormat();

    FormulaEvaluator createFormulaEvaluator();

    Hyperlink createHyperlink(int i);

    RichTextString createRichTextString(String str);
}
