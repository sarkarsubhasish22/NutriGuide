package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.poifs.common.POIFSBigBlockSize;
import org.apache.poi.util.IntList;
import org.apache.poi.util.LittleEndian;

public final class BlockAllocationTableReader {
    private static final int MAX_BLOCK_COUNT = 65535;
    private final IntList _entries;
    private POIFSBigBlockSize bigBlockSize;

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public BlockAllocationTableReader(POIFSBigBlockSize bigBlockSize2, int block_count, int[] block_array, int xbat_count, int xbat_index, BlockList raw_block_list) throws IOException {
        this(bigBlockSize2);
        int i = block_count;
        int[] iArr = block_array;
        BlockList blockList = raw_block_list;
        if (i <= 0) {
            int i2 = xbat_count;
            throw new IOException("Illegal block count; minimum count is 1, got " + i + " instead");
        } else if (i <= 65535) {
            int limit = Math.min(i, iArr.length);
            RawDataBlock[] blocks = new RawDataBlock[i];
            int block_index = 0;
            while (block_index < limit) {
                int nextOffset = iArr[block_index];
                if (nextOffset <= raw_block_list.blockCount()) {
                    blocks[block_index] = (RawDataBlock) blockList.remove(nextOffset);
                    block_index++;
                } else {
                    throw new IOException("Your file contains " + raw_block_list.blockCount() + " sectors, but the initial DIFAT array at index " + block_index + " referenced block # " + nextOffset + ". This isn't allowed and " + " your file is corrupt");
                }
            }
            if (block_index >= i) {
                int i3 = xbat_count;
            } else if (xbat_index >= 0) {
                int chain_index = xbat_index;
                int max_entries_per_block = bigBlockSize2.getXBATEntriesPerBlock();
                int chain_index_offset = bigBlockSize2.getNextXBATChainOffset();
                for (int j = 0; j < xbat_count; j++) {
                    int limit2 = Math.min(i - block_index, max_entries_per_block);
                    byte[] data = blockList.remove(chain_index).getData();
                    int offset = 0;
                    int k = 0;
                    while (k < limit2) {
                        blocks[block_index] = (RawDataBlock) blockList.remove(LittleEndian.getInt(data, offset));
                        offset += 4;
                        k++;
                        block_index++;
                    }
                    chain_index = LittleEndian.getInt(data, chain_index_offset);
                    if (chain_index == -2) {
                        break;
                    }
                }
            } else {
                int i4 = xbat_count;
                throw new IOException("BAT count exceeds limit, yet XBAT index indicates no valid entries");
            }
            if (block_index == i) {
                setEntries(blocks, blockList);
            } else {
                throw new IOException("Could not find all blocks");
            }
        } else {
            int i5 = xbat_count;
            throw new IOException("Block count " + i + " is too high. POI maximum is " + 65535 + ".");
        }
    }

    BlockAllocationTableReader(POIFSBigBlockSize bigBlockSize2, ListManagedBlock[] blocks, BlockList raw_block_list) throws IOException {
        this(bigBlockSize2);
        setEntries(blocks, raw_block_list);
    }

    BlockAllocationTableReader(POIFSBigBlockSize bigBlockSize2) {
        this.bigBlockSize = bigBlockSize2;
        this._entries = new IntList();
    }

    /* access modifiers changed from: package-private */
    public ListManagedBlock[] fetchBlocks(int startBlock, int headerPropertiesStartBlock, BlockList blockList) throws IOException {
        List<ListManagedBlock> blocks = new ArrayList<>();
        int currentBlock = startBlock;
        boolean firstPass = true;
        while (currentBlock != -2) {
            try {
                blocks.add(blockList.remove(currentBlock));
                currentBlock = this._entries.get(currentBlock);
                firstPass = false;
            } catch (IOException e) {
                if (currentBlock == headerPropertiesStartBlock) {
                    System.err.println("Warning, header block comes after data blocks in POIFS block listing");
                    currentBlock = -2;
                } else if (currentBlock != 0 || !firstPass) {
                    throw e;
                } else {
                    System.err.println("Warning, incorrectly terminated empty data blocks in POIFS block listing (should end at -2, ended at 0)");
                    currentBlock = -2;
                }
            }
        }
        return (ListManagedBlock[]) blocks.toArray(new ListManagedBlock[blocks.size()]);
    }

    /* access modifiers changed from: package-private */
    public boolean isUsed(int index) {
        try {
            return this._entries.get(index) != -1;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    public int getNextBlockIndex(int index) throws IOException {
        if (isUsed(index)) {
            return this._entries.get(index);
        }
        throw new IOException("index " + index + " is unused");
    }

    private void setEntries(ListManagedBlock[] blocks, BlockList raw_blocks) throws IOException {
        int limit = this.bigBlockSize.getBATEntriesPerBlock();
        for (int block_index = 0; block_index < blocks.length; block_index++) {
            byte[] data = blocks[block_index].getData();
            int offset = 0;
            for (int k = 0; k < limit; k++) {
                int entry = LittleEndian.getInt(data, offset);
                if (entry == -1) {
                    raw_blocks.zap(this._entries.size());
                }
                this._entries.add(entry);
                offset += 4;
            }
            blocks[block_index] = null;
        }
        raw_blocks.setBAT(this);
    }
}
