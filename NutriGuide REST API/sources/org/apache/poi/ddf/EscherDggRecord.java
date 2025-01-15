package org.apache.poi.ddf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.RecordFormatException;

public final class EscherDggRecord extends EscherRecord {
    private static final Comparator<FileIdCluster> MY_COMP = new Comparator<FileIdCluster>() {
        public int compare(FileIdCluster f1, FileIdCluster f2) {
            if (f1.getDrawingGroupId() == f2.getDrawingGroupId()) {
                return 0;
            }
            if (f1.getDrawingGroupId() < f2.getDrawingGroupId()) {
                return -1;
            }
            return 1;
        }
    };
    public static final String RECORD_DESCRIPTION = "MsofbtDgg";
    public static final short RECORD_ID = -4090;
    private int field_1_shapeIdMax;
    private int field_3_numShapesSaved;
    private int field_4_drawingsSaved;
    private FileIdCluster[] field_5_fileIdClusters;
    private int maxDgId;

    public static class FileIdCluster {
        /* access modifiers changed from: private */
        public int field_1_drawingGroupId;
        /* access modifiers changed from: private */
        public int field_2_numShapeIdsUsed;

        public FileIdCluster(int drawingGroupId, int numShapeIdsUsed) {
            this.field_1_drawingGroupId = drawingGroupId;
            this.field_2_numShapeIdsUsed = numShapeIdsUsed;
        }

        public int getDrawingGroupId() {
            return this.field_1_drawingGroupId;
        }

        public int getNumShapeIdsUsed() {
            return this.field_2_numShapeIdsUsed;
        }

        public void incrementShapeId() {
            this.field_2_numShapeIdsUsed++;
        }
    }

    public int fillFields(byte[] data, int offset, EscherRecordFactory recordFactory) {
        int bytesRemaining = readHeader(data, offset);
        int pos = offset + 8;
        this.field_1_shapeIdMax = LittleEndian.getInt(data, pos + 0);
        int size = 0 + 4;
        LittleEndian.getInt(data, pos + size);
        int size2 = size + 4;
        this.field_3_numShapesSaved = LittleEndian.getInt(data, pos + size2);
        int size3 = size2 + 4;
        this.field_4_drawingsSaved = LittleEndian.getInt(data, pos + size3);
        int size4 = size3 + 4;
        this.field_5_fileIdClusters = new FileIdCluster[((bytesRemaining - size4) / 8)];
        int i = 0;
        while (true) {
            FileIdCluster[] fileIdClusterArr = this.field_5_fileIdClusters;
            if (i >= fileIdClusterArr.length) {
                break;
            }
            fileIdClusterArr[i] = new FileIdCluster(LittleEndian.getInt(data, pos + size4), LittleEndian.getInt(data, pos + size4 + 4));
            this.maxDgId = Math.max(this.maxDgId, this.field_5_fileIdClusters[i].getDrawingGroupId());
            size4 += 8;
            i++;
        }
        int bytesRemaining2 = bytesRemaining - size4;
        if (bytesRemaining2 == 0) {
            return size4 + 8 + bytesRemaining2;
        }
        throw new RecordFormatException("Expecting no remaining data but got " + bytesRemaining2 + " byte(s).");
    }

    public int serialize(int offset, byte[] data, EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, getRecordId(), this);
        int pos = offset;
        LittleEndian.putShort(data, pos, getOptions());
        int pos2 = pos + 2;
        LittleEndian.putShort(data, pos2, getRecordId());
        int pos3 = pos2 + 2;
        LittleEndian.putInt(data, pos3, getRecordSize() - 8);
        int pos4 = pos3 + 4;
        LittleEndian.putInt(data, pos4, this.field_1_shapeIdMax);
        int pos5 = pos4 + 4;
        LittleEndian.putInt(data, pos5, getNumIdClusters());
        int pos6 = pos5 + 4;
        LittleEndian.putInt(data, pos6, this.field_3_numShapesSaved);
        int pos7 = pos6 + 4;
        LittleEndian.putInt(data, pos7, this.field_4_drawingsSaved);
        int pos8 = pos7 + 4;
        int i = 0;
        while (true) {
            FileIdCluster[] fileIdClusterArr = this.field_5_fileIdClusters;
            if (i < fileIdClusterArr.length) {
                LittleEndian.putInt(data, pos8, fileIdClusterArr[i].field_1_drawingGroupId);
                int pos9 = pos8 + 4;
                LittleEndian.putInt(data, pos9, this.field_5_fileIdClusters[i].field_2_numShapeIdsUsed);
                pos8 = pos9 + 4;
                i++;
            } else {
                listener.afterRecordSerialize(pos8, getRecordId(), getRecordSize(), this);
                return getRecordSize();
            }
        }
    }

    public int getRecordSize() {
        return (this.field_5_fileIdClusters.length * 8) + 24;
    }

    public short getRecordId() {
        return RECORD_ID;
    }

    public String getRecordName() {
        return "Dgg";
    }

    public String toString() {
        StringBuffer field_5_string = new StringBuffer();
        if (this.field_5_fileIdClusters != null) {
            for (int i = 0; i < this.field_5_fileIdClusters.length; i++) {
                field_5_string.append("  DrawingGroupId");
                field_5_string.append(i + 1);
                field_5_string.append(": ");
                field_5_string.append(this.field_5_fileIdClusters[i].field_1_drawingGroupId);
                field_5_string.append(10);
                field_5_string.append("  NumShapeIdsUsed");
                field_5_string.append(i + 1);
                field_5_string.append(": ");
                field_5_string.append(this.field_5_fileIdClusters[i].field_2_numShapeIdsUsed);
                field_5_string.append(10);
            }
        }
        return getClass().getName() + ":" + 10 + "  RecordId: 0x" + HexDump.toHex((short) RECORD_ID) + 10 + "  Options: 0x" + HexDump.toHex(getOptions()) + 10 + "  ShapeIdMax: " + this.field_1_shapeIdMax + 10 + "  NumIdClusters: " + getNumIdClusters() + 10 + "  NumShapesSaved: " + this.field_3_numShapesSaved + 10 + "  DrawingsSaved: " + this.field_4_drawingsSaved + 10 + "" + field_5_string.toString();
    }

    public int getShapeIdMax() {
        return this.field_1_shapeIdMax;
    }

    public void setShapeIdMax(int shapeIdMax) {
        this.field_1_shapeIdMax = shapeIdMax;
    }

    public int getNumIdClusters() {
        FileIdCluster[] fileIdClusterArr = this.field_5_fileIdClusters;
        if (fileIdClusterArr == null) {
            return 0;
        }
        return fileIdClusterArr.length + 1;
    }

    public int getNumShapesSaved() {
        return this.field_3_numShapesSaved;
    }

    public void setNumShapesSaved(int numShapesSaved) {
        this.field_3_numShapesSaved = numShapesSaved;
    }

    public int getDrawingsSaved() {
        return this.field_4_drawingsSaved;
    }

    public void setDrawingsSaved(int drawingsSaved) {
        this.field_4_drawingsSaved = drawingsSaved;
    }

    public int getMaxDrawingGroupId() {
        return this.maxDgId;
    }

    public void setMaxDrawingGroupId(int id) {
        this.maxDgId = id;
    }

    public FileIdCluster[] getFileIdClusters() {
        return this.field_5_fileIdClusters;
    }

    public void setFileIdClusters(FileIdCluster[] fileIdClusters) {
        this.field_5_fileIdClusters = fileIdClusters;
    }

    public void addCluster(int dgId, int numShapedUsed) {
        addCluster(dgId, numShapedUsed, true);
    }

    public void addCluster(int dgId, int numShapedUsed, boolean sort) {
        List<FileIdCluster> clusters = new ArrayList<>(Arrays.asList(this.field_5_fileIdClusters));
        clusters.add(new FileIdCluster(dgId, numShapedUsed));
        if (sort) {
            Collections.sort(clusters, MY_COMP);
        }
        this.maxDgId = Math.min(this.maxDgId, dgId);
        this.field_5_fileIdClusters = (FileIdCluster[]) clusters.toArray(new FileIdCluster[clusters.size()]);
    }
}
