package org.apache.poi;

import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hpsf.extractor.HPSFPropertiesExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public abstract class POIOLE2TextExtractor extends POITextExtractor {
    public POIOLE2TextExtractor(POIDocument document) {
        super(document);
    }

    public DocumentSummaryInformation getDocSummaryInformation() {
        return this.document.getDocumentSummaryInformation();
    }

    public SummaryInformation getSummaryInformation() {
        return this.document.getSummaryInformation();
    }

    public POITextExtractor getMetadataTextExtractor() {
        return new HPSFPropertiesExtractor((POITextExtractor) this);
    }

    public POIFSFileSystem getFileSystem() {
        return this.document.filesystem;
    }
}
