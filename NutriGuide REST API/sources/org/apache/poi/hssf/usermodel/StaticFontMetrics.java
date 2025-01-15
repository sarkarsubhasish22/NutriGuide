package org.apache.poi.hssf.usermodel;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

final class StaticFontMetrics {
    private static Map<String, FontDetails> fontDetailsMap = new HashMap();
    private static Properties fontMetricsProps;

    StaticFontMetrics() {
    }

    public static FontDetails getFontDetails(Font font) {
        if (fontMetricsProps == null) {
            InputStream metricsIn = null;
            try {
                fontMetricsProps = new Properties();
                String propFileName = null;
                try {
                    propFileName = System.getProperty("font.metrics.filename");
                } catch (SecurityException e) {
                }
                if (propFileName != null) {
                    File file = new File(propFileName);
                    if (file.exists()) {
                        metricsIn = new FileInputStream(file);
                    } else {
                        throw new FileNotFoundException("font_metrics.properties not found at path " + file.getAbsolutePath());
                    }
                } else {
                    metricsIn = FontDetails.class.getResourceAsStream("/font_metrics.properties");
                    if (metricsIn == null) {
                        throw new FileNotFoundException("font_metrics.properties not found in classpath");
                    }
                }
                fontMetricsProps.load(metricsIn);
                if (metricsIn != null) {
                    try {
                        metricsIn.close();
                    } catch (IOException e2) {
                    }
                }
            } catch (IOException e3) {
                throw new RuntimeException("Could not load font metrics: " + e3.getMessage());
            } catch (Throwable th) {
                if (metricsIn != null) {
                    try {
                        metricsIn.close();
                    } catch (IOException e4) {
                    }
                }
                throw th;
            }
        }
        String fontName = font.getName();
        String fontStyle = "";
        if (font.isPlain()) {
            fontStyle = fontStyle + "plain";
        }
        if (font.isBold()) {
            fontStyle = fontStyle + "bold";
        }
        if (font.isItalic()) {
            fontStyle = fontStyle + "italic";
        }
        if (fontMetricsProps.get(FontDetails.buildFontHeightProperty(fontName)) == null) {
            if (fontMetricsProps.get(FontDetails.buildFontHeightProperty(fontName + "." + fontStyle)) != null) {
                fontName = fontName + "." + fontStyle;
            }
        }
        if (fontDetailsMap.get(fontName) != null) {
            return fontDetailsMap.get(fontName);
        }
        FontDetails fontDetails = FontDetails.create(fontName, fontMetricsProps);
        fontDetailsMap.put(fontName, fontDetails);
        return fontDetails;
    }
}
