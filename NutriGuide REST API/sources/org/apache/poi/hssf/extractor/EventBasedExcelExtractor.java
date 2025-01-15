package org.apache.poi.hssf.extractor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.POIDocument;
import org.apache.poi.POIOLE2TextExtractor;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.LabelRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NoteRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.record.StringRecord;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class EventBasedExcelExtractor extends POIOLE2TextExtractor {
    private DirectoryNode _dir;
    boolean _formulasNotResults;
    private POIFSFileSystem _fs;
    boolean _includeSheetNames;

    public EventBasedExcelExtractor(DirectoryNode dir, POIFSFileSystem fs) {
        super((POIDocument) null);
        this._includeSheetNames = true;
        this._formulasNotResults = false;
        this._dir = dir;
        this._fs = fs;
    }

    public EventBasedExcelExtractor(POIFSFileSystem fs) {
        this(fs.getRoot(), fs);
    }

    public POIFSFileSystem getFileSystem() {
        return this._fs;
    }

    public DocumentSummaryInformation getDocSummaryInformation() {
        throw new IllegalStateException("Metadata extraction not supported in streaming mode, please use ExcelExtractor");
    }

    public SummaryInformation getSummaryInformation() {
        throw new IllegalStateException("Metadata extraction not supported in streaming mode, please use ExcelExtractor");
    }

    public void setIncludeSheetNames(boolean includeSheetNames) {
        this._includeSheetNames = includeSheetNames;
    }

    public void setFormulasNotResults(boolean formulasNotResults) {
        this._formulasNotResults = formulasNotResults;
    }

    public String getText() {
        try {
            String text = triggerExtraction()._text.toString();
            if (text.endsWith("\n")) {
                return text;
            }
            return text + "\n";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private TextListener triggerExtraction() throws IOException {
        TextListener tl = new TextListener();
        FormatTrackingHSSFListener ft = new FormatTrackingHSSFListener(tl);
        tl._ft = ft;
        HSSFEventFactory factory = new HSSFEventFactory();
        HSSFRequest request = new HSSFRequest();
        request.addListenerForAllRecords(ft);
        factory.processWorkbookEvents(request, this._dir);
        return tl;
    }

    private class TextListener implements HSSFListener {
        FormatTrackingHSSFListener _ft;
        final StringBuffer _text = new StringBuffer();
        private int nextRow = -1;
        private boolean outputNextStringValue = false;
        private int rowNum;
        private final List<String> sheetNames = new ArrayList();
        private int sheetNum = -1;
        private SSTRecord sstRecord;

        public TextListener() {
        }

        public void processRecord(Record record) {
            String thisText = null;
            int thisRow = -1;
            short sid = record.getSid();
            if (sid == 6) {
                FormulaRecord frec = (FormulaRecord) record;
                thisRow = frec.getRow();
                if (EventBasedExcelExtractor.this._formulasNotResults) {
                    thisText = HSSFFormulaParser.toFormulaString((HSSFWorkbook) null, frec.getParsedExpression());
                } else if (frec.hasCachedResultString()) {
                    this.outputNextStringValue = true;
                    this.nextRow = frec.getRow();
                } else {
                    thisText = this._ft.formatNumberDateCell(frec);
                }
            } else if (sid == 28) {
                thisRow = ((NoteRecord) record).getRow();
            } else if (sid == 133) {
                this.sheetNames.add(((BoundSheetRecord) record).getSheetname());
            } else if (sid != 519) {
                if (sid != 2057) {
                    if (sid == 252) {
                        this.sstRecord = (SSTRecord) record;
                    } else if (sid == 253) {
                        LabelSSTRecord lsrec = (LabelSSTRecord) record;
                        thisRow = lsrec.getRow();
                        SSTRecord sSTRecord = this.sstRecord;
                        if (sSTRecord != null) {
                            thisText = sSTRecord.getString(lsrec.getSSTIndex()).toString();
                        } else {
                            throw new IllegalStateException("No SST record found");
                        }
                    } else if (sid == 515) {
                        NumberRecord numrec = (NumberRecord) record;
                        thisRow = numrec.getRow();
                        thisText = this._ft.formatNumberDateCell(numrec);
                    } else if (sid == 516) {
                        LabelRecord lrec = (LabelRecord) record;
                        thisRow = lrec.getRow();
                        thisText = lrec.getValue();
                    }
                } else if (((BOFRecord) record).getType() == 16) {
                    this.sheetNum++;
                    this.rowNum = -1;
                    if (EventBasedExcelExtractor.this._includeSheetNames) {
                        if (this._text.length() > 0) {
                            this._text.append("\n");
                        }
                        this._text.append(this.sheetNames.get(this.sheetNum));
                    }
                }
            } else if (this.outputNextStringValue) {
                thisText = ((StringRecord) record).getString();
                thisRow = this.nextRow;
                this.outputNextStringValue = false;
            }
            if (thisText != null) {
                if (thisRow != this.rowNum) {
                    this.rowNum = thisRow;
                    if (this._text.length() > 0) {
                        this._text.append("\n");
                    }
                } else {
                    this._text.append("\t");
                }
                this._text.append(thisText);
            }
        }
    }
}
