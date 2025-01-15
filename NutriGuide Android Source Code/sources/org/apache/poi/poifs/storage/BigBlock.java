package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.poifs.common.POIFSBigBlockSize;

abstract class BigBlock implements BlockWritable {
    protected POIFSBigBlockSize bigBlockSize;

    /* access modifiers changed from: package-private */
    public abstract void writeData(OutputStream outputStream) throws IOException;

    protected BigBlock(POIFSBigBlockSize bigBlockSize2) {
        this.bigBlockSize = bigBlockSize2;
    }

    /* access modifiers changed from: protected */
    public void doWriteData(OutputStream stream, byte[] data) throws IOException {
        stream.write(data);
    }

    public void writeBlocks(OutputStream stream) throws IOException {
        writeData(stream);
    }
}
