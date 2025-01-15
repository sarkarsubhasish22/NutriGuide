package org.apache.poi.hpsf;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

public class Property {
    protected long id;
    protected long type;
    protected Object value;

    public long getID() {
        return this.id;
    }

    public long getType() {
        return this.type;
    }

    public Object getValue() {
        return this.value;
    }

    public Property(long id2, long type2, Object value2) {
        this.id = id2;
        this.type = type2;
        this.value = value2;
    }

    public Property(long id2, byte[] src, long offset, int length, int codepage) throws UnsupportedEncodingException {
        long j = id2;
        this.id = j;
        if (j == 0) {
            this.value = readDictionary(src, offset, length, codepage);
            return;
        }
        int o = (int) offset;
        long uInt = LittleEndian.getUInt(src, o);
        this.type = uInt;
        try {
            this.value = VariantSupport.read(src, o + 4, length, (long) ((int) uInt), codepage);
        } catch (UnsupportedVariantTypeException ex) {
            VariantSupport.writeUnsupportedTypeMessage(ex);
            this.value = ex.getValue();
        }
    }

    protected Property() {
    }

    /* access modifiers changed from: protected */
    public Map readDictionary(byte[] src, long offset, int length, int codepage) throws UnsupportedEncodingException {
        long nrEntries;
        byte[] bArr = src;
        long j = offset;
        int i = codepage;
        if (j < 0 || j > ((long) bArr.length)) {
            throw new HPSFRuntimeException("Illegal offset " + j + " while HPSF stream contains " + length + " bytes.");
        }
        int o = (int) j;
        long nrEntries2 = LittleEndian.getUInt(bArr, o);
        Map m = new HashMap((int) nrEntries2, 1.0f);
        int o2 = o + 4;
        int i2 = 0;
        while (((long) i2) < nrEntries2) {
            try {
                Long id2 = Long.valueOf(LittleEndian.getUInt(bArr, o2));
                int o3 = o2 + 4;
                long sLength = LittleEndian.getUInt(bArr, o3);
                int o4 = o3 + 4;
                StringBuffer b = new StringBuffer();
                if (i == -1) {
                    nrEntries = nrEntries2;
                    b.append(new String(bArr, o4, (int) sLength));
                } else if (i != 1200) {
                    try {
                        b.append(new String(bArr, o4, (int) sLength, VariantSupport.codepageToEncoding(codepage)));
                        nrEntries = nrEntries2;
                    } catch (RuntimeException e) {
                        ex = e;
                        long j2 = nrEntries2;
                        POILogFactory.getLogger((Class) getClass()).log(POILogger.WARN, (Object) "The property set's dictionary contains bogus data. All dictionary entries starting with the one with ID " + this.id + " will be ignored.", (Throwable) ex);
                        return m;
                    }
                } else {
                    nrEntries = nrEntries2;
                    int nrBytes = (int) (sLength * 2);
                    try {
                        byte[] h = new byte[nrBytes];
                        for (int i22 = 0; i22 < nrBytes; i22 += 2) {
                            h[i22] = bArr[o4 + i22 + 1];
                            h[i22 + 1] = bArr[o4 + i22];
                        }
                        b.append(new String(h, 0, nrBytes, VariantSupport.codepageToEncoding(codepage)));
                    } catch (RuntimeException e2) {
                        ex = e2;
                        POILogFactory.getLogger((Class) getClass()).log(POILogger.WARN, (Object) "The property set's dictionary contains bogus data. All dictionary entries starting with the one with ID " + this.id + " will be ignored.", (Throwable) ex);
                        return m;
                    }
                }
                while (b.length() > 0 && b.charAt(b.length() - 1) == 0) {
                    b.setLength(b.length() - 1);
                }
                if (i == 1200) {
                    if (sLength % 2 == 1) {
                        sLength++;
                    }
                    o2 = (int) (((long) o4) + sLength + sLength);
                } else {
                    o2 = (int) (((long) o4) + sLength);
                }
                m.put(id2, b.toString());
                i2++;
                nrEntries2 = nrEntries;
            } catch (RuntimeException e3) {
                ex = e3;
                long j3 = nrEntries2;
                POILogFactory.getLogger((Class) getClass()).log(POILogger.WARN, (Object) "The property set's dictionary contains bogus data. All dictionary entries starting with the one with ID " + this.id + " will be ignored.", (Throwable) ex);
                return m;
            }
        }
        long j4 = nrEntries2;
        return m;
    }

    /* access modifiers changed from: protected */
    public int getSize() throws WritingNotSupportedException {
        int length = VariantSupport.getVariantLength(this.type);
        if (length >= 0) {
            return length;
        }
        if (length != -2) {
            int i = (int) this.type;
            if (i == 0) {
                return length;
            }
            if (i == 30) {
                int l = ((String) this.value).length() + 1;
                int r = l % 4;
                if (r > 0) {
                    l += 4 - r;
                }
                return length + l;
            }
            throw new WritingNotSupportedException(this.type, this.value);
        }
        throw new WritingNotSupportedException(this.type, (Object) null);
    }

    public boolean equals(Object o) {
        if (!(o instanceof Property)) {
            return false;
        }
        Property p = (Property) o;
        Object pValue = p.getValue();
        long pId = p.getID();
        long j = this.id;
        if (j != pId || (j != 0 && !typesAreEqual(this.type, p.getType()))) {
            return false;
        }
        Object obj = this.value;
        if (obj == null && pValue == null) {
            return true;
        }
        if (obj == null || pValue == null) {
            return false;
        }
        Class<?> valueClass = obj.getClass();
        Class<?> pValueClass = pValue.getClass();
        if (!valueClass.isAssignableFrom(pValueClass) && !pValueClass.isAssignableFrom(valueClass)) {
            return false;
        }
        Object obj2 = this.value;
        if (obj2 instanceof byte[]) {
            return Util.equal((byte[]) obj2, (byte[]) pValue);
        }
        return obj2.equals(pValue);
    }

    private boolean typesAreEqual(long t1, long t2) {
        if (t1 == t2) {
            return true;
        }
        if (t1 == 30 && t2 == 31) {
            return true;
        }
        if (t2 == 30 && t1 == 31) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        long hashCode = 0 + this.id + this.type;
        Object obj = this.value;
        if (obj != null) {
            hashCode += (long) obj.hashCode();
        }
        return (int) (4294967295L & hashCode);
    }

    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append(getClass().getName());
        b.append('[');
        b.append("id: ");
        b.append(getID());
        b.append(", type: ");
        b.append(getType());
        Object value2 = getValue();
        b.append(", value: ");
        b.append(value2.toString());
        if (value2 instanceof String) {
            String s = (String) value2;
            int l = s.length();
            byte[] bytes = new byte[(l * 2)];
            for (int i = 0; i < l; i++) {
                char c = s.charAt(i);
                bytes[i * 2] = (byte) ((65280 & c) >> 8);
                bytes[(i * 2) + 1] = (byte) ((c & 255) >> 0);
            }
            String hex = HexDump.dump(bytes, 0, 0);
            b.append(" [");
            b.append(hex);
            b.append("]");
        }
        b.append(']');
        return b.toString();
    }
}
