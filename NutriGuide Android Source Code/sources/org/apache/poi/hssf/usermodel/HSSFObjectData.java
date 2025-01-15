package org.apache.poi.hssf.usermodel;

import java.io.IOException;
import org.apache.poi.hssf.record.EmbeddedObjectRefSubRecord;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.hssf.record.SubRecord;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.HexDump;

public final class HSSFObjectData {
    private final POIFSFileSystem _poifs;
    private final ObjRecord _record;

    public HSSFObjectData(ObjRecord record, POIFSFileSystem poifs) {
        this._record = record;
        this._poifs = poifs;
    }

    public String getOLE2ClassName() {
        return findObjectRecord().getOLEClassName();
    }

    public DirectoryEntry getDirectory() throws IOException {
        int streamId = findObjectRecord().getStreamId().intValue();
        String streamName = "MBD" + HexDump.toHex(streamId);
        Entry entry = this._poifs.getRoot().getEntry(streamName);
        if (entry instanceof DirectoryEntry) {
            return (DirectoryEntry) entry;
        }
        throw new IOException("Stream " + streamName + " was not an OLE2 directory");
    }

    public byte[] getObjectData() {
        return findObjectRecord().getObjectData();
    }

    public boolean hasDirectoryEntry() {
        Integer streamId = findObjectRecord().getStreamId();
        return (streamId == null || streamId.intValue() == 0) ? false : true;
    }

    /* access modifiers changed from: protected */
    public EmbeddedObjectRefSubRecord findObjectRecord() {
        for (SubRecord next : this._record.getSubRecords()) {
            if (next instanceof EmbeddedObjectRefSubRecord) {
                return (EmbeddedObjectRefSubRecord) next;
            }
        }
        throw new IllegalStateException("Object data does not contain a reference to an embedded object OLE2 directory");
    }
}
