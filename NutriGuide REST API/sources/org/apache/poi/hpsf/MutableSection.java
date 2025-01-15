package org.apache.poi.hpsf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class MutableSection extends Section {
    private boolean dirty;
    private List<Property> preprops;
    private byte[] sectionBytes;

    public MutableSection() {
        this.dirty = true;
        this.dirty = true;
        this.formatID = null;
        this.offset = -1;
        this.preprops = new LinkedList();
    }

    public MutableSection(Section s) {
        this.dirty = true;
        setFormatID(s.getFormatID());
        Property[] pa = s.getProperties();
        MutableProperty[] mpa = new MutableProperty[pa.length];
        for (int i = 0; i < pa.length; i++) {
            mpa[i] = new MutableProperty(pa[i]);
        }
        setProperties(mpa);
        setDictionary(s.getDictionary());
    }

    public void setFormatID(ClassID formatID) {
        this.formatID = formatID;
    }

    public void setFormatID(byte[] formatID) {
        ClassID fid = getFormatID();
        if (fid == null) {
            fid = new ClassID();
            setFormatID(fid);
        }
        fid.setBytes(formatID);
    }

    public void setProperties(Property[] properties) {
        this.properties = properties;
        this.preprops = new LinkedList();
        for (Property add : properties) {
            this.preprops.add(add);
        }
        this.dirty = true;
    }

    public void setProperty(int id, String value) {
        setProperty(id, 31, value);
        this.dirty = true;
    }

    public void setProperty(int id, int value) {
        setProperty(id, 3, Integer.valueOf(value));
        this.dirty = true;
    }

    public void setProperty(int id, long value) {
        setProperty(id, 20, Long.valueOf(value));
        this.dirty = true;
    }

    public void setProperty(int id, boolean value) {
        setProperty(id, 11, Boolean.valueOf(value));
        this.dirty = true;
    }

    public void setProperty(int id, long variantType, Object value) {
        MutableProperty p = new MutableProperty();
        p.setID((long) id);
        p.setType(variantType);
        p.setValue(value);
        setProperty(p);
        this.dirty = true;
    }

    public void setProperty(Property p) {
        removeProperty(p.getID());
        this.preprops.add(p);
        this.dirty = true;
    }

    public void removeProperty(long id) {
        Iterator<Property> i = this.preprops.iterator();
        while (true) {
            if (i.hasNext()) {
                if (i.next().getID() == id) {
                    i.remove();
                    break;
                }
            } else {
                break;
            }
        }
        this.dirty = true;
    }

    /* access modifiers changed from: protected */
    public void setPropertyBooleanValue(int id, boolean value) {
        setProperty(id, 11, Boolean.valueOf(value));
    }

    public int getSize() {
        if (this.dirty) {
            try {
                this.size = calcSize();
                this.dirty = false;
            } catch (HPSFRuntimeException ex) {
                throw ex;
            } catch (Exception ex2) {
                throw new HPSFRuntimeException((Throwable) ex2);
            }
        }
        return this.size;
    }

    private int calcSize() throws WritingNotSupportedException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        write(out);
        out.close();
        byte[] pad4 = Util.pad4(out.toByteArray());
        this.sectionBytes = pad4;
        return pad4.length;
    }

    public int write(OutputStream out) throws WritingNotSupportedException, IOException {
        byte[] bArr;
        if (this.dirty || (bArr = this.sectionBytes) == null) {
            ByteArrayOutputStream propertyStream = new ByteArrayOutputStream();
            ByteArrayOutputStream propertyListStream = new ByteArrayOutputStream();
            int position = 0 + (getPropertyCount() * 2 * 4) + 8;
            int codepage = -1;
            if (getProperty(0) != null) {
                Object p1 = getProperty(1);
                if (p1 == null) {
                    setProperty(1, 2, 1200);
                } else if (!(p1 instanceof Integer)) {
                    throw new IllegalPropertySetDataException("The codepage property (ID = 1) must be an Integer object.");
                }
                codepage = getCodepage();
            }
            Collections.sort(this.preprops, new Comparator<Property>() {
                public int compare(Property p1, Property p2) {
                    if (p1.getID() < p2.getID()) {
                        return -1;
                    }
                    if (p1.getID() == p2.getID()) {
                        return 0;
                    }
                    return 1;
                }
            });
            ListIterator<Property> i = this.preprops.listIterator();
            while (i.hasNext()) {
                MutableProperty p = (MutableProperty) i.next();
                long id = p.getID();
                TypeWriter.writeUIntToStream(propertyListStream, p.getID());
                TypeWriter.writeUIntToStream(propertyListStream, (long) position);
                if (id != 0) {
                    position += p.write(propertyStream, getCodepage());
                } else if (codepage != -1) {
                    position += writeDictionary(propertyStream, this.dictionary, codepage);
                } else {
                    throw new IllegalPropertySetDataException("Codepage (property 1) is undefined.");
                }
            }
            propertyStream.close();
            propertyListStream.close();
            byte[] pb1 = propertyListStream.toByteArray();
            byte[] pb2 = propertyStream.toByteArray();
            TypeWriter.writeToStream(out, pb1.length + 8 + pb2.length);
            TypeWriter.writeToStream(out, getPropertyCount());
            out.write(pb1);
            out.write(pb2);
            return pb1.length + 8 + pb2.length;
        }
        out.write(bArr);
        return this.sectionBytes.length;
    }

    private static int writeDictionary(OutputStream out, Map<Long, String> dictionary, int codepage) throws IOException {
        int length = TypeWriter.writeUIntToStream(out, (long) dictionary.size());
        for (Long key : dictionary.keySet()) {
            String value = dictionary.get(key);
            if (codepage == 1200) {
                int sLength = value.length() + 1;
                if (sLength % 2 == 1) {
                    sLength++;
                }
                length = length + TypeWriter.writeUIntToStream(out, key.longValue()) + TypeWriter.writeUIntToStream(out, (long) sLength);
                byte[] ca = value.getBytes(VariantSupport.codepageToEncoding(codepage));
                for (int j = 2; j < ca.length; j += 2) {
                    out.write(ca[j + 1]);
                    out.write(ca[j]);
                    length += 2;
                }
                for (int sLength2 = sLength - value.length(); sLength2 > 0; sLength2--) {
                    out.write(0);
                    out.write(0);
                    length += 2;
                }
            } else {
                int length2 = length + TypeWriter.writeUIntToStream(out, key.longValue()) + TypeWriter.writeUIntToStream(out, (long) (value.length() + 1));
                byte[] ba = value.getBytes(VariantSupport.codepageToEncoding(codepage));
                for (byte write : ba) {
                    out.write(write);
                    length2++;
                }
                out.write(0);
                length = length2 + 1;
            }
        }
        return length;
    }

    public int getPropertyCount() {
        return this.preprops.size();
    }

    public Property[] getProperties() {
        this.properties = (Property[]) this.preprops.toArray(new Property[0]);
        return this.properties;
    }

    public Object getProperty(long id) {
        getProperties();
        return super.getProperty(id);
    }

    public void setDictionary(Map<Long, String> dictionary) throws IllegalPropertySetDataException {
        if (dictionary != null) {
            this.dictionary = dictionary;
            setProperty(0, -1, dictionary);
            if (((Integer) getProperty(1)) == null) {
                setProperty(1, 2, 1200);
                return;
            }
            return;
        }
        removeProperty(0);
    }

    public void setProperty(int id, Object value) {
        if (value instanceof String) {
            setProperty(id, (String) value);
        } else if (value instanceof Long) {
            setProperty(id, ((Long) value).longValue());
        } else if (value instanceof Integer) {
            setProperty(id, ((Integer) value).intValue());
        } else if (value instanceof Short) {
            setProperty(id, ((Short) value).intValue());
        } else if (value instanceof Boolean) {
            setProperty(id, ((Boolean) value).booleanValue());
        } else if (value instanceof Date) {
            setProperty(id, 64, value);
        } else {
            throw new HPSFRuntimeException("HPSF does not support properties of type " + value.getClass().getName() + ".");
        }
    }

    public void clear() {
        Property[] properties = getProperties();
        for (Property p : properties) {
            removeProperty(p.getID());
        }
    }

    public void setCodepage(int codepage) {
        setProperty(1, 2, Integer.valueOf(codepage));
    }
}
