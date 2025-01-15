package org.apache.poi.util;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class FontMetricsDumper {
    public static void main(String[] args) throws IOException {
        char c;
        Properties props = new Properties();
        Font[] allFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        for (Font fontName : allFonts) {
            String fontName2 = fontName.getFontName();
            FontMetrics fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(new Font(fontName2, 1, 10));
            props.setProperty("font." + fontName2 + ".height", fontMetrics.getHeight() + "");
            StringBuffer characters = new StringBuffer();
            char c2 = 'a';
            while (true) {
                if (c2 > 'z') {
                    break;
                }
                characters.append(c2 + ", ");
                c2 = (char) (c2 + 1);
            }
            for (char c3 = 'A'; c3 <= 'Z'; c3 = (char) (c3 + 1)) {
                characters.append(c3 + ", ");
            }
            for (char c4 = '0'; c4 <= '9'; c4 = (char) (c4 + 1)) {
                characters.append(c4 + ", ");
            }
            StringBuffer widths = new StringBuffer();
            char c5 = 'a';
            for (c = 'z'; c5 <= c; c = 'z') {
                widths.append(fontMetrics.getWidths()[c5] + ", ");
                c5 = (char) (c5 + 1);
            }
            for (char c6 = 'A'; c6 <= 'Z'; c6 = (char) (c6 + 1)) {
                widths.append(fontMetrics.getWidths()[c6] + ", ");
            }
            for (char c7 = '0'; c7 <= '9'; c7 = (char) (c7 + 1)) {
                widths.append(fontMetrics.getWidths()[c7] + ", ");
            }
            props.setProperty("font." + fontName2 + ".characters", characters.toString());
            props.setProperty("font." + fontName2 + ".widths", widths.toString());
        }
        FileOutputStream fileOut = new FileOutputStream("font_metrics.properties");
        try {
            props.store(fileOut, "Font Metrics");
        } finally {
            fileOut.close();
        }
    }
}
