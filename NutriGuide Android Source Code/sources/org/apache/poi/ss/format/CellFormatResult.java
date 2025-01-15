package org.apache.poi.ss.format;

import java.awt.Color;

public class CellFormatResult {
    public final boolean applies;
    public final String text;
    public final Color textColor;

    public CellFormatResult(boolean applies2, String text2, Color textColor2) {
        this.applies = applies2;
        this.text = text2;
        this.textColor = applies2 ? textColor2 : null;
    }
}
