package org.apache.poi.hssf.dev;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class EFBiffViewer {
    String file;

    public void run() throws IOException {
        InputStream din = new POIFSFileSystem(new FileInputStream(this.file)).createDocumentInputStream("Workbook");
        HSSFRequest req = new HSSFRequest();
        req.addListenerForAllRecords(new HSSFListener() {
            public void processRecord(Record rec) {
                System.out.println(rec.toString());
            }
        });
        new HSSFEventFactory().processEvents(req, din);
    }

    public void setFile(String file2) {
        this.file = file2;
    }

    public static void main(String[] args) {
        if (args.length != 1 || args[0].equals("--help")) {
            System.out.println("EFBiffViewer");
            System.out.println("Outputs biffview of records based on HSSFEventFactory");
            System.out.println("usage: java org.apache.poi.hssf.dev.EBBiffViewer filename");
            return;
        }
        try {
            EFBiffViewer viewer = new EFBiffViewer();
            viewer.setFile(args[0]);
            viewer.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
