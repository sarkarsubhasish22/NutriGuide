package org.apache.poi.hpsf;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.poi.hpsf.wellknown.SectionIDMap;
import org.apache.poi.util.LittleEndian;

public class PropertySet {
    static final byte[] BYTE_ORDER_ASSERTION = {-2, -1};
    static final byte[] FORMAT_ASSERTION = {0, 0};
    public static final int OS_MACINTOSH = 1;
    public static final int OS_WIN16 = 0;
    public static final int OS_WIN32 = 2;
    protected int byteOrder;
    protected ClassID classID;
    protected int format;
    protected int osVersion;
    protected List sections;

    public int getByteOrder() {
        return this.byteOrder;
    }

    public int getFormat() {
        return this.format;
    }

    public int getOSVersion() {
        return this.osVersion;
    }

    public ClassID getClassID() {
        return this.classID;
    }

    public int getSectionCount() {
        return this.sections.size();
    }

    public List getSections() {
        return this.sections;
    }

    protected PropertySet() {
    }

    public PropertySet(InputStream stream) throws NoPropertySetStreamException, MarkUnsupportedException, IOException, UnsupportedEncodingException {
        if (isPropertySetStream(stream)) {
            byte[] buffer = new byte[stream.available()];
            stream.read(buffer, 0, buffer.length);
            init(buffer, 0, buffer.length);
            return;
        }
        throw new NoPropertySetStreamException();
    }

    public PropertySet(byte[] stream, int offset, int length) throws NoPropertySetStreamException, UnsupportedEncodingException {
        if (isPropertySetStream(stream, offset, length)) {
            init(stream, offset, length);
            return;
        }
        throw new NoPropertySetStreamException();
    }

    public PropertySet(byte[] stream) throws NoPropertySetStreamException, UnsupportedEncodingException {
        this(stream, 0, stream.length);
    }

    public static boolean isPropertySetStream(InputStream stream) throws MarkUnsupportedException, IOException {
        if (stream.markSupported()) {
            stream.mark(50);
            byte[] buffer = new byte[50];
            boolean isPropertySetStream = isPropertySetStream(buffer, 0, stream.read(buffer, 0, Math.min(buffer.length, stream.available())));
            stream.reset();
            return isPropertySetStream;
        }
        throw new MarkUnsupportedException(stream.getClass().getName());
    }

    public static boolean isPropertySetStream(byte[] src, int offset, int length) {
        int o = offset;
        int byteOrder2 = LittleEndian.getUShort(src, o);
        int o2 = o + 2;
        byte[] temp = new byte[2];
        LittleEndian.putShort(temp, (short) byteOrder2);
        if (!Util.equal(temp, BYTE_ORDER_ASSERTION)) {
            return false;
        }
        int format2 = LittleEndian.getUShort(src, o2);
        int o3 = o2 + 2;
        byte[] temp2 = new byte[2];
        LittleEndian.putShort(temp2, (short) format2);
        if (!Util.equal(temp2, FORMAT_ASSERTION)) {
            return false;
        }
        int o4 = o3 + 4 + 16;
        long sectionCount = LittleEndian.getUInt(src, o4);
        int o5 = o4 + 4;
        if (sectionCount < 0) {
            return false;
        }
        return true;
    }

    private void init(byte[] src, int offset, int length) throws UnsupportedEncodingException {
        int o = offset;
        this.byteOrder = LittleEndian.getUShort(src, o);
        int o2 = o + 2;
        this.format = LittleEndian.getUShort(src, o2);
        int o3 = o2 + 2;
        this.osVersion = (int) LittleEndian.getUInt(src, o3);
        int o4 = o3 + 4;
        this.classID = new ClassID(src, o4);
        int o5 = o4 + 16;
        int sectionCount = LittleEndian.getInt(src, o5);
        int o6 = o5 + 4;
        if (sectionCount >= 0) {
            this.sections = new ArrayList(sectionCount);
            for (int i = 0; i < sectionCount; i++) {
                Section s = new Section(src, o6);
                o6 += 20;
                this.sections.add(s);
            }
            return;
        }
        throw new HPSFRuntimeException("Section count " + sectionCount + " is negative.");
    }

    public boolean isSummaryInformation() {
        if (this.sections.size() <= 0) {
            return false;
        }
        return Util.equal(((Section) this.sections.get(0)).getFormatID().getBytes(), SectionIDMap.SUMMARY_INFORMATION_ID);
    }

    public boolean isDocumentSummaryInformation() {
        if (this.sections.size() <= 0) {
            return false;
        }
        return Util.equal(((Section) this.sections.get(0)).getFormatID().getBytes(), SectionIDMap.DOCUMENT_SUMMARY_INFORMATION_ID[0]);
    }

    public Property[] getProperties() throws NoSingleSectionException {
        return getFirstSection().getProperties();
    }

    /* access modifiers changed from: protected */
    public Object getProperty(int id) throws NoSingleSectionException {
        return getFirstSection().getProperty((long) id);
    }

    /* access modifiers changed from: protected */
    public boolean getPropertyBooleanValue(int id) throws NoSingleSectionException {
        return getFirstSection().getPropertyBooleanValue(id);
    }

    /* access modifiers changed from: protected */
    public int getPropertyIntValue(int id) throws NoSingleSectionException {
        return getFirstSection().getPropertyIntValue((long) id);
    }

    public boolean wasNull() throws NoSingleSectionException {
        return getFirstSection().wasNull();
    }

    public Section getFirstSection() {
        if (getSectionCount() >= 1) {
            return (Section) this.sections.get(0);
        }
        throw new MissingSectionException("Property set does not contain any sections.");
    }

    public Section getSingleSection() {
        int sectionCount = getSectionCount();
        if (sectionCount == 1) {
            return (Section) this.sections.get(0);
        }
        throw new NoSingleSectionException("Property set contains " + sectionCount + " sections.");
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof PropertySet)) {
            return false;
        }
        PropertySet ps = (PropertySet) o;
        int byteOrder1 = ps.getByteOrder();
        int byteOrder2 = getByteOrder();
        ClassID classID1 = ps.getClassID();
        ClassID classID2 = getClassID();
        int format1 = ps.getFormat();
        int format2 = getFormat();
        int osVersion1 = ps.getOSVersion();
        int osVersion2 = getOSVersion();
        int sectionCount1 = ps.getSectionCount();
        int sectionCount2 = getSectionCount();
        if (byteOrder1 == byteOrder2 && classID1.equals(classID2) && format1 == format2 && osVersion1 == osVersion2 && sectionCount1 == sectionCount2) {
            return Util.equals((Collection<?>) getSections(), (Collection<?>) ps.getSections());
        }
        return false;
    }

    public int hashCode() {
        throw new UnsupportedOperationException("FIXME: Not yet implemented.");
    }

    public String toString() {
        StringBuffer b = new StringBuffer();
        int sectionCount = getSectionCount();
        b.append(getClass().getName());
        b.append('[');
        b.append("byteOrder: ");
        b.append(getByteOrder());
        b.append(", classID: ");
        b.append(getClassID());
        b.append(", format: ");
        b.append(getFormat());
        b.append(", OSVersion: ");
        b.append(getOSVersion());
        b.append(", sectionCount: ");
        b.append(sectionCount);
        b.append(", sections: [\n");
        List sections2 = getSections();
        for (int i = 0; i < sectionCount; i++) {
            b.append(((Section) sections2.get(i)).toString());
        }
        b.append(']');
        b.append(']');
        return b.toString();
    }
}
