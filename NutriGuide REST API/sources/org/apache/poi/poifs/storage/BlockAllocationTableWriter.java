package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.poifs.common.POIFSBigBlockSize;
import org.apache.poi.poifs.filesystem.BATManaged;
import org.apache.poi.util.IntList;

public final class BlockAllocationTableWriter implements BlockWritable, BATManaged {
    private POIFSBigBlockSize _bigBlockSize;
    private BATBlock[] _blocks = new BATBlock[0];
    private IntList _entries = new IntList();
    private int _start_block = -2;

    public BlockAllocationTableWriter(POIFSBigBlockSize bigBlockSize) {
        this._bigBlockSize = bigBlockSize;
    }

    public int createBlocks() {
        int xbat_blocks = 0;
        int bat_blocks = 0;
        while (true) {
            int calculated_bat_blocks = BATBlock.calculateStorageRequirements(this._bigBlockSize, bat_blocks + xbat_blocks + this._entries.size());
            int calculated_xbat_blocks = HeaderBlockWriter.calculateXBATStorageRequirements(this._bigBlockSize, calculated_bat_blocks);
            if (bat_blocks == calculated_bat_blocks && xbat_blocks == calculated_xbat_blocks) {
                int startBlock = allocateSpace(bat_blocks);
                allocateSpace(xbat_blocks);
                simpleCreateBlocks();
                return startBlock;
            }
            bat_blocks = calculated_bat_blocks;
            xbat_blocks = calculated_xbat_blocks;
        }
    }

    public int allocateSpace(int blockCount) {
        int startBlock = this._entries.size();
        if (blockCount > 0) {
            int limit = blockCount - 1;
            int index = startBlock + 1;
            int k = 0;
            while (k < limit) {
                this._entries.add(index);
                k++;
                index++;
            }
            this._entries.add(-2);
        }
        return startBlock;
    }

    public int getStartBlock() {
        return this._start_block;
    }

    /* access modifiers changed from: package-private */
    public void simpleCreateBlocks() {
        this._blocks = BATBlock.createBATBlocks(this._bigBlockSize, this._entries.toArray());
    }

    public void writeBlocks(OutputStream stream) throws IOException {
        int j = 0;
        while (true) {
            BATBlock[] bATBlockArr = this._blocks;
            if (j < bATBlockArr.length) {
                bATBlockArr[j].writeBlocks(stream);
                j++;
            } else {
                return;
            }
        }
    }

    public int countBlocks() {
        return this._blocks.length;
    }

    public void setStartBlock(int start_block) {
        this._start_block = start_block;
    }
}
