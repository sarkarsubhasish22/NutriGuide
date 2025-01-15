package org.apache.poi.poifs.dev;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.DocumentNode;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class POIFSDump {
    public static void main(String[] args) throws Exception {
        for (int i = 0; i < args.length; i++) {
            PrintStream printStream = System.out;
            printStream.println("Dumping " + args[i]);
            FileInputStream is = new FileInputStream(args[i]);
            POIFSFileSystem fs = new POIFSFileSystem(is);
            is.close();
            DirectoryEntry root = fs.getRoot();
            File file = new File(root.getName());
            file.mkdir();
            dump(root, file);
        }
    }

    public static void dump(DirectoryEntry root, File parent) throws IOException {
        Iterator it = root.getEntries();
        while (it.hasNext()) {
            Entry entry = it.next();
            if (entry instanceof DocumentNode) {
                DocumentNode node = (DocumentNode) entry;
                DocumentInputStream is = new DocumentInputStream((DocumentEntry) node);
                byte[] bytes = new byte[node.getSize()];
                is.read(bytes);
                is.close();
                FileOutputStream out = new FileOutputStream(new File(parent, node.getName().trim()));
                out.write(bytes);
                out.close();
            } else if (entry instanceof DirectoryEntry) {
                File file = new File(parent, entry.getName());
                file.mkdir();
                dump((DirectoryEntry) entry, file);
            } else {
                PrintStream printStream = System.err;
                printStream.println("Skipping unsupported POIFS entry: " + entry);
            }
        }
    }
}
