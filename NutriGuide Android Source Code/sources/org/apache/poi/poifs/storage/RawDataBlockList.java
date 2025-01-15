package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.poifs.common.POIFSBigBlockSize;

public class RawDataBlockList extends BlockListImpl {
    public /* bridge */ /* synthetic */ int blockCount() {
        return super.blockCount();
    }

    public /* bridge */ /* synthetic */ ListManagedBlock[] fetchBlocks(int x0, int x1) throws IOException {
        return super.fetchBlocks(x0, x1);
    }

    public /* bridge */ /* synthetic */ ListManagedBlock remove(int x0) throws IOException {
        return super.remove(x0);
    }

    public /* bridge */ /* synthetic */ void setBAT(BlockAllocationTableReader x0) throws IOException {
        super.setBAT(x0);
    }

    public /* bridge */ /* synthetic */ void zap(int x0) {
        super.zap(x0);
    }

    public RawDataBlockList(InputStream stream, POIFSBigBlockSize bigBlockSize) throws IOException {
        RawDataBlock block;
        List<RawDataBlock> blocks = new ArrayList<>();
        do {
            block = new RawDataBlock(stream, bigBlockSize.getBigBlockSize());
            if (block.hasData()) {
                blocks.add(block);
            }
        } while (!block.eof());
        setBlocks((ListManagedBlock[]) blocks.toArray(new RawDataBlock[blocks.size()]));
    }
}
