package org.apache.poi.ss.format;

import java.util.regex.Matcher;
import org.apache.poi.ss.format.CellFormatPart;

public class CellTextFormatter extends CellFormatter {
    static final CellFormatter SIMPLE_TEXT = new CellTextFormatter("@");
    private final String desc;
    private final int[] textPos;

    public CellTextFormatter(String format) {
        super(format);
        final int[] numPlaces = new int[1];
        String stringBuffer = CellFormatPart.parseFormat(format, CellFormatType.TEXT, new CellFormatPart.PartHandler() {
            public String handlePart(Matcher m, String part, CellFormatType type, StringBuffer desc) {
                if (!part.equals("@")) {
                    return null;
                }
                int[] iArr = numPlaces;
                iArr[0] = iArr[0] + 1;
                return "\u0000";
            }
        }).toString();
        this.desc = stringBuffer;
        this.textPos = new int[numPlaces[0]];
        int pos = stringBuffer.length() - 1;
        int i = 0;
        while (true) {
            int[] iArr = this.textPos;
            if (i < iArr.length) {
                iArr[i] = this.desc.lastIndexOf("\u0000", pos);
                pos = this.textPos[i] - 1;
                i++;
            } else {
                return;
            }
        }
    }

    public void formatValue(StringBuffer toAppendTo, Object obj) {
        int start = toAppendTo.length();
        String text = obj.toString();
        toAppendTo.append(this.desc);
        int i = 0;
        while (true) {
            int[] iArr = this.textPos;
            if (i < iArr.length) {
                int pos = iArr[i] + start;
                toAppendTo.replace(pos, pos + 1, text);
                i++;
            } else {
                return;
            }
        }
    }

    public void simpleValue(StringBuffer toAppendTo, Object value) {
        SIMPLE_TEXT.formatValue(toAppendTo, value);
    }
}
