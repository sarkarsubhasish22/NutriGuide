package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.util.List;

public class SmallDocumentBlockList extends BlockListImpl {
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

    public SmallDocumentBlockList(List blocks) {
        setBlocks((SmallDocumentBlock[]) blocks.toArray(new SmallDocumentBlock[blocks.size()]));
    }
}
