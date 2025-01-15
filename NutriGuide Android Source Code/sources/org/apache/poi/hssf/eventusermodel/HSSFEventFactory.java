package org.apache.poi.hssf.eventusermodel;

import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordFactoryInputStream;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class HSSFEventFactory {
    public void processWorkbookEvents(HSSFRequest req, POIFSFileSystem fs) throws IOException {
        processWorkbookEvents(req, fs.getRoot());
    }

    public void processWorkbookEvents(HSSFRequest req, DirectoryNode dir) throws IOException {
        processEvents(req, dir.createDocumentInputStream("Workbook"));
    }

    public short abortableProcessWorkbookEvents(HSSFRequest req, POIFSFileSystem fs) throws IOException, HSSFUserException {
        return abortableProcessWorkbookEvents(req, fs.getRoot());
    }

    public short abortableProcessWorkbookEvents(HSSFRequest req, DirectoryNode dir) throws IOException, HSSFUserException {
        return abortableProcessEvents(req, dir.createDocumentInputStream("Workbook"));
    }

    public void processEvents(HSSFRequest req, InputStream in) {
        try {
            genericProcessEvents(req, in);
        } catch (HSSFUserException e) {
        }
    }

    public short abortableProcessEvents(HSSFRequest req, InputStream in) throws HSSFUserException {
        return genericProcessEvents(req, in);
    }

    private short genericProcessEvents(HSSFRequest req, InputStream in) throws HSSFUserException {
        Record r;
        short userCode = 0;
        RecordFactoryInputStream recordStream = new RecordFactoryInputStream(in, false);
        do {
            r = recordStream.nextRecord();
            if (r == null || (userCode = req.processRecord(r)) != 0) {
                return userCode;
            }
            r = recordStream.nextRecord();
            break;
        } while ((userCode = req.processRecord(r)) != 0);
        return userCode;
    }
}
