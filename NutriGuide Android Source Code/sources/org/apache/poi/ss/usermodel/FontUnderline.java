package org.apache.poi.ss.usermodel;

public enum FontUnderline {
    SINGLE(1),
    DOUBLE(2),
    SINGLE_ACCOUNTING(3),
    DOUBLE_ACCOUNTING(4),
    NONE(5);
    
    private static FontUnderline[] _table;
    private int value;

    static {
        _table = new FontUnderline[6];
        for (FontUnderline c : values()) {
            _table[c.getValue()] = c;
        }
    }

    private FontUnderline(int val) {
        this.value = val;
    }

    public int getValue() {
        return this.value;
    }

    /* renamed from: org.apache.poi.ss.usermodel.FontUnderline$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$ss$usermodel$FontUnderline = null;

        static {
            int[] iArr = new int[FontUnderline.values().length];
            $SwitchMap$org$apache$poi$ss$usermodel$FontUnderline = iArr;
            try {
                iArr[FontUnderline.DOUBLE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$FontUnderline[FontUnderline.DOUBLE_ACCOUNTING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$FontUnderline[FontUnderline.SINGLE_ACCOUNTING.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$FontUnderline[FontUnderline.NONE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$FontUnderline[FontUnderline.SINGLE.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    public byte getByteValue() {
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$FontUnderline[ordinal()];
        if (i == 1) {
            return 2;
        }
        if (i == 2) {
            return 34;
        }
        if (i == 3) {
            return 33;
        }
        if (i != 4) {
            return 1;
        }
        return 0;
    }

    public static FontUnderline valueOf(int value2) {
        return _table[value2];
    }

    public static FontUnderline valueOf(byte value2) {
        if (value2 == 1) {
            return SINGLE;
        }
        if (value2 == 2) {
            return DOUBLE;
        }
        if (value2 == 33) {
            return SINGLE_ACCOUNTING;
        }
        if (value2 != 34) {
            return NONE;
        }
        return DOUBLE_ACCOUNTING;
    }
}
