package org.apache.poi.poifs.filesystem;

public class POIFSWriterEvent {
    private String documentName;
    private int limit;
    private POIFSDocumentPath path;
    private DocumentOutputStream stream;

    POIFSWriterEvent(DocumentOutputStream stream2, POIFSDocumentPath path2, String documentName2, int limit2) {
        this.stream = stream2;
        this.path = path2;
        this.documentName = documentName2;
        this.limit = limit2;
    }

    public DocumentOutputStream getStream() {
        return this.stream;
    }

    public POIFSDocumentPath getPath() {
        return this.path;
    }

    public String getName() {
        return this.documentName;
    }

    public int getLimit() {
        return this.limit;
    }
}
