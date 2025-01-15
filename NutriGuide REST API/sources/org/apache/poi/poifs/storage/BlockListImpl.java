package org.apache.poi.poifs.storage;

import java.io.IOException;

abstract class BlockListImpl implements BlockList {
    private BlockAllocationTableReader _bat = null;
    private ListManagedBlock[] _blocks = new ListManagedBlock[0];

    protected BlockListImpl() {
    }

    /* access modifiers changed from: protected */
    public void setBlocks(ListManagedBlock[] blocks) {
        this._blocks = blocks;
    }

    public void zap(int index) {
        if (index >= 0) {
            ListManagedBlock[] listManagedBlockArr = this._blocks;
            if (index < listManagedBlockArr.length) {
                listManagedBlockArr[index] = null;
            }
        }
    }

    /* access modifiers changed from: protected */
    public ListManagedBlock get(int index) {
        return this._blocks[index];
    }

    public ListManagedBlock remove(int index) throws IOException {
        try {
            ListManagedBlock[] listManagedBlockArr = this._blocks;
            ListManagedBlock result = listManagedBlockArr[index];
            if (result != null) {
                listManagedBlockArr[index] = null;
                return result;
            }
            throw new IOException("block[ " + index + " ] already removed - " + "does your POIFS have circular or duplicate block references?");
        } catch (ArrayIndexOutOfBoundsException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Cannot remove block[ ");
            sb.append(index);
            sb.append(" ]; out of range[ 0 - ");
            sb.append(this._blocks.length - 1);
            sb.append(" ]");
            throw new IOException(sb.toString());
        }
    }

    public ListManagedBlock[] fetchBlocks(int startBlock, int headerPropertiesStartBlock) throws IOException {
        BlockAllocationTableReader blockAllocationTableReader = this._bat;
        if (blockAllocationTableReader != null) {
            return blockAllocationTableReader.fetchBlocks(startBlock, headerPropertiesStartBlock, this);
        }
        throw new IOException("Improperly initialized list: no block allocation table provided");
    }

    public void setBAT(BlockAllocationTableReader bat) throws IOException {
        if (this._bat == null) {
            this._bat = bat;
            return;
        }
        throw new IOException("Attempt to replace existing BlockAllocationTable");
    }

    public int blockCount() {
        return this._blocks.length;
    }

    /* access modifiers changed from: protected */
    public int remainingBlocks() {
        int c = 0;
        int i = 0;
        while (true) {
            ListManagedBlock[] listManagedBlockArr = this._blocks;
            if (i >= listManagedBlockArr.length) {
                return c;
            }
            if (listManagedBlockArr[i] != null) {
                c++;
            }
            i++;
        }
    }
}
