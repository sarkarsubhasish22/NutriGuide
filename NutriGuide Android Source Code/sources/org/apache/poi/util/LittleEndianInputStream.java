package org.apache.poi.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LittleEndianInputStream extends FilterInputStream implements LittleEndianInput {
    public LittleEndianInputStream(InputStream is) {
        super(is);
    }

    public int available() {
        try {
            return super.available();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte readByte() {
        return (byte) readUByte();
    }

    public int readUByte() {
        try {
            int ch = this.in.read();
            checkEOF(ch);
            return ch;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public double readDouble() {
        return Double.longBitsToDouble(readLong());
    }

    public int readInt() {
        int ch2;
        int ch3;
        try {
            int ch1 = this.in.read();
            try {
                ch2 = this.in.read();
            } catch (IOException e) {
                e = e;
                throw new RuntimeException(e);
            }
            try {
                ch3 = this.in.read();
            } catch (IOException e2) {
                e = e2;
                throw new RuntimeException(e);
            }
            try {
                int ch4 = this.in.read();
                checkEOF(ch1 | ch2 | ch3 | ch4);
                return (ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0);
            } catch (IOException e3) {
                e = e3;
                throw new RuntimeException(e);
            }
        } catch (IOException e4) {
            e = e4;
            throw new RuntimeException(e);
        }
    }

    public long readLong() {
        int b1;
        int b2;
        int b4;
        int b5;
        try {
            int b0 = this.in.read();
            try {
                b1 = this.in.read();
                try {
                    b2 = this.in.read();
                } catch (IOException e) {
                    e = e;
                    throw new RuntimeException(e);
                }
            } catch (IOException e2) {
                e = e2;
                throw new RuntimeException(e);
            }
            try {
                int b3 = this.in.read();
                try {
                    b4 = this.in.read();
                    try {
                        b5 = this.in.read();
                    } catch (IOException e3) {
                        e = e3;
                        throw new RuntimeException(e);
                    }
                } catch (IOException e4) {
                    e = e4;
                    throw new RuntimeException(e);
                }
                try {
                    int b6 = this.in.read();
                    try {
                        int b7 = this.in.read();
                        checkEOF(b0 | b1 | b2 | b3 | b4 | b5 | b6 | b7);
                        return (((long) b7) << 56) + (((long) b6) << 48) + (((long) b5) << 40) + (((long) b4) << 32) + (((long) b3) << 24) + ((long) (b2 << 16)) + ((long) (b1 << 8)) + ((long) (b0 << 0));
                    } catch (IOException e5) {
                        e = e5;
                        throw new RuntimeException(e);
                    }
                } catch (IOException e6) {
                    e = e6;
                    throw new RuntimeException(e);
                }
            } catch (IOException e7) {
                e = e7;
                throw new RuntimeException(e);
            }
        } catch (IOException e8) {
            e = e8;
            throw new RuntimeException(e);
        }
    }

    public short readShort() {
        return (short) readUShort();
    }

    public int readUShort() {
        try {
            int ch1 = this.in.read();
            try {
                int ch2 = this.in.read();
                checkEOF(ch1 | ch2);
                return (ch2 << 8) + (ch1 << 0);
            } catch (IOException e) {
                e = e;
                throw new RuntimeException(e);
            }
        } catch (IOException e2) {
            e = e2;
            throw new RuntimeException(e);
        }
    }

    private static void checkEOF(int value) {
        if (value < 0) {
            throw new RuntimeException("Unexpected end-of-file");
        }
    }

    public void readFully(byte[] buf) {
        readFully(buf, 0, buf.length);
    }

    public void readFully(byte[] buf, int off, int len) {
        int max = off + len;
        int i = off;
        while (i < max) {
            try {
                int ch = this.in.read();
                checkEOF(ch);
                buf[i] = (byte) ch;
                i++;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
