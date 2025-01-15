package org.apache.poi.hpsf.extractor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.POIDocument;
import org.apache.poi.POITextExtractor;
import org.apache.poi.hpsf.CustomProperties;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.Property;
import org.apache.poi.hpsf.SpecialPropertySet;
import org.apache.poi.hpsf.wellknown.PropertyIDMap;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.LittleEndian;

public class HPSFPropertiesExtractor extends POITextExtractor {
    public HPSFPropertiesExtractor(POITextExtractor mainExtractor) {
        super(mainExtractor);
    }

    public HPSFPropertiesExtractor(POIDocument doc) {
        super(doc);
    }

    public HPSFPropertiesExtractor(POIFSFileSystem fs) {
        super((POIDocument) new PropertiesOnlyDocument(fs));
    }

    public String getDocumentSummaryInformationText() {
        DocumentSummaryInformation dsi = this.document.getDocumentSummaryInformation();
        StringBuffer text = new StringBuffer();
        text.append(getPropertiesText(dsi));
        CustomProperties cps = dsi == null ? null : dsi.getCustomProperties();
        if (cps != null) {
            for (String key : cps.nameSet()) {
                String val = getPropertyValueText(cps.get(key));
                text.append(key + " = " + val + "\n");
            }
        }
        return text.toString();
    }

    public String getSummaryInformationText() {
        return getPropertiesText(this.document.getSummaryInformation());
    }

    private static String getPropertiesText(SpecialPropertySet ps) {
        if (ps == null) {
            return "";
        }
        StringBuffer text = new StringBuffer();
        PropertyIDMap idMap = ps.getPropertySetIDMap();
        Property[] props = ps.getProperties();
        for (int i = 0; i < props.length; i++) {
            String type = Long.toString(props[i].getID());
            Object typeObj = idMap.get(props[i].getID());
            if (typeObj != null) {
                type = typeObj.toString();
            }
            String val = getPropertyValueText(props[i].getValue());
            text.append(type + " = " + val + "\n");
        }
        return text.toString();
    }

    private static String getPropertyValueText(Object val) {
        if (val == null) {
            return "(not set)";
        }
        if (!(val instanceof byte[])) {
            return val.toString();
        }
        byte[] b = (byte[]) val;
        if (b.length == 0) {
            return "";
        }
        if (b.length == 1) {
            return Byte.toString(b[0]);
        }
        if (b.length == 2) {
            return Integer.toString(LittleEndian.getUShort(b));
        }
        if (b.length == 4) {
            return Long.toString(LittleEndian.getUInt(b));
        }
        return new String(b);
    }

    public String getText() {
        return getSummaryInformationText() + getDocumentSummaryInformationText();
    }

    public POITextExtractor getMetadataTextExtractor() {
        throw new IllegalStateException("You already have the Metadata Text Extractor, not recursing!");
    }

    private static final class PropertiesOnlyDocument extends POIDocument {
        public PropertiesOnlyDocument(POIFSFileSystem fs) {
            super(fs);
        }

        public void write(OutputStream out) {
            throw new IllegalStateException("Unable to write, only for properties!");
        }
    }

    public static void main(String[] args) throws IOException {
        for (String file : args) {
            System.out.println(new HPSFPropertiesExtractor(new POIFSFileSystem(new FileInputStream(file))).getText());
        }
    }
}
