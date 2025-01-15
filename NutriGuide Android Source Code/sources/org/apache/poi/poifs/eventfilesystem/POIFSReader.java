package org.apache.poi.poifs.eventfilesystem;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Iterator;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSDocument;
import org.apache.poi.poifs.filesystem.POIFSDocumentPath;
import org.apache.poi.poifs.property.DirectoryProperty;
import org.apache.poi.poifs.property.Property;
import org.apache.poi.poifs.property.PropertyTable;
import org.apache.poi.poifs.storage.BlockAllocationTableReader;
import org.apache.poi.poifs.storage.BlockList;
import org.apache.poi.poifs.storage.HeaderBlockReader;
import org.apache.poi.poifs.storage.RawDataBlockList;
import org.apache.poi.poifs.storage.SmallBlockTableReader;

public class POIFSReader {
    private POIFSReaderRegistry registry = new POIFSReaderRegistry();
    private boolean registryClosed = false;

    public void read(InputStream stream) throws IOException {
        this.registryClosed = true;
        HeaderBlockReader header_block_reader = new HeaderBlockReader(stream);
        RawDataBlockList data_blocks = new RawDataBlockList(stream, header_block_reader.getBigBlockSize());
        new BlockAllocationTableReader(header_block_reader.getBigBlockSize(), header_block_reader.getBATCount(), header_block_reader.getBATArray(), header_block_reader.getXBATCount(), header_block_reader.getXBATIndex(), data_blocks);
        PropertyTable properties = new PropertyTable(header_block_reader.getBigBlockSize(), header_block_reader.getPropertyStart(), data_blocks);
        processProperties(SmallBlockTableReader.getSmallDocumentBlocks(header_block_reader.getBigBlockSize(), data_blocks, properties.getRoot(), header_block_reader.getSBATStart()), data_blocks, properties.getRoot().getChildren(), new POIFSDocumentPath());
    }

    public void registerListener(POIFSReaderListener listener) {
        if (listener == null) {
            throw new NullPointerException();
        } else if (!this.registryClosed) {
            this.registry.registerListener(listener);
        } else {
            throw new IllegalStateException();
        }
    }

    public void registerListener(POIFSReaderListener listener, String name) {
        registerListener(listener, (POIFSDocumentPath) null, name);
    }

    public void registerListener(POIFSReaderListener listener, POIFSDocumentPath path, String name) {
        if (listener == null || name == null || name.length() == 0) {
            throw new NullPointerException();
        } else if (!this.registryClosed) {
            this.registry.registerListener(listener, path == null ? new POIFSDocumentPath() : path, name);
        } else {
            throw new IllegalStateException();
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("at least one argument required: input filename(s)");
            System.exit(1);
        }
        for (int j = 0; j < args.length; j++) {
            POIFSReader reader = new POIFSReader();
            reader.registerListener(new SampleListener());
            PrintStream printStream = System.out;
            printStream.println("reading " + args[j]);
            FileInputStream istream = new FileInputStream(args[j]);
            reader.read(istream);
            istream.close();
        }
    }

    private void processProperties(BlockList small_blocks, BlockList big_blocks, Iterator properties, POIFSDocumentPath path) throws IOException {
        POIFSDocument document;
        while (properties.hasNext()) {
            Property property = (Property) properties.next();
            String name = property.getName();
            if (property.isDirectory()) {
                processProperties(small_blocks, big_blocks, ((DirectoryProperty) property).getChildren(), new POIFSDocumentPath(path, new String[]{name}));
            } else {
                int startBlock = property.getStartBlock();
                Iterator listeners = this.registry.getListeners(path, name);
                if (listeners.hasNext()) {
                    int size = property.getSize();
                    if (property.shouldUseSmallBlocks()) {
                        document = new POIFSDocument(name, small_blocks.fetchBlocks(startBlock, -1), size);
                    } else {
                        document = new POIFSDocument(name, big_blocks.fetchBlocks(startBlock, -1), size);
                    }
                    while (listeners.hasNext()) {
                        ((POIFSReaderListener) listeners.next()).processPOIFSReaderEvent(new POIFSReaderEvent(new DocumentInputStream(document), path, name));
                    }
                } else if (property.shouldUseSmallBlocks()) {
                    small_blocks.fetchBlocks(startBlock, -1);
                } else {
                    big_blocks.fetchBlocks(startBlock, -1);
                }
            }
        }
    }

    private static class SampleListener implements POIFSReaderListener {
        SampleListener() {
        }

        public void processPOIFSReaderEvent(POIFSReaderEvent event) {
            DocumentInputStream istream = event.getStream();
            POIFSDocumentPath path = event.getPath();
            String name = event.getName();
            try {
                byte[] data = new byte[istream.available()];
                istream.read(data);
                int pathLength = path.length();
                for (int k = 0; k < pathLength; k++) {
                    PrintStream printStream = System.out;
                    printStream.print("/" + path.getComponent(k));
                }
                PrintStream printStream2 = System.out;
                printStream2.println("/" + name + ": " + data.length + " bytes read");
            } catch (IOException e) {
            }
        }
    }
}
