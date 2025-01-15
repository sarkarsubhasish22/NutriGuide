package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class PasswordRecord extends StandardRecord {
    public static final short sid = 19;
    private int field_1_password;

    public PasswordRecord(int password) {
        this.field_1_password = password;
    }

    public PasswordRecord(RecordInputStream in) {
        this.field_1_password = in.readShort();
    }

    public static short hashPassword(String password) {
        byte[] passwordCharacters = password.getBytes();
        int hash = 0;
        if (passwordCharacters.length > 0) {
            int charIndex = passwordCharacters.length;
            while (true) {
                int charIndex2 = charIndex - 1;
                if (charIndex <= 0) {
                    break;
                }
                hash = (((hash >> 14) & 1) | ((hash << 1) & 32767)) ^ passwordCharacters[charIndex2];
                charIndex = charIndex2;
            }
            hash = ((((hash >> 14) & 1) | ((hash << 1) & 32767)) ^ passwordCharacters.length) ^ 52811;
        }
        return (short) hash;
    }

    public void setPassword(int password) {
        this.field_1_password = password;
    }

    public int getPassword() {
        return this.field_1_password;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[PASSWORD]\n");
        buffer.append("    .password = ");
        buffer.append(HexDump.shortToHex(this.field_1_password));
        buffer.append("\n");
        buffer.append("[/PASSWORD]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_password);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 2;
    }

    public short getSid() {
        return 19;
    }

    public Object clone() {
        return new PasswordRecord(this.field_1_password);
    }
}
