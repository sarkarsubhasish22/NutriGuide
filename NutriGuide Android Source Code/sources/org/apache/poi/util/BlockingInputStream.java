package org.apache.poi.util;

import java.io.IOException;
import java.io.InputStream;

public class BlockingInputStream extends InputStream {
    protected InputStream is;

    public BlockingInputStream(InputStream is2) {
        this.is = is2;
    }

    public int available() throws IOException {
        return this.is.available();
    }

    public void close() throws IOException {
        this.is.close();
    }

    public void mark(int readLimit) {
        this.is.mark(readLimit);
    }

    public boolean markSupported() {
        return this.is.markSupported();
    }

    public int read() throws IOException {
        return this.is.read();
    }

    public int read(byte[] bf) throws IOException {
        int i = 0;
        int b = 4611;
        while (i < bf.length && (b = this.is.read()) != -1) {
            bf[i] = (byte) b;
            i++;
        }
        if (i == 0 && b == -1) {
            return -1;
        }
        return i;
    }

    public int read(byte[] bf, int s, int l) throws IOException {
        return this.is.read(bf, s, l);
    }

    public void reset() throws IOException {
        this.is.reset();
    }

    public long skip(long n) throws IOException {
        return this.is.skip(n);
    }
}
