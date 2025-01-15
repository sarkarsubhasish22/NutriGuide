package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import org.apache.poi.poifs.common.POIFSBigBlockSize;
import org.apache.poi.util.IntegerField;
import org.apache.poi.util.LongField;
import org.apache.poi.util.ShortField;

public class HeaderBlockWriter extends BigBlock implements HeaderBlockConstants {
    private static final byte _default_value = -1;
    private IntegerField _bat_count;
    private byte[] _data;
    private IntegerField _property_start;
    private IntegerField _sbat_block_count;
    private IntegerField _sbat_start;
    private IntegerField _xbat_count;
    private IntegerField _xbat_start;

    public /* bridge */ /* synthetic */ void writeBlocks(OutputStream x0) throws IOException {
        super.writeBlocks(x0);
    }

    public HeaderBlockWriter(POIFSBigBlockSize bigBlockSize) {
        super(bigBlockSize);
        byte[] bArr = new byte[bigBlockSize.getBigBlockSize()];
        this._data = bArr;
        Arrays.fill(bArr, _default_value);
        new LongField(0, HeaderBlockConstants._signature, this._data);
        new IntegerField(8, 0, this._data);
        new IntegerField(12, 0, this._data);
        new IntegerField(16, 0, this._data);
        new IntegerField(20, 0, this._data);
        new ShortField(24, 59, this._data);
        new ShortField(26, 3, this._data);
        new ShortField(28, -2, this._data);
        new ShortField(30, bigBlockSize.getHeaderValue(), this._data);
        if (bigBlockSize.getBigBlockSize() == 4096) {
            int i = 512;
            while (true) {
                byte[] bArr2 = this._data;
                if (i >= bArr2.length) {
                    break;
                }
                bArr2[i] = 0;
                i++;
            }
        }
        new IntegerField(32, 6, this._data);
        new IntegerField(36, 0, this._data);
        new IntegerField(40, 0, this._data);
        this._bat_count = new IntegerField(44, 0, this._data);
        this._property_start = new IntegerField(48, -2, this._data);
        new IntegerField(52, 0, this._data);
        new IntegerField(56, 4096, this._data);
        this._sbat_start = new IntegerField(60, -2, this._data);
        this._sbat_block_count = new IntegerField(64, 0, this._data);
        this._xbat_start = new IntegerField(68, -2, this._data);
        this._xbat_count = new IntegerField(72, 0, this._data);
    }

    public BATBlock[] setBATBlocks(int blockCount, int startBlock) {
        BATBlock[] rvalue;
        this._bat_count.set(blockCount, this._data);
        int limit = Math.min(blockCount, 109);
        int offset = 76;
        for (int j = 0; j < limit; j++) {
            new IntegerField(offset, startBlock + j, this._data);
            offset += 4;
        }
        if (blockCount > 109) {
            int excess_blocks = blockCount - 109;
            int[] excess_block_array = new int[excess_blocks];
            for (int j2 = 0; j2 < excess_blocks; j2++) {
                excess_block_array[j2] = startBlock + j2 + 109;
            }
            rvalue = BATBlock.createXBATBlocks(this.bigBlockSize, excess_block_array, startBlock + blockCount);
            this._xbat_start.set(startBlock + blockCount, this._data);
        } else {
            rvalue = BATBlock.createXBATBlocks(this.bigBlockSize, new int[0], 0);
            this._xbat_start.set(-2, this._data);
        }
        this._xbat_count.set(rvalue.length, this._data);
        return rvalue;
    }

    public void setPropertyStart(int startBlock) {
        this._property_start.set(startBlock, this._data);
    }

    public void setSBATStart(int startBlock) {
        this._sbat_start.set(startBlock, this._data);
    }

    public void setSBATBlockCount(int count) {
        this._sbat_block_count.set(count, this._data);
    }

    static int calculateXBATStorageRequirements(POIFSBigBlockSize bigBlockSize, int blockCount) {
        if (blockCount > 109) {
            return BATBlock.calculateXBATStorageRequirements(bigBlockSize, blockCount - 109);
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public void writeData(OutputStream stream) throws IOException {
        doWriteData(stream, this._data);
    }
}
