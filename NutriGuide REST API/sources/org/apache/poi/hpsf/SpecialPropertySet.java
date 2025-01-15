package org.apache.poi.hpsf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import org.apache.poi.hpsf.wellknown.PropertyIDMap;
import org.apache.poi.poifs.filesystem.DirectoryEntry;

public abstract class SpecialPropertySet extends MutablePropertySet {
    private MutablePropertySet delegate;

    public abstract PropertyIDMap getPropertySetIDMap();

    public SpecialPropertySet(PropertySet ps) {
        this.delegate = new MutablePropertySet(ps);
    }

    public SpecialPropertySet(MutablePropertySet ps) {
        this.delegate = ps;
    }

    public int getByteOrder() {
        return this.delegate.getByteOrder();
    }

    public int getFormat() {
        return this.delegate.getFormat();
    }

    public int getOSVersion() {
        return this.delegate.getOSVersion();
    }

    public ClassID getClassID() {
        return this.delegate.getClassID();
    }

    public int getSectionCount() {
        return this.delegate.getSectionCount();
    }

    public List getSections() {
        return this.delegate.getSections();
    }

    public boolean isSummaryInformation() {
        return this.delegate.isSummaryInformation();
    }

    public boolean isDocumentSummaryInformation() {
        return this.delegate.isDocumentSummaryInformation();
    }

    public Section getFirstSection() {
        return this.delegate.getFirstSection();
    }

    public void addSection(Section section) {
        this.delegate.addSection(section);
    }

    public void clearSections() {
        this.delegate.clearSections();
    }

    public void setByteOrder(int byteOrder) {
        this.delegate.setByteOrder(byteOrder);
    }

    public void setClassID(ClassID classID) {
        this.delegate.setClassID(classID);
    }

    public void setFormat(int format) {
        this.delegate.setFormat(format);
    }

    public void setOSVersion(int osVersion) {
        this.delegate.setOSVersion(osVersion);
    }

    public InputStream toInputStream() throws IOException, WritingNotSupportedException {
        return this.delegate.toInputStream();
    }

    public void write(DirectoryEntry dir, String name) throws WritingNotSupportedException, IOException {
        this.delegate.write(dir, name);
    }

    public void write(OutputStream out) throws WritingNotSupportedException, IOException {
        this.delegate.write(out);
    }

    public boolean equals(Object o) {
        return this.delegate.equals(o);
    }

    public Property[] getProperties() throws NoSingleSectionException {
        return this.delegate.getProperties();
    }

    /* access modifiers changed from: protected */
    public Object getProperty(int id) throws NoSingleSectionException {
        return this.delegate.getProperty(id);
    }

    /* access modifiers changed from: protected */
    public boolean getPropertyBooleanValue(int id) throws NoSingleSectionException {
        return this.delegate.getPropertyBooleanValue(id);
    }

    /* access modifiers changed from: protected */
    public int getPropertyIntValue(int id) throws NoSingleSectionException {
        return this.delegate.getPropertyIntValue(id);
    }

    public int hashCode() {
        return this.delegate.hashCode();
    }

    public String toString() {
        return this.delegate.toString();
    }

    public boolean wasNull() throws NoSingleSectionException {
        return this.delegate.wasNull();
    }
}
