package org.apache.poi.hpsf;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.rmi.UnexpectedException;
import org.apache.poi.hpsf.wellknown.SectionIDMap;

public class PropertySetFactory {
    public static PropertySet create(InputStream stream) throws NoPropertySetStreamException, MarkUnsupportedException, UnsupportedEncodingException, IOException {
        PropertySet ps = new PropertySet(stream);
        try {
            if (ps.isSummaryInformation()) {
                return new SummaryInformation(ps);
            }
            if (ps.isDocumentSummaryInformation()) {
                return new DocumentSummaryInformation(ps);
            }
            return ps;
        } catch (UnexpectedPropertySetTypeException ex) {
            throw new UnexpectedException(ex.toString());
        }
    }

    public static SummaryInformation newSummaryInformation() {
        MutablePropertySet ps = new MutablePropertySet();
        ((MutableSection) ps.getFirstSection()).setFormatID(SectionIDMap.SUMMARY_INFORMATION_ID);
        try {
            return new SummaryInformation(ps);
        } catch (UnexpectedPropertySetTypeException ex) {
            throw new HPSFRuntimeException((Throwable) ex);
        }
    }

    public static DocumentSummaryInformation newDocumentSummaryInformation() {
        MutablePropertySet ps = new MutablePropertySet();
        ((MutableSection) ps.getFirstSection()).setFormatID(SectionIDMap.DOCUMENT_SUMMARY_INFORMATION_ID[0]);
        try {
            return new DocumentSummaryInformation(ps);
        } catch (UnexpectedPropertySetTypeException ex) {
            throw new HPSFRuntimeException((Throwable) ex);
        }
    }
}
