package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.StringUtil;

public final class FileSharingRecord extends StandardRecord {
    public static final short sid = 91;
    private short field_1_readonly;
    private short field_2_password;
    private byte field_3_username_unicode_options;
    private String field_3_username_value;

    public FileSharingRecord() {
    }

    public FileSharingRecord(RecordInputStream in) {
        this.field_1_readonly = in.readShort();
        this.field_2_password = in.readShort();
        int nameLen = in.readShort();
        if (nameLen > 0) {
            this.field_3_username_unicode_options = in.readByte();
            this.field_3_username_value = in.readCompressedUnicode(nameLen);
            return;
        }
        this.field_3_username_value = "";
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

    public void setReadOnly(short readonly) {
        this.field_1_readonly = readonly;
    }

    public short getReadOnly() {
        return this.field_1_readonly;
    }

    public void setPassword(short password) {
        this.field_2_password = password;
    }

    public short getPassword() {
        return this.field_2_password;
    }

    public String getUsername() {
        return this.field_3_username_value;
    }

    public void setUsername(String username) {
        this.field_3_username_value = username;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[FILESHARING]\n");
        buffer.append("    .readonly       = ");
        buffer.append(getReadOnly() == 1 ? "true" : "false");
        buffer.append("\n");
        buffer.append("    .password       = ");
        buffer.append(Integer.toHexString(getPassword()));
        buffer.append("\n");
        buffer.append("    .username       = ");
        buffer.append(getUsername());
        buffer.append("\n");
        buffer.append("[/FILESHARING]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(getReadOnly());
        out.writeShort(getPassword());
        out.writeShort(this.field_3_username_value.length());
        if (this.field_3_username_value.length() > 0) {
            out.writeByte(this.field_3_username_unicode_options);
            StringUtil.putCompressedUnicode(getUsername(), out);
        }
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        int nameLen = this.field_3_username_value.length();
        if (nameLen < 1) {
            return 6;
        }
        return nameLen + 7;
    }

    public short getSid() {
        return 91;
    }

    public Object clone() {
        FileSharingRecord clone = new FileSharingRecord();
        clone.setReadOnly(this.field_1_readonly);
        clone.setPassword(this.field_2_password);
        clone.setUsername(this.field_3_username_value);
        return clone;
    }
}
