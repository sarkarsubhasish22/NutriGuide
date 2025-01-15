package org.apache.poi.hpsf.wellknown;

import java.util.HashMap;
import org.apache.poi.hssf.record.formula.AreaErrPtg;
import org.apache.poi.hssf.record.formula.RefNPtg;
import org.apache.poi.hssf.record.formula.UnionPtg;

public class SectionIDMap extends HashMap {
    public static final byte[][] DOCUMENT_SUMMARY_INFORMATION_ID = {new byte[]{-43, -51, -43, 2, 46, -100, UnionPtg.sid, 27, -109, -105, 8, 0, AreaErrPtg.sid, RefNPtg.sid, -7, -82}, new byte[]{-43, -51, -43, 5, 46, -100, UnionPtg.sid, 27, -109, -105, 8, 0, AreaErrPtg.sid, RefNPtg.sid, -7, -82}};
    public static final byte[] SUMMARY_INFORMATION_ID = {-14, -97, -123, -32, 79, -7, UnionPtg.sid, 104, -85, -111, 8, 0, AreaErrPtg.sid, 39, -77, -39};
    public static final String UNDEFINED = "[undefined]";
    private static SectionIDMap defaultMap;

    public static SectionIDMap getInstance() {
        if (defaultMap == null) {
            SectionIDMap m = new SectionIDMap();
            m.put(SUMMARY_INFORMATION_ID, PropertyIDMap.getSummaryInformationProperties());
            m.put(DOCUMENT_SUMMARY_INFORMATION_ID[0], PropertyIDMap.getDocumentSummaryInformationProperties());
            defaultMap = m;
        }
        return defaultMap;
    }

    public static String getPIDString(byte[] sectionFormatID, long pid) {
        String s;
        PropertyIDMap m = getInstance().get(sectionFormatID);
        if (m == null || (s = (String) m.get(pid)) == null) {
            return UNDEFINED;
        }
        return s;
    }

    public PropertyIDMap get(byte[] sectionFormatID) {
        return (PropertyIDMap) super.get(new String(sectionFormatID));
    }

    public Object get(Object sectionFormatID) {
        return get((byte[]) sectionFormatID);
    }

    public Object put(byte[] sectionFormatID, PropertyIDMap propertyIDMap) {
        return super.put(new String(sectionFormatID), propertyIDMap);
    }

    public Object put(Object key, Object value) {
        return put((byte[]) key, (PropertyIDMap) value);
    }
}
