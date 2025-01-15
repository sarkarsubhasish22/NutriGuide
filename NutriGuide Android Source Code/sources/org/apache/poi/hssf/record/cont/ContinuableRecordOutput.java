package org.apache.poi.hssf.record.cont;

import org.apache.poi.util.DelayableLittleEndianOutput;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.StringUtil;

public final class ContinuableRecordOutput implements LittleEndianOutput {
    private static final LittleEndianOutput NOPOutput = new DelayableLittleEndianOutput() {
        public LittleEndianOutput createDelayedOutput(int size) {
            return this;
        }

        public void write(byte[] b) {
        }

        public void write(byte[] b, int offset, int len) {
        }

        public void writeByte(int v) {
        }

        public void writeDouble(double v) {
        }

        public void writeInt(int v) {
        }

        public void writeLong(long v) {
        }

        public void writeShort(int v) {
        }
    };
    private final LittleEndianOutput _out;
    private int _totalPreviousRecordsSize = 0;
    private UnknownLengthRecordOutput _ulrOutput;

    public ContinuableRecordOutput(LittleEndianOutput out, int sid) {
        this._ulrOutput = new UnknownLengthRecordOutput(out, sid);
        this._out = out;
    }

    public static ContinuableRecordOutput createForCountingOnly() {
        return new ContinuableRecordOutput(NOPOutput, -777);
    }

    public int getTotalSize() {
        return this._totalPreviousRecordsSize + this._ulrOutput.getTotalSize();
    }

    /* access modifiers changed from: package-private */
    public void terminate() {
        this._ulrOutput.terminate();
    }

    public int getAvailableSpace() {
        return this._ulrOutput.getAvailableSpace();
    }

    public void writeContinue() {
        this._ulrOutput.terminate();
        this._totalPreviousRecordsSize += this._ulrOutput.getTotalSize();
        this._ulrOutput = new UnknownLengthRecordOutput(this._out, 60);
    }

    public void writeContinueIfRequired(int requiredContinuousSize) {
        if (this._ulrOutput.getAvailableSpace() < requiredContinuousSize) {
            writeContinue();
        }
    }

    public void writeStringData(String text) {
        boolean is16bitEncoded = StringUtil.hasMultibyte(text);
        int keepTogetherSize = 2;
        int optionFlags = 0;
        if (is16bitEncoded) {
            optionFlags = 0 | 1;
            keepTogetherSize = 2 + 1;
        }
        writeContinueIfRequired(keepTogetherSize);
        writeByte(optionFlags);
        writeCharacterData(text, is16bitEncoded);
    }

    public void writeString(String text, int numberOfRichTextRuns, int extendedDataSize) {
        boolean is16bitEncoded = StringUtil.hasMultibyte(text);
        int keepTogetherSize = 4;
        int optionFlags = 0;
        if (is16bitEncoded) {
            optionFlags = 0 | 1;
            keepTogetherSize = 4 + 1;
        }
        if (numberOfRichTextRuns > 0) {
            optionFlags |= 8;
            keepTogetherSize += 2;
        }
        if (extendedDataSize > 0) {
            optionFlags |= 4;
            keepTogetherSize += 4;
        }
        writeContinueIfRequired(keepTogetherSize);
        writeShort(text.length());
        writeByte(optionFlags);
        if (numberOfRichTextRuns > 0) {
            writeShort(numberOfRichTextRuns);
        }
        if (extendedDataSize > 0) {
            writeInt(extendedDataSize);
        }
        writeCharacterData(text, is16bitEncoded);
    }

    private void writeCharacterData(String text, boolean is16bitEncoded) {
        int nChars = text.length();
        int i = 0;
        if (is16bitEncoded) {
            while (true) {
                int nWritableChars = Math.min(nChars - i, this._ulrOutput.getAvailableSpace() / 2);
                while (nWritableChars > 0) {
                    this._ulrOutput.writeShort(text.charAt(i));
                    nWritableChars--;
                    i++;
                }
                if (i < nChars) {
                    writeContinue();
                    writeByte(1);
                } else {
                    return;
                }
            }
        } else {
            while (true) {
                int nWritableChars2 = Math.min(nChars - i, this._ulrOutput.getAvailableSpace() / 1);
                while (nWritableChars2 > 0) {
                    this._ulrOutput.writeByte(text.charAt(i));
                    nWritableChars2--;
                    i++;
                }
                if (i < nChars) {
                    writeContinue();
                    writeByte(0);
                } else {
                    return;
                }
            }
        }
    }

    public void write(byte[] b) {
        writeContinueIfRequired(b.length);
        this._ulrOutput.write(b);
    }

    public void write(byte[] b, int offset, int len) {
        writeContinueIfRequired(len);
        this._ulrOutput.write(b, offset, len);
    }

    public void writeByte(int v) {
        writeContinueIfRequired(1);
        this._ulrOutput.writeByte(v);
    }

    public void writeDouble(double v) {
        writeContinueIfRequired(8);
        this._ulrOutput.writeDouble(v);
    }

    public void writeInt(int v) {
        writeContinueIfRequired(4);
        this._ulrOutput.writeInt(v);
    }

    public void writeLong(long v) {
        writeContinueIfRequired(8);
        this._ulrOutput.writeLong(v);
    }

    public void writeShort(int v) {
        writeContinueIfRequired(2);
        this._ulrOutput.writeShort(v);
    }
}
