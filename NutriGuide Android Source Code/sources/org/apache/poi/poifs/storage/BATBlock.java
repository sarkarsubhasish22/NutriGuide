package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import org.apache.poi.poifs.common.POIFSBigBlockSize;
import org.apache.poi.util.IntegerField;

public final class BATBlock extends BigBlock {
    private static final byte _default_value = -1;
    private byte[] _data;
    private IntegerField[] _fields;

    public /* bridge */ /* synthetic */ void writeBlocks(OutputStream x0) throws IOException {
        super.writeBlocks(x0);
    }

    private BATBlock(POIFSBigBlockSize bigBlockSize) {
        super(bigBlockSize);
        int _entries_per_block = bigBlockSize.getBATEntriesPerBlock();
        byte[] bArr = new byte[bigBlockSize.getBigBlockSize()];
        this._data = bArr;
        Arrays.fill(bArr, _default_value);
        this._fields = new IntegerField[_entries_per_block];
        int offset = 0;
        for (int j = 0; j < _entries_per_block; j++) {
            this._fields[j] = new IntegerField(offset);
            offset += 4;
        }
    }

    public static BATBlock[] createBATBlocks(POIFSBigBlockSize bigBlockSize, int[] entries) {
        BATBlock[] blocks = new BATBlock[calculateStorageRequirements(bigBlockSize, entries.length)];
        int index = 0;
        int remaining = entries.length;
        int _entries_per_block = bigBlockSize.getBATEntriesPerBlock();
        int j = 0;
        while (j < entries.length) {
            int index2 = index + 1;
            blocks[index] = new BATBlock(bigBlockSize, entries, j, remaining > _entries_per_block ? j + _entries_per_block : entries.length);
            remaining -= _entries_per_block;
            j += _entries_per_block;
            index = index2;
        }
        return blocks;
    }

    public static BATBlock[] createXBATBlocks(POIFSBigBlockSize bigBlockSize, int[] entries, int startBlock) {
        int block_count = calculateXBATStorageRequirements(bigBlockSize, entries.length);
        BATBlock[] blocks = new BATBlock[block_count];
        int index = 0;
        int remaining = entries.length;
        int _entries_per_xbat_block = bigBlockSize.getXBATEntriesPerBlock();
        if (block_count != 0) {
            int j = 0;
            while (j < entries.length) {
                int index2 = index + 1;
                blocks[index] = new BATBlock(bigBlockSize, entries, j, remaining > _entries_per_xbat_block ? j + _entries_per_xbat_block : entries.length);
                remaining -= _entries_per_xbat_block;
                j += _entries_per_xbat_block;
                index = index2;
            }
            int index3 = 0;
            while (index3 < blocks.length - 1) {
                blocks[index3].setXBATChain(bigBlockSize, startBlock + index3 + 1);
                index3++;
            }
            blocks[index3].setXBATChain(bigBlockSize, -2);
        }
        return blocks;
    }

    public static int calculateStorageRequirements(POIFSBigBlockSize bigBlockSize, int entryCount) {
        int _entries_per_block = bigBlockSize.getBATEntriesPerBlock();
        return ((entryCount + _entries_per_block) - 1) / _entries_per_block;
    }

    public static int calculateXBATStorageRequirements(POIFSBigBlockSize bigBlockSize, int entryCount) {
        int _entries_per_xbat_block = bigBlockSize.getXBATEntriesPerBlock();
        return ((entryCount + _entries_per_xbat_block) - 1) / _entries_per_xbat_block;
    }

    private void setXBATChain(POIFSBigBlockSize bigBlockSize, int chainIndex) {
        this._fields[bigBlockSize.getXBATEntriesPerBlock()].set(chainIndex, this._data);
    }

    private BATBlock(POIFSBigBlockSize bigBlockSize, int[] entries, int start_index, int end_index) {
        this(bigBlockSize);
        for (int k = start_index; k < end_index; k++) {
            this._fields[k - start_index].set(entries[k], this._data);
        }
    }

    /* access modifiers changed from: package-private */
    public void writeData(OutputStream stream) throws IOException {
        doWriteData(stream, this._data);
    }
}
