package org.apache.poi.hpsf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.ListIterator;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.util.LittleEndian;

public class MutablePropertySet extends PropertySet {
    private final int OFFSET_HEADER = ((((BYTE_ORDER_ASSERTION.length + FORMAT_ASSERTION.length) + 4) + 16) + 4);

    public MutablePropertySet() {
        this.byteOrder = LittleEndian.getUShort(BYTE_ORDER_ASSERTION);
        this.format = LittleEndian.getUShort(FORMAT_ASSERTION);
        this.osVersion = 133636;
        this.classID = new ClassID();
        this.sections = new LinkedList();
        this.sections.add(new MutableSection());
    }

    public MutablePropertySet(PropertySet ps) {
        this.byteOrder = ps.getByteOrder();
        this.format = ps.getFormat();
        this.osVersion = ps.getOSVersion();
        setClassID(ps.getClassID());
        clearSections();
        if (this.sections == null) {
            this.sections = new LinkedList();
        }
        for (Section mutableSection : ps.getSections()) {
            addSection(new MutableSection(mutableSection));
        }
    }

    public void setByteOrder(int byteOrder) {
        this.byteOrder = byteOrder;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    public void setOSVersion(int osVersion) {
        this.osVersion = osVersion;
    }

    public void setClassID(ClassID classID) {
        this.classID = classID;
    }

    public void clearSections() {
        this.sections = null;
    }

    public void addSection(Section section) {
        if (this.sections == null) {
            this.sections = new LinkedList();
        }
        this.sections.add(section);
    }

    public void write(OutputStream out) throws WritingNotSupportedException, IOException {
        int nrSections = this.sections.size();
        int length = 0 + TypeWriter.writeToStream(out, (short) getByteOrder()) + TypeWriter.writeToStream(out, (short) getFormat()) + TypeWriter.writeToStream(out, getOSVersion()) + TypeWriter.writeToStream(out, getClassID()) + TypeWriter.writeToStream(out, nrSections);
        int offset = this.OFFSET_HEADER + (nrSections * 20);
        int sectionsBegin = offset;
        ListIterator i = this.sections.listIterator();
        while (i.hasNext()) {
            MutableSection s = (MutableSection) i.next();
            if (s.getFormatID() != null) {
                length = length + TypeWriter.writeToStream(out, s.getFormatID()) + TypeWriter.writeUIntToStream(out, (long) offset);
                try {
                    offset += s.getSize();
                } catch (HPSFRuntimeException ex) {
                    Throwable cause = ex.getReason();
                    if (cause instanceof UnsupportedEncodingException) {
                        throw new IllegalPropertySetDataException(cause);
                    }
                    throw ex;
                }
            } else {
                throw new NoFormatIDException();
            }
        }
        int offset2 = sectionsBegin;
        ListIterator i2 = this.sections.listIterator();
        while (i2.hasNext()) {
            offset2 += ((MutableSection) i2.next()).write(out);
        }
    }

    public InputStream toInputStream() throws IOException, WritingNotSupportedException {
        ByteArrayOutputStream psStream = new ByteArrayOutputStream();
        write(psStream);
        psStream.close();
        return new ByteArrayInputStream(psStream.toByteArray());
    }

    public void write(DirectoryEntry dir, String name) throws WritingNotSupportedException, IOException {
        try {
            dir.getEntry(name).delete();
        } catch (FileNotFoundException e) {
        }
        dir.createDocument(name, toInputStream());
    }
}
