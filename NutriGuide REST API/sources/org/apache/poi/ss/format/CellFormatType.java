package org.apache.poi.ss.format;

public enum CellFormatType {
    GENERAL {
        /* access modifiers changed from: package-private */
        public CellFormatter formatter(String pattern) {
            return new CellGeneralFormatter();
        }

        /* access modifiers changed from: package-private */
        public boolean isSpecial(char ch) {
            return false;
        }
    },
    NUMBER {
        /* access modifiers changed from: package-private */
        public boolean isSpecial(char ch) {
            return false;
        }

        /* access modifiers changed from: package-private */
        public CellFormatter formatter(String pattern) {
            return new CellNumberFormatter(pattern);
        }
    },
    DATE {
        /* access modifiers changed from: package-private */
        public boolean isSpecial(char ch) {
            return ch == '\'' || (ch <= 127 && Character.isLetter(ch));
        }

        /* access modifiers changed from: package-private */
        public CellFormatter formatter(String pattern) {
            return new CellDateFormatter(pattern);
        }
    },
    ELAPSED {
        /* access modifiers changed from: package-private */
        public boolean isSpecial(char ch) {
            return false;
        }

        /* access modifiers changed from: package-private */
        public CellFormatter formatter(String pattern) {
            return new CellElapsedFormatter(pattern);
        }
    },
    TEXT {
        /* access modifiers changed from: package-private */
        public boolean isSpecial(char ch) {
            return false;
        }

        /* access modifiers changed from: package-private */
        public CellFormatter formatter(String pattern) {
            return new CellTextFormatter(pattern);
        }
    };

    /* access modifiers changed from: package-private */
    public abstract CellFormatter formatter(String str);

    /* access modifiers changed from: package-private */
    public abstract boolean isSpecial(char c);
}
