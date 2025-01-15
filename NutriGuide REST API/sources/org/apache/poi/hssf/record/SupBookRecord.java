package org.apache.poi.hssf.record;

import androidx.core.view.InputDeviceCompat;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.StringUtil;

public final class SupBookRecord extends StandardRecord {
    private static final short SMALL_RECORD_SIZE = 4;
    private static final short TAG_ADD_IN_FUNCTIONS = 14849;
    private static final short TAG_INTERNAL_REFERENCES = 1025;
    public static final short sid = 430;
    private boolean _isAddInFunctions;
    private short field_1_number_of_sheets;
    private String field_2_encoded_url;
    private String[] field_3_sheet_names;

    public static SupBookRecord createInternalReferences(short numberOfSheets) {
        return new SupBookRecord(false, numberOfSheets);
    }

    public static SupBookRecord createAddInFunctions() {
        return new SupBookRecord(true, 0);
    }

    public static SupBookRecord createExternalReferences(String url, String[] sheetNames) {
        return new SupBookRecord(url, sheetNames);
    }

    private SupBookRecord(boolean isAddInFuncs, short numberOfSheets) {
        this.field_1_number_of_sheets = numberOfSheets;
        this.field_2_encoded_url = null;
        this.field_3_sheet_names = null;
        this._isAddInFunctions = isAddInFuncs;
    }

    public SupBookRecord(String url, String[] sheetNames) {
        this.field_1_number_of_sheets = (short) sheetNames.length;
        this.field_2_encoded_url = url;
        this.field_3_sheet_names = sheetNames;
        this._isAddInFunctions = false;
    }

    public boolean isExternalReferences() {
        return this.field_3_sheet_names != null;
    }

    public boolean isInternalReferences() {
        return this.field_3_sheet_names == null && !this._isAddInFunctions;
    }

    public boolean isAddInFunctions() {
        return this.field_3_sheet_names == null && this._isAddInFunctions;
    }

    public SupBookRecord(RecordInputStream in) {
        int recLen = in.remaining();
        this.field_1_number_of_sheets = in.readShort();
        if (recLen > 4) {
            this._isAddInFunctions = false;
            this.field_2_encoded_url = in.readString();
            String[] sheetNames = new String[this.field_1_number_of_sheets];
            for (int i = 0; i < sheetNames.length; i++) {
                sheetNames[i] = in.readString();
            }
            this.field_3_sheet_names = sheetNames;
            return;
        }
        this.field_2_encoded_url = null;
        this.field_3_sheet_names = null;
        short nextShort = in.readShort();
        if (nextShort == 1025) {
            this._isAddInFunctions = false;
        } else if (nextShort == 14849) {
            this._isAddInFunctions = true;
            if (this.field_1_number_of_sheets != 1) {
                throw new RuntimeException("Expected 0x0001 for number of sheets field in 'Add-In Functions' but got (" + this.field_1_number_of_sheets + ")");
            }
        } else {
            throw new RuntimeException("invalid EXTERNALBOOK code (" + Integer.toHexString(nextShort) + ")");
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getName());
        sb.append(" [SUPBOOK ");
        if (isExternalReferences()) {
            sb.append("External References");
            sb.append(" nSheets=");
            sb.append(this.field_1_number_of_sheets);
            sb.append(" url=");
            sb.append(this.field_2_encoded_url);
        } else if (this._isAddInFunctions) {
            sb.append("Add-In Functions");
        } else {
            sb.append("Internal References ");
            sb.append(" nSheets= ");
            sb.append(this.field_1_number_of_sheets);
        }
        sb.append("]");
        return sb.toString();
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        if (!isExternalReferences()) {
            return 4;
        }
        int sum = 2 + StringUtil.getEncodedSize(this.field_2_encoded_url);
        int i = 0;
        while (true) {
            String[] strArr = this.field_3_sheet_names;
            if (i >= strArr.length) {
                return sum;
            }
            sum += StringUtil.getEncodedSize(strArr[i]);
            i++;
        }
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_number_of_sheets);
        if (isExternalReferences()) {
            StringUtil.writeUnicodeString(out, this.field_2_encoded_url);
            int i = 0;
            while (true) {
                String[] strArr = this.field_3_sheet_names;
                if (i < strArr.length) {
                    StringUtil.writeUnicodeString(out, strArr[i]);
                    i++;
                } else {
                    return;
                }
            }
        } else {
            out.writeShort(this._isAddInFunctions ? 14849 : InputDeviceCompat.SOURCE_GAMEPAD);
        }
    }

    public void setNumberOfSheets(short number) {
        this.field_1_number_of_sheets = number;
    }

    public short getNumberOfSheets() {
        return this.field_1_number_of_sheets;
    }

    public short getSid() {
        return sid;
    }

    public String getURL() {
        String encodedUrl = this.field_2_encoded_url;
        char charAt = encodedUrl.charAt(0);
        if (charAt == 0) {
            return encodedUrl.substring(1);
        }
        if (charAt == 1) {
            return decodeFileName(encodedUrl);
        }
        if (charAt != 2) {
            return encodedUrl;
        }
        return encodedUrl.substring(1);
    }

    private static String decodeFileName(String encodedUrl) {
        return encodedUrl.substring(1);
    }

    public String[] getSheetNames() {
        return (String[]) this.field_3_sheet_names.clone();
    }
}
