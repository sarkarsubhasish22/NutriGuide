package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.poifs.common.POIFSBigBlockSize;
import org.apache.poi.poifs.common.POIFSConstants;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;

public final class HeaderBlockReader {
    private final int _bat_count;
    private final byte[] _data;
    private final int _property_start;
    private final int _sbat_count;
    private final int _sbat_start;
    private final int _xbat_count;
    private final int _xbat_start;
    private final POIFSBigBlockSize bigBlockSize;

    public HeaderBlockReader(InputStream stream) throws IOException {
        byte[] blockStart = new byte[32];
        int bsCount = IOUtils.readFully(stream, blockStart);
        if (bsCount == 32) {
            long signature = LittleEndian.getLong(blockStart, 0);
            if (signature != HeaderBlockConstants._signature) {
                byte[] OOXML_FILE_HEADER = POIFSConstants.OOXML_FILE_HEADER;
                if (blockStart[0] == OOXML_FILE_HEADER[0] && blockStart[1] == OOXML_FILE_HEADER[1] && blockStart[2] == OOXML_FILE_HEADER[2] && blockStart[3] == OOXML_FILE_HEADER[3]) {
                    throw new OfficeXmlFileException("The supplied data appears to be in the Office 2007+ XML. You are calling the part of POI that deals with OLE2 Office Documents. You need to call a different part of POI to process this data (eg XSSF instead of HSSF)");
                } else if ((-31525197391593473L & signature) == 4503608217567241L) {
                    throw new IllegalArgumentException("The supplied data appears to be in BIFF2 format.  POI only supports BIFF8 format");
                } else {
                    throw new IOException("Invalid header signature; read " + longToHex(signature) + ", expected " + longToHex(HeaderBlockConstants._signature));
                }
            } else {
                byte b = blockStart[30];
                if (b == 9) {
                    this.bigBlockSize = POIFSConstants.SMALLER_BIG_BLOCK_SIZE_DETAILS;
                } else if (b == 12) {
                    this.bigBlockSize = POIFSConstants.LARGER_BIG_BLOCK_SIZE_DETAILS;
                } else {
                    throw new IOException("Unsupported blocksize  (2^" + blockStart[30] + "). Expected 2^9 or 2^12.");
                }
                byte[] bArr = new byte[this.bigBlockSize.getBigBlockSize()];
                this._data = bArr;
                System.arraycopy(blockStart, 0, bArr, 0, blockStart.length);
                int byte_count = IOUtils.readFully(stream, bArr, blockStart.length, bArr.length - blockStart.length);
                if (byte_count + bsCount == this.bigBlockSize.getBigBlockSize()) {
                    this._bat_count = getInt(44, bArr);
                    this._property_start = getInt(48, bArr);
                    this._sbat_start = getInt(60, bArr);
                    this._sbat_count = getInt(64, bArr);
                    this._xbat_start = getInt(68, bArr);
                    this._xbat_count = getInt(72, bArr);
                    return;
                }
                throw alertShortRead(byte_count, this.bigBlockSize.getBigBlockSize());
            }
        } else {
            throw alertShortRead(bsCount, 32);
        }
    }

    private static int getInt(int offset, byte[] data) {
        return LittleEndian.getInt(data, offset);
    }

    private static String longToHex(long value) {
        return new String(HexDump.longToHex(value));
    }

    private static IOException alertShortRead(int pRead, int expectedReadSize) {
        int read;
        if (pRead < 0) {
            read = 0;
        } else {
            read = pRead;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(" byte");
        sb.append(read == 1 ? "" : "s");
        String type = sb.toString();
        return new IOException("Unable to read entire header; " + read + type + " read; expected " + expectedReadSize + " bytes");
    }

    public int getPropertyStart() {
        return this._property_start;
    }

    public int getSBATStart() {
        return this._sbat_start;
    }

    public int getSBATCount() {
        return this._sbat_count;
    }

    public int getBATCount() {
        return this._bat_count;
    }

    public int[] getBATArray() {
        int[] result = new int[109];
        int offset = 76;
        for (int j = 0; j < 109; j++) {
            result[j] = LittleEndian.getInt(this._data, offset);
            offset += 4;
        }
        return result;
    }

    public int getXBATCount() {
        return this._xbat_count;
    }

    public int getXBATIndex() {
        return this._xbat_start;
    }

    public POIFSBigBlockSize getBigBlockSize() {
        return this.bigBlockSize;
    }
}
