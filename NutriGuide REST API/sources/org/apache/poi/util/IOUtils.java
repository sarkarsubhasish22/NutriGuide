package org.apache.poi.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class IOUtils {
    private IOUtils() {
    }

    public static byte[] toByteArray(InputStream stream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read = 0;
        while (read != -1) {
            read = stream.read(buffer);
            if (read > 0) {
                baos.write(buffer, 0, read);
            }
        }
        return baos.toByteArray();
    }

    public static int readFully(InputStream in, byte[] b) throws IOException {
        return readFully(in, b, 0, b.length);
    }

    public static int readFully(InputStream in, byte[] b, int off, int len) throws IOException {
        int total = 0;
        do {
            int got = in.read(b, off + total, len - total);
            if (got >= 0) {
                total += got;
            } else if (total == 0) {
                return -1;
            } else {
                return total;
            }
        } while (total != len);
        return total;
    }

    public static void copy(InputStream inp, OutputStream out) throws IOException {
        byte[] buff = new byte[4096];
        while (true) {
            int read = inp.read(buff);
            int count = read;
            if (read == -1) {
                return;
            }
            if (count > 0) {
                out.write(buff, 0, count);
            }
        }
    }
}
