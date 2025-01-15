package org.apache.poi.poifs.dev;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.DocumentNode;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class POIFSLister {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Must specify at least one file to view");
            System.exit(1);
        }
        boolean withSizes = false;
        for (int j = 0; j < args.length; j++) {
            if (args[j].equalsIgnoreCase("-size") || args[j].equalsIgnoreCase("-sizes")) {
                withSizes = true;
            } else {
                viewFile(args[j], withSizes);
            }
        }
    }

    public static void viewFile(String filename, boolean withSizes) throws IOException {
        displayDirectory(new POIFSFileSystem(new FileInputStream(filename)).getRoot(), "", withSizes);
    }

    public static void displayDirectory(DirectoryNode dir, String indent, boolean withSizes) {
        System.out.println(indent + dir.getName() + " -");
        StringBuilder sb = new StringBuilder();
        sb.append(indent);
        sb.append("  ");
        String newIndent = sb.toString();
        boolean hadChildren = false;
        Iterator it = dir.getEntries();
        while (it.hasNext()) {
            hadChildren = true;
            Entry next = it.next();
            if (next instanceof DirectoryNode) {
                displayDirectory((DirectoryNode) next, newIndent, withSizes);
            } else {
                DocumentNode doc = (DocumentNode) next;
                String name = doc.getName();
                String size = "";
                if (name.charAt(0) < 10) {
                    name = name.substring(1) + " <" + ("(0x0" + name.charAt(0) + ")" + name.substring(1)) + ">";
                }
                if (withSizes) {
                    size = " [" + doc.getSize() + " / 0x" + Integer.toHexString(doc.getSize()) + "]";
                }
                System.out.println(newIndent + name + size);
            }
        }
        if (!hadChildren) {
            System.out.println(newIndent + "(no children)");
        }
    }
}
