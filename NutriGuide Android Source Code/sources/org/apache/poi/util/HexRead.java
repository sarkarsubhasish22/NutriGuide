package org.apache.poi.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class HexRead {
    public static byte[] readData(String filename) throws IOException {
        FileInputStream stream = new FileInputStream(new File(filename));
        try {
            return readData((InputStream) stream, -1);
        } finally {
            stream.close();
        }
    }

    public static byte[] readData(InputStream stream, String section) throws IOException {
        try {
            StringBuffer sectionText = new StringBuffer();
            boolean inSection = false;
            int c = stream.read();
            while (c != -1) {
                if (c == 10 || c == 13) {
                    inSection = false;
                    sectionText = new StringBuffer();
                } else if (c == 91) {
                    inSection = true;
                } else if (c == 93) {
                    inSection = false;
                    if (sectionText.toString().equals(section)) {
                        return readData(stream, 91);
                    }
                    sectionText = new StringBuffer();
                } else if (inSection) {
                    sectionText.append((char) c);
                }
                c = stream.read();
            }
            stream.close();
            throw new IOException("Section '" + section + "' not found");
        } finally {
            stream.close();
        }
    }

    public static byte[] readData(String filename, String section) throws IOException {
        return readData((InputStream) new FileInputStream(new File(filename)), section);
    }

    /* JADX WARNING: Removed duplicated region for block: B:24:0x006d A[LOOP:1: B:22:0x006a->B:24:0x006d, LOOP_END] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static byte[] readData(java.io.InputStream r8, int r9) throws java.io.IOException {
        /*
            r0 = 0
            r1 = 0
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r3 = 0
        L_0x0008:
            if (r3 != 0) goto L_0x005b
            int r4 = r8.read()
            r5 = 97
            if (r4 != r9) goto L_0x0013
            goto L_0x005b
        L_0x0013:
            r6 = -1
            if (r4 == r6) goto L_0x0058
            r6 = 35
            if (r4 == r6) goto L_0x0054
            r6 = 2
            switch(r4) {
                case 48: goto L_0x003e;
                case 49: goto L_0x003e;
                case 50: goto L_0x003e;
                case 51: goto L_0x003e;
                case 52: goto L_0x003e;
                case 53: goto L_0x003e;
                case 54: goto L_0x003e;
                case 55: goto L_0x003e;
                case 56: goto L_0x003e;
                case 57: goto L_0x003e;
                default: goto L_0x001e;
            }
        L_0x001e:
            switch(r4) {
                case 65: goto L_0x0025;
                case 66: goto L_0x0025;
                case 67: goto L_0x0025;
                case 68: goto L_0x0025;
                case 69: goto L_0x0025;
                case 70: goto L_0x0025;
                default: goto L_0x0021;
            }
        L_0x0021:
            switch(r4) {
                case 97: goto L_0x0027;
                case 98: goto L_0x0027;
                case 99: goto L_0x0027;
                case 100: goto L_0x0027;
                case 101: goto L_0x0027;
                case 102: goto L_0x0027;
                default: goto L_0x0024;
            }
        L_0x0024:
            goto L_0x005a
        L_0x0025:
            r5 = 65
        L_0x0027:
            int r7 = r1 << 4
            byte r1 = (byte) r7
            int r7 = r4 + 10
            int r7 = r7 - r5
            byte r7 = (byte) r7
            int r7 = r7 + r1
            byte r1 = (byte) r7
            int r0 = r0 + 1
            if (r0 != r6) goto L_0x005a
            java.lang.Byte r6 = java.lang.Byte.valueOf(r1)
            r2.add(r6)
            r0 = 0
            r1 = 0
            goto L_0x005a
        L_0x003e:
            int r7 = r1 << 4
            byte r1 = (byte) r7
            int r7 = r4 + -48
            byte r7 = (byte) r7
            int r7 = r7 + r1
            byte r1 = (byte) r7
            int r0 = r0 + 1
            if (r0 != r6) goto L_0x005a
            java.lang.Byte r6 = java.lang.Byte.valueOf(r1)
            r2.add(r6)
            r0 = 0
            r1 = 0
            goto L_0x005a
        L_0x0054:
            readToEOL(r8)
            goto L_0x005a
        L_0x0058:
            r3 = 1
        L_0x005a:
            goto L_0x0008
        L_0x005b:
            r4 = 0
            java.lang.Byte[] r4 = new java.lang.Byte[r4]
            java.lang.Object[] r4 = r2.toArray(r4)
            java.lang.Byte[] r4 = (java.lang.Byte[]) r4
            java.lang.Byte[] r4 = (java.lang.Byte[]) r4
            int r5 = r4.length
            byte[] r5 = new byte[r5]
            r6 = 0
        L_0x006a:
            int r7 = r4.length
            if (r6 >= r7) goto L_0x0078
            r7 = r4[r6]
            byte r7 = r7.byteValue()
            r5[r6] = r7
            int r6 = r6 + 1
            goto L_0x006a
        L_0x0078:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.util.HexRead.readData(java.io.InputStream, int):byte[]");
    }

    public static byte[] readFromString(String data) {
        try {
            return readData((InputStream) new ByteArrayInputStream(data.getBytes()), -1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void readToEOL(InputStream stream) throws IOException {
        int c = stream.read();
        while (c != -1 && c != 10 && c != 13) {
            c = stream.read();
        }
    }
}
