package org.apache.poi.ss.formula;

import java.util.Arrays;
import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

public class Formula {
    private static final Formula EMPTY = new Formula(new byte[0], 0);
    private final byte[] _byteEncoding;
    private final int _encodedTokenLen;

    private Formula(byte[] byteEncoding, int encodedTokenLen) {
        this._byteEncoding = byteEncoding;
        this._encodedTokenLen = encodedTokenLen;
    }

    public static Formula read(int encodedTokenLen, LittleEndianInput in) {
        return read(encodedTokenLen, in, encodedTokenLen);
    }

    public static Formula read(int encodedTokenLen, LittleEndianInput in, int totalEncodedLen) {
        byte[] byteEncoding = new byte[totalEncodedLen];
        in.readFully(byteEncoding);
        return new Formula(byteEncoding, encodedTokenLen);
    }

    public Ptg[] getTokens() {
        return Ptg.readTokens(this._encodedTokenLen, new LittleEndianByteArrayInputStream(this._byteEncoding));
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this._encodedTokenLen);
        out.write(this._byteEncoding);
    }

    public void serializeTokens(LittleEndianOutput out) {
        out.write(this._byteEncoding, 0, this._encodedTokenLen);
    }

    public void serializeArrayConstantData(LittleEndianOutput out) {
        byte[] bArr = this._byteEncoding;
        int length = bArr.length;
        int i = this._encodedTokenLen;
        out.write(bArr, i, length - i);
    }

    public int getEncodedSize() {
        return this._byteEncoding.length + 2;
    }

    public int getEncodedTokenSize() {
        return this._encodedTokenLen;
    }

    public static Formula create(Ptg[] ptgs) {
        if (ptgs == null || ptgs.length < 1) {
            return EMPTY;
        }
        byte[] encodedData = new byte[Ptg.getEncodedSize(ptgs)];
        Ptg.serializePtgs(ptgs, encodedData, 0);
        return new Formula(encodedData, Ptg.getEncodedSizeWithoutArrayData(ptgs));
    }

    public static Ptg[] getTokens(Formula formula) {
        if (formula == null) {
            return null;
        }
        return formula.getTokens();
    }

    public Formula copy() {
        return this;
    }

    public CellReference getExpReference() {
        byte[] data = this._byteEncoding;
        if (data.length != 5) {
            return null;
        }
        byte b = data[0];
        if (b == 1 || b == 2) {
            return new CellReference(LittleEndian.getUShort(data, 1), LittleEndian.getUShort(data, 3));
        }
        return null;
    }

    public boolean isSame(Formula other) {
        return Arrays.equals(this._byteEncoding, other._byteEncoding);
    }
}
