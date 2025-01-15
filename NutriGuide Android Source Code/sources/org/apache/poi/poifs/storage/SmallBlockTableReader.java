package org.apache.poi.poifs.storage;

import java.io.IOException;
import org.apache.poi.poifs.common.POIFSBigBlockSize;
import org.apache.poi.poifs.property.RootProperty;

public final class SmallBlockTableReader {
    public static BlockList getSmallDocumentBlocks(POIFSBigBlockSize bigBlockSize, RawDataBlockList blockList, RootProperty root, int sbatStart) throws IOException {
        BlockList list = new SmallDocumentBlockList(SmallDocumentBlock.extract(bigBlockSize, blockList.fetchBlocks(root.getStartBlock(), -1)));
        new BlockAllocationTableReader(bigBlockSize, blockList.fetchBlocks(sbatStart, -1), list);
        return list;
    }
}
