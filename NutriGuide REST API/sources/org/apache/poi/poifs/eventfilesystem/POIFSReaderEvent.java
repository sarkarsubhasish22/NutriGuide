package org.apache.poi.poifs.eventfilesystem;

import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSDocumentPath;

public class POIFSReaderEvent {
    private String documentName;
    private POIFSDocumentPath path;
    private DocumentInputStream stream;

    POIFSReaderEvent(DocumentInputStream stream2, POIFSDocumentPath path2, String documentName2) {
        this.stream = stream2;
        this.path = path2;
        this.documentName = documentName2;
    }

    public DocumentInputStream getStream() {
        return this.stream;
    }

    public POIFSDocumentPath getPath() {
        return this.path;
    }

    public String getName() {
        return this.documentName;
    }
}
