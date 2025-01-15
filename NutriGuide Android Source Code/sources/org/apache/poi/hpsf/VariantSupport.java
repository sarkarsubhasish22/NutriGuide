package org.apache.poi.hpsf;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.apache.poi.util.LittleEndian;

public class VariantSupport extends Variant {
    public static final int[] SUPPORTED_TYPES = {0, 2, 3, 20, 5, 64, 30, 31, 71, 11};
    private static boolean logUnsupportedTypes = false;
    protected static List unsupportedMessage;

    public static void setLogUnsupportedTypes(boolean logUnsupportedTypes2) {
        logUnsupportedTypes = logUnsupportedTypes2;
    }

    public static boolean isLogUnsupportedTypes() {
        return logUnsupportedTypes;
    }

    protected static void writeUnsupportedTypeMessage(UnsupportedVariantTypeException ex) {
        if (isLogUnsupportedTypes()) {
            if (unsupportedMessage == null) {
                unsupportedMessage = new LinkedList();
            }
            Long vt = Long.valueOf(ex.getVariantType());
            if (!unsupportedMessage.contains(vt)) {
                System.err.println(ex.getMessage());
                unsupportedMessage.add(vt);
            }
        }
    }

    public boolean isSupportedType(int variantType) {
        int i = 0;
        while (true) {
            int[] iArr = SUPPORTED_TYPES;
            if (i >= iArr.length) {
                return false;
            }
            if (variantType == iArr[i]) {
                return true;
            }
            i++;
        }
    }

    public static Object read(byte[] src, int offset, int length, long type, int codepage) throws ReadingNotSupportedException, UnsupportedEncodingException {
        byte[] bArr = src;
        long j = type;
        int i = codepage;
        int o1 = offset;
        int l1 = length - 4;
        long lType = type;
        if (i == 1200 && j == 30) {
            lType = 31;
        }
        int i2 = (int) lType;
        if (i2 == 0) {
            return null;
        } else if (i2 == 5) {
            return new Double(LittleEndian.getDouble(bArr, o1));
        } else if (i2 == 11) {
            if (LittleEndian.getUInt(bArr, o1) != 0) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        } else if (i2 == 20) {
            return Long.valueOf(LittleEndian.getLong(bArr, o1));
        } else if (i2 == 64) {
            return Util.filetimeToDate((int) LittleEndian.getUInt(bArr, o1 + 4), (int) LittleEndian.getUInt(bArr, o1));
        } else if (i2 == 71) {
            if (l1 < 0) {
                l1 = LittleEndian.getInt(bArr, o1);
                o1 += 4;
            }
            byte[] v = new byte[l1];
            System.arraycopy(bArr, o1, v, 0, v.length);
            Object value = v;
            return v;
        } else if (i2 == 2) {
            return Integer.valueOf(LittleEndian.getShort(bArr, o1));
        } else if (i2 == 3) {
            return Integer.valueOf(LittleEndian.getInt(bArr, o1));
        } else if (i2 == 30) {
            int first = o1 + 4;
            long last = (((long) first) + LittleEndian.getUInt(bArr, o1)) - 1;
            int o12 = o1 + 4;
            while (bArr[(int) last] == 0 && ((long) first) <= last) {
                last--;
            }
            int l = (int) ((last - ((long) first)) + 1);
            return i != -1 ? new String(bArr, first, l, codepageToEncoding(codepage)) : new String(bArr, first, l);
        } else if (i2 != 31) {
            byte[] v2 = new byte[l1];
            for (int i3 = 0; i3 < l1; i3++) {
                v2[i3] = bArr[o1 + i3];
            }
            throw new ReadingNotSupportedException(j, v2);
        } else {
            int first2 = o1 + 4;
            long last2 = (((long) first2) + LittleEndian.getUInt(bArr, o1)) - 1;
            long l2 = last2 - ((long) first2);
            int o13 = o1 + 4;
            StringBuffer b = new StringBuffer((int) (last2 - ((long) first2)));
            int i4 = 0;
            while (true) {
                long lType2 = lType;
                if (((long) i4) > l2) {
                    break;
                }
                int i1 = (i4 * 2) + o13;
                int i5 = i1;
                b.append((char) ((bArr[i1 + 1] << 8) | (bArr[i1] & 255)));
                i4++;
                lType = lType2;
                o13 = o13;
            }
            int o14 = o13;
            while (b.length() > 0 && b.charAt(b.length() - 1) == 0) {
                b.setLength(b.length() - 1);
            }
            int i6 = o14;
            return b.toString();
        }
    }

    public static String codepageToEncoding(int codepage) throws UnsupportedEncodingException {
        if (codepage <= 0) {
            throw new UnsupportedEncodingException("Codepage number may not be " + codepage);
        } else if (codepage == 1200) {
            return "UTF-16";
        } else {
            if (codepage == 1201) {
                return "UTF-16BE";
            }
            if (codepage == 10081) {
                return "MacTurkish";
            }
            if (codepage == 10082) {
                return "MacCroatian";
            }
            switch (codepage) {
                case 37:
                    return "cp037";
                case Constants.CP_SJIS:
                    return "SJIS";
                case Constants.CP_GBK:
                    return "GBK";
                case Constants.CP_MS949:
                    return "ms949";
                case Constants.CP_JOHAB:
                    return "johab";
                case Constants.CP_MAC_ROMANIA:
                    return "MacRomania";
                case Constants.CP_MAC_UKRAINE:
                    return "MacUkraine";
                case Constants.CP_MAC_THAI:
                    return "MacThai";
                case Constants.CP_MAC_CENTRAL_EUROPE:
                    return "MacCentralEurope";
                case Constants.CP_MAC_ICELAND:
                    return "MacIceland";
                case Constants.CP_US_ACSII:
                    return "US-ASCII";
                case Constants.CP_KOI8_R:
                    return "KOI8-R";
                case Constants.CP_ISO_2022_KR:
                    return "ISO-2022-KR";
                case Constants.CP_EUC_JP:
                    return "EUC-JP";
                case Constants.CP_EUC_KR:
                    return "EUC-KR";
                case Constants.CP_GB2312:
                    return "GB2312";
                case Constants.CP_GB18030:
                    return "GB18030";
                default:
                    switch (codepage) {
                        case Constants.CP_WINDOWS_1250:
                            return "windows-1250";
                        case Constants.CP_WINDOWS_1251:
                            return "windows-1251";
                        case Constants.CP_WINDOWS_1252:
                            return "windows-1252";
                        case Constants.CP_WINDOWS_1253:
                            return "windows-1253";
                        case Constants.CP_WINDOWS_1254:
                            return "windows-1254";
                        case Constants.CP_WINDOWS_1255:
                            return "windows-1255";
                        case Constants.CP_WINDOWS_1256:
                            return "windows-1256";
                        case Constants.CP_WINDOWS_1257:
                            return "windows-1257";
                        case Constants.CP_WINDOWS_1258:
                            return "windows-1258";
                        default:
                            switch (codepage) {
                                case Constants.CP_MAC_ROMAN:
                                    return "MacRoman";
                                case Constants.CP_MAC_JAPAN:
                                    return "SJIS";
                                case Constants.CP_MAC_CHINESE_TRADITIONAL:
                                    return "Big5";
                                case Constants.CP_MAC_KOREAN:
                                    return "EUC-KR";
                                case Constants.CP_MAC_ARABIC:
                                    return "MacArabic";
                                case Constants.CP_MAC_HEBREW:
                                    return "MacHebrew";
                                case Constants.CP_MAC_GREEK:
                                    return "MacGreek";
                                case Constants.CP_MAC_CYRILLIC:
                                    return "MacCyrillic";
                                case Constants.CP_MAC_CHINESE_SIMPLE:
                                    return "EUC_CN";
                                default:
                                    switch (codepage) {
                                        case Constants.CP_ISO_8859_1:
                                            return "ISO-8859-1";
                                        case Constants.CP_ISO_8859_2:
                                            return "ISO-8859-2";
                                        case Constants.CP_ISO_8859_3:
                                            return "ISO-8859-3";
                                        case Constants.CP_ISO_8859_4:
                                            return "ISO-8859-4";
                                        case Constants.CP_ISO_8859_5:
                                            return "ISO-8859-5";
                                        case Constants.CP_ISO_8859_6:
                                            return "ISO-8859-6";
                                        case Constants.CP_ISO_8859_7:
                                            return "ISO-8859-7";
                                        case Constants.CP_ISO_8859_8:
                                            return "ISO-8859-8";
                                        case Constants.CP_ISO_8859_9:
                                            return "ISO-8859-9";
                                        default:
                                            switch (codepage) {
                                                case Constants.CP_ISO_2022_JP1:
                                                case Constants.CP_ISO_2022_JP2:
                                                case Constants.CP_ISO_2022_JP3:
                                                    return "ISO-2022-JP";
                                                default:
                                                    switch (codepage) {
                                                        case Constants.CP_US_ASCII2:
                                                            return "US-ASCII";
                                                        case Constants.CP_UTF8:
                                                            return "UTF-8";
                                                        default:
                                                            return "cp" + codepage;
                                                    }
                                            }
                                    }
                            }
                    }
            }
        }
    }

    public static int write(OutputStream out, long type, Object value, int codepage) throws IOException, WritingNotSupportedException {
        int trueOrFalse;
        int i = (int) type;
        if (i == 0) {
            TypeWriter.writeUIntToStream(out, 0);
            return 4;
        } else if (i == 5) {
            return 0 + TypeWriter.writeToStream(out, ((Double) value).doubleValue());
        } else {
            if (i == 11) {
                if (((Boolean) value).booleanValue()) {
                    trueOrFalse = 1;
                } else {
                    trueOrFalse = 0;
                }
                return TypeWriter.writeUIntToStream(out, (long) trueOrFalse);
            } else if (i == 20) {
                TypeWriter.writeToStream(out, ((Long) value).longValue());
                return 8;
            } else if (i == 64) {
                long filetime = Util.dateToFileTime((Date) value);
                return 0 + TypeWriter.writeUIntToStream(out, ((long) ((int) (filetime & 4294967295L))) & 4294967295L) + TypeWriter.writeUIntToStream(out, 4294967295L & ((long) ((int) ((filetime >> 32) & 4294967295L))));
            } else if (i == 71) {
                byte[] b = (byte[]) value;
                out.write(b);
                return b.length;
            } else if (i == 2) {
                TypeWriter.writeToStream(out, ((Integer) value).shortValue());
                return 2;
            } else if (i != 3) {
                if (i == 30) {
                    byte[] bytes = codepage == -1 ? ((String) value).getBytes() : ((String) value).getBytes(codepageToEncoding(codepage));
                    int length = TypeWriter.writeUIntToStream(out, (long) (bytes.length + 1));
                    byte[] b2 = new byte[(bytes.length + 1)];
                    System.arraycopy(bytes, 0, b2, 0, bytes.length);
                    b2[b2.length - 1] = 0;
                    out.write(b2);
                    return length + b2.length;
                } else if (i == 31) {
                    int length2 = 0 + TypeWriter.writeUIntToStream(out, (long) (((String) value).length() + 1));
                    char[] s = Util.pad4((String) value);
                    for (int i2 = 0; i2 < s.length; i2++) {
                        out.write((byte) (s[i2] & 255));
                        out.write((byte) ((s[i2] & 65280) >> 8));
                        length2 += 2;
                    }
                    out.write(0);
                    out.write(0);
                    return length2 + 2;
                } else if (value instanceof byte[]) {
                    byte[] b3 = (byte[]) value;
                    out.write(b3);
                    int length3 = b3.length;
                    writeUnsupportedTypeMessage(new WritingNotSupportedException(type, value));
                    return length3;
                } else {
                    throw new WritingNotSupportedException(type, value);
                }
            } else if (value instanceof Integer) {
                return 0 + TypeWriter.writeToStream(out, ((Integer) value).intValue());
            } else {
                throw new ClassCastException("Could not cast an object to " + Integer.class.toString() + ": " + value.getClass().toString() + ", " + value.toString());
            }
        }
    }
}
