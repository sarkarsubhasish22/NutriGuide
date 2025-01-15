package org.apache.poi;

public abstract class POITextExtractor {
    protected POIDocument document;

    public abstract POITextExtractor getMetadataTextExtractor();

    public abstract String getText();

    public POITextExtractor(POIDocument document2) {
        this.document = document2;
    }

    protected POITextExtractor(POITextExtractor otherExtractor) {
        this.document = otherExtractor.document;
    }
}
