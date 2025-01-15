package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

public class RawDataBlock implements ListManagedBlock {
    private static POILogger log = POILogFactory.getLogger(RawDataBlock.class);
    private byte[] _data;
    private boolean _eof;
    private boolean _hasData;

    public RawDataBlock(InputStream stream) throws IOException {
        this(stream, 512);
    }

    public RawDataBlock(InputStream stream, int blockSize) throws IOException {
        byte[] bArr = new byte[blockSize];
        this._data = bArr;
        int count = IOUtils.readFully(stream, bArr);
        this._hasData = count > 0;
        if (count == -1) {
            this._eof = true;
        } else if (count != blockSize) {
            this._eof = true;
            StringBuilder sb = new StringBuilder();
            sb.append(" byte");
            sb.append(count == 1 ? "" : "s");
            String type = sb.toString();
            POILogger pOILogger = log;
            int i = POILogger.ERROR;
            pOILogger.log(i, (Object) "Unable to read entire block; " + count + type + " read before EOF; expected " + blockSize + " bytes. Your document " + "was either written by software that " + "ignores the spec, or has been truncated!");
        } else {
            this._eof = false;
        }
    }

    public boolean eof() {
        return this._eof;
    }

    public boolean hasData() {
        return this._hasData;
    }

    public String toString() {
        return "RawDataBlock of size " + this._data.length;
    }

    public byte[] getData() throws IOException {
        if (hasData()) {
            return this._data;
        }
        throw new IOException("Cannot return empty data");
    }

    public int getBigBlockSize() {
        return this._data.length;
    }
}
