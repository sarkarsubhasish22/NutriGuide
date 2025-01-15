package org.apache.poi.poifs.dev;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.apache.poi.poifs.common.POIFSBigBlockSize;
import org.apache.poi.poifs.property.PropertyTable;
import org.apache.poi.poifs.storage.BlockAllocationTableReader;
import org.apache.poi.poifs.storage.BlockList;
import org.apache.poi.poifs.storage.HeaderBlockReader;
import org.apache.poi.poifs.storage.ListManagedBlock;
import org.apache.poi.poifs.storage.RawDataBlockList;
import org.apache.poi.poifs.storage.SmallBlockTableReader;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.IntList;

public class POIFSHeaderDumper {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Must specify at least one file to view");
            System.exit(1);
        }
        for (String viewFile : args) {
            viewFile(viewFile);
        }
    }

    public static void viewFile(String filename) throws Exception {
        InputStream inp = new FileInputStream(filename);
        HeaderBlockReader header_block_reader = new HeaderBlockReader(inp);
        displayHeader(header_block_reader);
        POIFSBigBlockSize bigBlockSize = header_block_reader.getBigBlockSize();
        RawDataBlockList data_blocks = new RawDataBlockList(inp, bigBlockSize);
        displayRawBlocksSummary(data_blocks);
        displayBATReader(new BlockAllocationTableReader(header_block_reader.getBigBlockSize(), header_block_reader.getBATCount(), header_block_reader.getBATArray(), header_block_reader.getXBATCount(), header_block_reader.getXBATIndex(), data_blocks));
        BlockList smallDocumentBlocks = SmallBlockTableReader.getSmallDocumentBlocks(bigBlockSize, data_blocks, new PropertyTable(header_block_reader.getBigBlockSize(), header_block_reader.getPropertyStart(), data_blocks).getRoot(), header_block_reader.getSBATStart());
    }

    public static void displayHeader(HeaderBlockReader header_block_reader) throws Exception {
        System.out.println("Header Details:");
        PrintStream printStream = System.out;
        printStream.println(" Block size: " + header_block_reader.getBigBlockSize());
        PrintStream printStream2 = System.out;
        printStream2.println(" BAT (FAT) header blocks: " + header_block_reader.getBATArray().length);
        PrintStream printStream3 = System.out;
        printStream3.println(" BAT (FAT) block count: " + header_block_reader.getBATCount());
        PrintStream printStream4 = System.out;
        printStream4.println(" XBAT (FAT) block count: " + header_block_reader.getXBATCount());
        PrintStream printStream5 = System.out;
        printStream5.println(" XBAT (FAT) block 1 at: " + header_block_reader.getXBATIndex());
        PrintStream printStream6 = System.out;
        printStream6.println(" SBAT (MiniFAT) block count: " + header_block_reader.getSBATCount());
        PrintStream printStream7 = System.out;
        printStream7.println(" SBAT (MiniFAT) block 1 at: " + header_block_reader.getSBATStart());
        PrintStream printStream8 = System.out;
        printStream8.println(" Property table at: " + header_block_reader.getPropertyStart());
        System.out.println("");
    }

    public static void displayRawBlocksSummary(RawDataBlockList data_blocks) throws Exception {
        System.out.println("Raw Blocks Details:");
        PrintStream printStream = System.out;
        printStream.println(" Number of blocks: " + data_blocks.blockCount());
        Method gbm = data_blocks.getClass().getSuperclass().getDeclaredMethod("get", new Class[]{Integer.TYPE});
        gbm.setAccessible(true);
        for (int i = 0; i < Math.min(16, data_blocks.blockCount()); i++) {
            ListManagedBlock block = (ListManagedBlock) gbm.invoke(data_blocks, new Object[]{new Integer(i)});
            byte[] data = new byte[Math.min(48, block.getData().length)];
            System.arraycopy(block.getData(), 0, data, 0, data.length);
            PrintStream printStream2 = System.out;
            printStream2.println(" Block #" + i + ":");
            System.out.println(HexDump.dump(data, 0, 0));
        }
        System.out.println("");
    }

    public static void displayBATReader(BlockAllocationTableReader batReader) throws Exception {
        System.out.println("Sectors, as referenced from the FAT:");
        Field entriesF = batReader.getClass().getDeclaredField("_entries");
        entriesF.setAccessible(true);
        IntList entries = (IntList) entriesF.get(batReader);
        for (int i = 0; i < entries.size(); i++) {
            int bn = entries.get(i);
            String bnS = Integer.toString(bn);
            if (bn == -2) {
                bnS = "End Of Chain";
            } else if (bn == -4) {
                bnS = "DI Fat Block";
            } else if (bn == -3) {
                bnS = "Normal Fat Block";
            } else if (bn == -1) {
                bnS = "Block Not Used (Free)";
            }
            PrintStream printStream = System.out;
            printStream.println("  Block  # " + i + " -> " + bnS);
        }
        System.out.println("");
    }
}
