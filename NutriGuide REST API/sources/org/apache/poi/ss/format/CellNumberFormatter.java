package org.apache.poi.ss.format;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.util.Collections;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.regex.Matcher;
import org.apache.poi.ss.format.CellFormatPart;

public class CellNumberFormatter extends CellFormatter {
    /* access modifiers changed from: private */
    public static final CellFormatter SIMPLE_FLOAT = new CellNumberFormatter("#.#");
    /* access modifiers changed from: private */
    public static final CellFormatter SIMPLE_INT = new CellNumberFormatter("#");
    static final CellFormatter SIMPLE_NUMBER = new CellFormatter("General") {
        public void formatValue(StringBuffer toAppendTo, Object value) {
            if (value != null) {
                if (!(value instanceof Number)) {
                    CellTextFormatter.SIMPLE_TEXT.formatValue(toAppendTo, value);
                } else if (((Number) value).doubleValue() % 1.0d == 0.0d) {
                    CellNumberFormatter.SIMPLE_INT.formatValue(toAppendTo, value);
                } else {
                    CellNumberFormatter.SIMPLE_FLOAT.formatValue(toAppendTo, value);
                }
            }
        }

        public void simpleValue(StringBuffer toAppendTo, Object value) {
            formatValue(toAppendTo, value);
        }
    };
    private Special afterFractional;
    private Special afterInteger;
    private DecimalFormat decimalFmt;
    /* access modifiers changed from: private */
    public Special decimalPoint;
    private String denominatorFmt;
    private List<Special> denominatorSpecials;
    private final String desc;
    /* access modifiers changed from: private */
    public Special exponent;
    private List<Special> exponentDigitSpecials;
    private List<Special> exponentSpecials;
    private List<Special> fractionalSpecials;
    /* access modifiers changed from: private */
    public boolean improperFraction;
    private boolean integerCommas;
    private List<Special> integerSpecials;
    private int maxDenominator;
    /* access modifiers changed from: private */
    public Special numerator;
    private String numeratorFmt;
    private List<Special> numeratorSpecials;
    private String printfFmt;
    private double scale = 1.0d;
    /* access modifiers changed from: private */
    public Special slash;
    /* access modifiers changed from: private */
    public final List<Special> specials;

    static /* synthetic */ double access$1034(CellNumberFormatter x0, double x1) {
        double d = x0.scale * x1;
        x0.scale = d;
        return d;
    }

    static class Special {
        final char ch;
        int pos;

        Special(char ch2, int pos2) {
            this.ch = ch2;
            this.pos = pos2;
        }

        public String toString() {
            return "'" + this.ch + "' @ " + this.pos;
        }
    }

    static class StringMod implements Comparable<StringMod> {
        public static final int AFTER = 2;
        public static final int BEFORE = 1;
        public static final int REPLACE = 3;
        Special end;
        boolean endInclusive;
        final int op;
        final Special special;
        boolean startInclusive;
        CharSequence toAdd;

        private StringMod(Special special2, CharSequence toAdd2, int op2) {
            this.special = special2;
            this.toAdd = toAdd2;
            this.op = op2;
        }

        public StringMod(Special start, boolean startInclusive2, Special end2, boolean endInclusive2, char toAdd2) {
            this(start, startInclusive2, end2, endInclusive2);
            this.toAdd = toAdd2 + "";
        }

        public StringMod(Special start, boolean startInclusive2, Special end2, boolean endInclusive2) {
            this.special = start;
            this.startInclusive = startInclusive2;
            this.end = end2;
            this.endInclusive = endInclusive2;
            this.op = 3;
            this.toAdd = "";
        }

        public int compareTo(StringMod that) {
            int diff = this.special.pos - that.special.pos;
            if (diff != 0) {
                return diff;
            }
            return this.op - that.op;
        }

        public boolean equals(Object that) {
            try {
                return compareTo((StringMod) that) == 0;
            } catch (RuntimeException e) {
                return false;
            }
        }

        public int hashCode() {
            return this.special.hashCode() + this.op;
        }
    }

    private class NumPartHandler implements CellFormatPart.PartHandler {
        private char insertSignForExponent;

        private NumPartHandler() {
        }

        public String handlePart(Matcher m, String part, CellFormatType type, StringBuffer desc) {
            int pos = desc.length();
            char firstCh = part.charAt(0);
            if (firstCh != '#') {
                if (firstCh == '%') {
                    CellNumberFormatter.access$1034(CellNumberFormatter.this, 100.0d);
                } else if (firstCh != '?') {
                    if (firstCh != 'E' && firstCh != 'e') {
                        switch (firstCh) {
                            case '.':
                                if (CellNumberFormatter.this.decimalPoint == null && CellNumberFormatter.this.specials.size() > 0) {
                                    CellNumberFormatter.this.specials.add(CellNumberFormatter.this.decimalPoint = new Special('.', pos));
                                    break;
                                }
                            case '/':
                                if (CellNumberFormatter.this.slash == null && CellNumberFormatter.this.specials.size() > 0) {
                                    CellNumberFormatter cellNumberFormatter = CellNumberFormatter.this;
                                    Special unused = cellNumberFormatter.numerator = cellNumberFormatter.previousNumber();
                                    if (CellNumberFormatter.this.numerator == CellNumberFormatter.firstDigit(CellNumberFormatter.this.specials)) {
                                        boolean unused2 = CellNumberFormatter.this.improperFraction = true;
                                    }
                                    CellNumberFormatter.this.specials.add(CellNumberFormatter.this.slash = new Special('.', pos));
                                    break;
                                }
                            case '0':
                                break;
                            default:
                                return null;
                        }
                    } else if (CellNumberFormatter.this.exponent == null && CellNumberFormatter.this.specials.size() > 0) {
                        CellNumberFormatter.this.specials.add(CellNumberFormatter.this.exponent = new Special('.', pos));
                        this.insertSignForExponent = part.charAt(1);
                        return part.substring(0, 1);
                    }
                }
                return part;
            }
            if (this.insertSignForExponent != 0) {
                CellNumberFormatter.this.specials.add(new Special(this.insertSignForExponent, pos));
                desc.append(this.insertSignForExponent);
                this.insertSignForExponent = 0;
                pos++;
            }
            for (int i = 0; i < part.length(); i++) {
                CellNumberFormatter.this.specials.add(new Special(part.charAt(i), pos + i));
            }
            return part;
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public CellNumberFormatter(String format) {
        super(format);
        int precision;
        boolean first;
        LinkedList linkedList = new LinkedList();
        this.specials = linkedList;
        StringBuffer descBuf = CellFormatPart.parseFormat(format, CellFormatType.NUMBER, new NumPartHandler());
        if (!((this.decimalPoint == null && this.exponent == null) || this.slash == null)) {
            this.slash = null;
            this.numerator = null;
        }
        interpretCommas(descBuf);
        int fractionPartWidth = 0;
        if (this.decimalPoint == null) {
            precision = 0;
        } else {
            int precision2 = interpretPrecision();
            fractionPartWidth = precision2 + 1;
            if (precision2 == 0) {
                linkedList.remove(this.decimalPoint);
                this.decimalPoint = null;
            }
            precision = precision2;
        }
        if (precision == 0) {
            this.fractionalSpecials = Collections.emptyList();
        } else {
            this.fractionalSpecials = linkedList.subList(linkedList.indexOf(this.decimalPoint) + 1, fractionalEnd());
        }
        Special special = this.exponent;
        if (special == null) {
            this.exponentSpecials = Collections.emptyList();
        } else {
            int exponentPos = linkedList.indexOf(special);
            this.exponentSpecials = specialsFor(exponentPos, 2);
            this.exponentDigitSpecials = specialsFor(exponentPos + 2);
        }
        if (this.slash == null) {
            this.numeratorSpecials = Collections.emptyList();
            this.denominatorSpecials = Collections.emptyList();
        } else {
            Special special2 = this.numerator;
            if (special2 == null) {
                this.numeratorSpecials = Collections.emptyList();
            } else {
                this.numeratorSpecials = specialsFor(linkedList.indexOf(special2));
            }
            List<Special> specialsFor = specialsFor(linkedList.indexOf(this.slash) + 1);
            this.denominatorSpecials = specialsFor;
            if (specialsFor.isEmpty()) {
                this.numeratorSpecials = Collections.emptyList();
            } else {
                this.maxDenominator = maxValue(this.denominatorSpecials);
                this.numeratorFmt = singleNumberFormat(this.numeratorSpecials);
                this.denominatorFmt = singleNumberFormat(this.denominatorSpecials);
            }
        }
        this.integerSpecials = linkedList.subList(0, integerEnd());
        if (this.exponent == null) {
            StringBuffer fmtBuf = new StringBuffer("%");
            fmtBuf.append('0');
            fmtBuf.append(calculateIntegerPartWidth() + fractionPartWidth);
            fmtBuf.append('.');
            fmtBuf.append(precision);
            fmtBuf.append("f");
            this.printfFmt = fmtBuf.toString();
        } else {
            StringBuffer fmtBuf2 = new StringBuffer();
            boolean first2 = true;
            List<Special> specialList = this.integerSpecials;
            if (this.integerSpecials.size() == 1) {
                fmtBuf2.append("0");
                first = false;
            } else {
                for (Special s : specialList) {
                    if (isDigitFmt(s)) {
                        fmtBuf2.append(first2 ? '#' : '0');
                        first2 = false;
                    }
                }
                first = first2;
            }
            if (this.fractionalSpecials.size() > 0) {
                fmtBuf2.append('.');
                for (Special s2 : this.fractionalSpecials) {
                    if (isDigitFmt(s2)) {
                        if (!first) {
                            fmtBuf2.append('0');
                        }
                        first = false;
                    }
                }
            }
            fmtBuf2.append('E');
            List<Special> list = this.exponentSpecials;
            placeZeros(fmtBuf2, list.subList(2, list.size()));
            this.decimalFmt = new DecimalFormat(fmtBuf2.toString());
        }
        if (this.exponent != null) {
            this.scale = 1.0d;
        }
        this.desc = descBuf.toString();
    }

    private static void placeZeros(StringBuffer sb, List<Special> specials2) {
        for (Special s : specials2) {
            if (isDigitFmt(s)) {
                sb.append('0');
            }
        }
    }

    /* access modifiers changed from: private */
    public static Special firstDigit(List<Special> specials2) {
        for (Special s : specials2) {
            if (isDigitFmt(s)) {
                return s;
            }
        }
        return null;
    }

    static StringMod insertMod(Special special, CharSequence toAdd, int where) {
        return new StringMod(special, toAdd, where);
    }

    static StringMod deleteMod(Special start, boolean startInclusive, Special end, boolean endInclusive) {
        return new StringMod(start, startInclusive, end, endInclusive);
    }

    static StringMod replaceMod(Special start, boolean startInclusive, Special end, boolean endInclusive, char withChar) {
        return new StringMod(start, startInclusive, end, endInclusive, withChar);
    }

    private static String singleNumberFormat(List<Special> numSpecials) {
        return "%0" + numSpecials.size() + "d";
    }

    private static int maxValue(List<Special> s) {
        return (int) Math.round(Math.pow(10.0d, (double) s.size()) - 1.0d);
    }

    private List<Special> specialsFor(int pos, int takeFirst) {
        if (pos >= this.specials.size()) {
            return Collections.emptyList();
        }
        ListIterator<Special> it = this.specials.listIterator(pos + takeFirst);
        Special last = it.next();
        int end = pos + takeFirst;
        while (it.hasNext()) {
            Special s = it.next();
            if (!isDigitFmt(s) || s.pos - last.pos > 1) {
                break;
            }
            end++;
            last = s;
        }
        return this.specials.subList(pos, end + 1);
    }

    private List<Special> specialsFor(int pos) {
        return specialsFor(pos, 0);
    }

    private static boolean isDigitFmt(Special s) {
        return s.ch == '0' || s.ch == '?' || s.ch == '#';
    }

    /* access modifiers changed from: private */
    public Special previousNumber() {
        Special numStart;
        List<Special> list = this.specials;
        ListIterator<Special> it = list.listIterator(list.size());
        while (it.hasPrevious()) {
            Special s = it.previous();
            if (isDigitFmt(s)) {
                while (true) {
                    numStart = s;
                    Special last = s;
                    if (!it.hasPrevious()) {
                        break;
                    }
                    s = it.previous();
                    if (last.pos - s.pos > 1 || !isDigitFmt(s)) {
                        break;
                    }
                }
                return numStart;
            }
        }
        return null;
    }

    private int calculateIntegerPartWidth() {
        Special s;
        ListIterator<Special> it = this.specials.listIterator();
        int digitCount = 0;
        while (it.hasNext() && (s = it.next()) != this.afterInteger) {
            if (isDigitFmt(s)) {
                digitCount++;
            }
        }
        return digitCount;
    }

    private int interpretPrecision() {
        Special special = this.decimalPoint;
        if (special == null) {
            return -1;
        }
        int precision = 0;
        List<Special> list = this.specials;
        ListIterator<Special> it = list.listIterator(list.indexOf(special));
        if (it.hasNext()) {
            it.next();
        }
        while (it.hasNext() && isDigitFmt(it.next())) {
            precision++;
        }
        return precision;
    }

    private void interpretCommas(StringBuffer sb) {
        ListIterator<Special> it = this.specials.listIterator(integerEnd());
        boolean stillScaling = true;
        this.integerCommas = false;
        while (it.hasPrevious()) {
            if (it.previous().ch != ',') {
                stillScaling = false;
            } else if (stillScaling) {
                this.scale /= 1000.0d;
            } else {
                this.integerCommas = true;
            }
        }
        if (this.decimalPoint != null) {
            ListIterator<Special> it2 = this.specials.listIterator(fractionalEnd());
            while (it2.hasPrevious() && it2.previous().ch == ',') {
                this.scale /= 1000.0d;
            }
        }
        ListIterator<Special> it3 = this.specials.listIterator();
        int removed = 0;
        while (it3.hasNext()) {
            Special s = it3.next();
            s.pos -= removed;
            if (s.ch == ',') {
                removed++;
                it3.remove();
                sb.deleteCharAt(s.pos);
            }
        }
    }

    private int integerEnd() {
        Special special = this.decimalPoint;
        if (special != null) {
            this.afterInteger = special;
        } else {
            Special special2 = this.exponent;
            if (special2 != null) {
                this.afterInteger = special2;
            } else {
                Special special3 = this.numerator;
                if (special3 != null) {
                    this.afterInteger = special3;
                } else {
                    this.afterInteger = null;
                }
            }
        }
        Special special4 = this.afterInteger;
        return special4 == null ? this.specials.size() : this.specials.indexOf(special4);
    }

    private int fractionalEnd() {
        Special special = this.exponent;
        if (special != null) {
            this.afterFractional = special;
        } else {
            Special special2 = this.numerator;
            if (special2 != null) {
                this.afterInteger = special2;
            } else {
                this.afterFractional = null;
            }
        }
        Special special3 = this.afterFractional;
        return special3 == null ? this.specials.size() : this.specials.indexOf(special3);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v9, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v12, resolved type: org.apache.poi.ss.format.CellNumberFormatter$StringMod} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x011f  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01bf  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x01c7  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void formatValue(java.lang.StringBuffer r26, java.lang.Object r27) {
        /*
            r25 = this;
            r8 = r25
            r9 = r26
            r0 = r27
            java.lang.Number r0 = (java.lang.Number) r0
            double r0 = r0.doubleValue()
            double r2 = r8.scale
            double r0 = r0 * r2
            r10 = 0
            r11 = 1
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 >= 0) goto L_0x001a
            r2 = 1
            goto L_0x001b
        L_0x001a:
            r2 = 0
        L_0x001b:
            r12 = r2
            if (r12 == 0) goto L_0x001f
            double r0 = -r0
        L_0x001f:
            r2 = 0
            org.apache.poi.ss.format.CellNumberFormatter$Special r4 = r8.slash
            if (r4 == 0) goto L_0x0038
            boolean r4 = r8.improperFraction
            if (r4 == 0) goto L_0x002f
            r2 = r0
            r0 = 0
            r13 = r0
            r15 = r2
            goto L_0x003a
        L_0x002f:
            r4 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            double r2 = r0 % r4
            long r4 = (long) r0
            double r0 = (double) r4
            r13 = r0
            r15 = r2
            goto L_0x003a
        L_0x0038:
            r13 = r0
            r15 = r2
        L_0x003a:
            java.util.TreeSet r0 = new java.util.TreeSet
            r0.<init>()
            r7 = r0
            java.lang.StringBuffer r0 = new java.lang.StringBuffer
            java.lang.String r1 = r8.desc
            r0.<init>(r1)
            r6 = r0
            org.apache.poi.ss.format.CellNumberFormatter$Special r0 = r8.exponent
            if (r0 == 0) goto L_0x0055
            r8.writeScientific(r13, r6, r7)
            r19 = r6
            r18 = r7
            goto L_0x00b6
        L_0x0055:
            boolean r0 = r8.improperFraction
            if (r0 == 0) goto L_0x0068
            r3 = 0
            r0 = r25
            r1 = r13
            r4 = r15
            r17 = r6
            r18 = r7
            r0.writeFraction(r1, r3, r4, r6, r7)
            r19 = r17
            goto L_0x00b6
        L_0x0068:
            r17 = r6
            r18 = r7
            java.lang.StringBuffer r0 = new java.lang.StringBuffer
            r0.<init>()
            r7 = r0
            java.util.Formatter r0 = new java.util.Formatter
            r0.<init>(r7)
            r6 = r0
            java.util.Locale r0 = LOCALE
            java.lang.String r1 = r8.printfFmt
            java.lang.Object[] r2 = new java.lang.Object[r11]
            java.lang.Double r3 = java.lang.Double.valueOf(r13)
            r2[r10] = r3
            r6.format(r0, r1, r2)
            org.apache.poi.ss.format.CellNumberFormatter$Special r0 = r8.numerator
            if (r0 != 0) goto L_0x00a4
            r5 = r17
            r8.writeFractional(r7, r5)
            java.util.List<org.apache.poi.ss.format.CellNumberFormatter$Special> r3 = r8.integerSpecials
            boolean r4 = r8.integerCommas
            r0 = r25
            r1 = r7
            r2 = r5
            r17 = r4
            r4 = r18
            r19 = r5
            r5 = r17
            r0.writeInteger(r1, r2, r3, r4, r5)
            goto L_0x00b6
        L_0x00a4:
            r19 = r17
            r0 = r25
            r1 = r13
            r3 = r7
            r4 = r15
            r17 = r6
            r6 = r19
            r20 = r7
            r7 = r18
            r0.writeFraction(r1, r3, r4, r6, r7)
        L_0x00b6:
            java.util.List<org.apache.poi.ss.format.CellNumberFormatter$Special> r0 = r8.specials
            java.util.ListIterator r0 = r0.listIterator()
            java.util.Iterator r1 = r18.iterator()
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x00cd
            java.lang.Object r2 = r1.next()
            org.apache.poi.ss.format.CellNumberFormatter$StringMod r2 = (org.apache.poi.ss.format.CellNumberFormatter.StringMod) r2
            goto L_0x00ce
        L_0x00cd:
            r2 = 0
        L_0x00ce:
            r3 = 0
            java.util.BitSet r4 = new java.util.BitSet
            r4.<init>()
            r5 = 0
            r6 = 0
            r7 = 0
        L_0x00d7:
            boolean r17 = r0.hasNext()
            if (r17 == 0) goto L_0x01e0
            java.lang.Object r17 = r0.next()
            r10 = r17
            org.apache.poi.ss.format.CellNumberFormatter$Special r10 = (org.apache.poi.ss.format.CellNumberFormatter.Special) r10
            int r11 = r10.pos
            int r11 = r11 + r3
            r21 = r0
            int r0 = r10.pos
            boolean r0 = r4.get(r0)
            if (r0 != 0) goto L_0x010f
            r0 = r19
            r19 = r2
            char r2 = r0.charAt(r11)
            r22 = r5
            r5 = 35
            if (r2 != r5) goto L_0x0115
            r0.deleteCharAt(r11)
            int r3 = r3 + -1
            int r2 = r10.pos
            r4.set(r2)
            r2 = r19
            r5 = r22
            goto L_0x0119
        L_0x010f:
            r22 = r5
            r0 = r19
            r19 = r2
        L_0x0115:
            r2 = r19
            r5 = r22
        L_0x0119:
            if (r2 == 0) goto L_0x01d0
            org.apache.poi.ss.format.CellNumberFormatter$Special r8 = r2.special
            if (r10 != r8) goto L_0x01d0
            int r8 = r0.length()
            r22 = r11
            int r11 = r10.pos
            int r11 = r11 + r3
            r19 = 0
            r23 = r13
            int r13 = r2.op
            r14 = 1
            if (r13 == r14) goto L_0x01ab
            r14 = 2
            if (r13 == r14) goto L_0x0196
            r14 = 3
            if (r13 != r14) goto L_0x017b
            int r5 = r10.pos
            boolean r6 = r2.startInclusive
            if (r6 != 0) goto L_0x0141
            int r5 = r5 + 1
            int r11 = r11 + 1
        L_0x0141:
            boolean r6 = r4.get(r5)
            if (r6 == 0) goto L_0x014c
            int r5 = r5 + 1
            int r11 = r11 + 1
            goto L_0x0141
        L_0x014c:
            org.apache.poi.ss.format.CellNumberFormatter$Special r6 = r2.end
            int r6 = r6.pos
            boolean r7 = r2.endInclusive
            if (r7 == 0) goto L_0x0156
            int r6 = r6 + 1
        L_0x0156:
            int r7 = r6 + r3
            if (r11 >= r7) goto L_0x01b3
            java.lang.CharSequence r13 = r2.toAdd
            java.lang.String r14 = ""
            if (r13 != r14) goto L_0x0164
            r0.delete(r11, r7)
            goto L_0x0177
        L_0x0164:
            java.lang.CharSequence r13 = r2.toAdd
            r14 = 0
            char r13 = r13.charAt(r14)
            r20 = r11
            r14 = r20
        L_0x016f:
            if (r14 >= r7) goto L_0x0177
            r0.setCharAt(r14, r13)
            int r14 = r14 + 1
            goto L_0x016f
        L_0x0177:
            r4.set(r5, r6)
            goto L_0x01b3
        L_0x017b:
            java.lang.IllegalStateException r13 = new java.lang.IllegalStateException
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r17 = r5
            java.lang.String r5 = "Unknown op: "
            r14.append(r5)
            int r5 = r2.op
            r14.append(r5)
            java.lang.String r5 = r14.toString()
            r13.<init>(r5)
            throw r13
        L_0x0196:
            java.lang.CharSequence r13 = r2.toAdd
            java.lang.String r14 = ","
            boolean r13 = r13.equals(r14)
            if (r13 == 0) goto L_0x01a9
            int r13 = r10.pos
            boolean r13 = r4.get(r13)
            if (r13 == 0) goto L_0x01a9
            goto L_0x01b3
        L_0x01a9:
            r19 = 1
        L_0x01ab:
            int r13 = r11 + r19
            java.lang.CharSequence r14 = r2.toAdd
            r0.insert(r13, r14)
        L_0x01b3:
            int r13 = r0.length()
            int r13 = r13 - r8
            int r3 = r3 + r13
            boolean r13 = r1.hasNext()
            if (r13 == 0) goto L_0x01c7
            java.lang.Object r13 = r1.next()
            r2 = r13
            org.apache.poi.ss.format.CellNumberFormatter$StringMod r2 = (org.apache.poi.ss.format.CellNumberFormatter.StringMod) r2
            goto L_0x01c8
        L_0x01c7:
            r2 = 0
        L_0x01c8:
            r8 = r25
            r11 = r22
            r13 = r23
            goto L_0x0119
        L_0x01d0:
            r22 = r11
            r23 = r13
            r8 = r25
            r19 = r0
            r0 = r21
            r13 = r23
            r10 = 0
            r11 = 1
            goto L_0x00d7
        L_0x01e0:
            r21 = r0
            r23 = r13
            r0 = r19
            r19 = r2
            if (r12 == 0) goto L_0x01ef
            r2 = 45
            r9.append(r2)
        L_0x01ef:
            r9.append(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.ss.format.CellNumberFormatter.formatValue(java.lang.StringBuffer, java.lang.Object):void");
    }

    private void writeScientific(double value, StringBuffer output, Set<StringMod> mods) {
        char expSignRes;
        Set<StringMod> set = mods;
        StringBuffer result = new StringBuffer();
        FieldPosition fractionPos = new FieldPosition(1);
        this.decimalFmt.format(value, result, fractionPos);
        writeInteger(result, output, this.integerSpecials, mods, this.integerCommas);
        writeFractional(result, output);
        int signPos = fractionPos.getEndIndex() + 1;
        char expSignRes2 = result.charAt(signPos);
        if (expSignRes2 != '-') {
            result.insert(signPos, '+');
            expSignRes = '+';
        } else {
            expSignRes = expSignRes2;
        }
        Special expSign = this.exponentSpecials.listIterator(1).next();
        char expSignFmt = expSign.ch;
        if (expSignRes == '-' || expSignFmt == '+') {
            set.add(replaceMod(expSign, true, expSign, true, expSignRes));
        } else {
            set.add(deleteMod(expSign, true, expSign, true));
        }
        char c = expSignFmt;
        Special special = expSign;
        char c2 = expSignRes;
        writeInteger(new StringBuffer(result.substring(signPos + 1)), output, this.exponentDigitSpecials, mods, false);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:27:0x00b2, code lost:
        if (hasChar('0', r7.numeratorSpecials) == false) goto L_0x00b4;
     */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x009a  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00cd  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00d4 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0103  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x010d  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0143  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0152 A[Catch:{ RuntimeException -> 0x0170 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void writeFraction(double r21, java.lang.StringBuffer r23, double r24, java.lang.StringBuffer r26, java.util.Set<org.apache.poi.ss.format.CellNumberFormatter.StringMod> r27) {
        /*
            r20 = this;
            r7 = r20
            r8 = r24
            r10 = r27
            boolean r0 = r7.improperFraction
            r11 = 0
            if (r0 != 0) goto L_0x0116
            r0 = 32
            r13 = 2
            r14 = 63
            r1 = 48
            r15 = 0
            r6 = 1
            int r2 = (r8 > r11 ? 1 : (r8 == r11 ? 0 : -1))
            if (r2 != 0) goto L_0x0074
            java.util.List[] r2 = new java.util.List[r6]
            java.util.List<org.apache.poi.ss.format.CellNumberFormatter$Special> r3 = r7.numeratorSpecials
            r2[r15] = r3
            boolean r2 = hasChar(r1, r2)
            if (r2 != 0) goto L_0x0074
            java.util.List<org.apache.poi.ss.format.CellNumberFormatter$Special> r4 = r7.integerSpecials
            r11 = 0
            r1 = r20
            r2 = r23
            r3 = r26
            r5 = r27
            r12 = 1
            r6 = r11
            r1.writeInteger(r2, r3, r4, r5, r6)
            java.util.List<org.apache.poi.ss.format.CellNumberFormatter$Special> r1 = r7.integerSpecials
            int r2 = r1.size()
            int r2 = r2 - r12
            java.lang.Object r1 = r1.get(r2)
            org.apache.poi.ss.format.CellNumberFormatter$Special r1 = (org.apache.poi.ss.format.CellNumberFormatter.Special) r1
            java.util.List<org.apache.poi.ss.format.CellNumberFormatter$Special> r2 = r7.denominatorSpecials
            int r3 = r2.size()
            int r3 = r3 - r12
            java.lang.Object r2 = r2.get(r3)
            org.apache.poi.ss.format.CellNumberFormatter$Special r2 = (org.apache.poi.ss.format.CellNumberFormatter.Special) r2
            r3 = 3
            java.util.List[] r3 = new java.util.List[r3]
            java.util.List<org.apache.poi.ss.format.CellNumberFormatter$Special> r4 = r7.integerSpecials
            r3[r15] = r4
            java.util.List<org.apache.poi.ss.format.CellNumberFormatter$Special> r4 = r7.numeratorSpecials
            r3[r12] = r4
            java.util.List<org.apache.poi.ss.format.CellNumberFormatter$Special> r4 = r7.denominatorSpecials
            r3[r13] = r4
            boolean r3 = hasChar(r14, r3)
            if (r3 == 0) goto L_0x006c
            org.apache.poi.ss.format.CellNumberFormatter$StringMod r0 = replaceMod(r1, r15, r2, r12, r0)
            r10.add(r0)
            goto L_0x0073
        L_0x006c:
            org.apache.poi.ss.format.CellNumberFormatter$StringMod r0 = deleteMod(r1, r15, r2, r12)
            r10.add(r0)
        L_0x0073:
            return
        L_0x0074:
            r2 = 1
            int r3 = (r21 > r11 ? 1 : (r21 == r11 ? 0 : -1))
            if (r3 != 0) goto L_0x007f
            int r3 = (r8 > r11 ? 1 : (r8 == r11 ? 0 : -1))
            if (r3 != 0) goto L_0x007f
            r6 = 1
            goto L_0x0080
        L_0x007f:
            r6 = 0
        L_0x0080:
            r16 = r6
            int r3 = (r8 > r11 ? 1 : (r8 == r11 ? 0 : -1))
            if (r3 != 0) goto L_0x0095
            java.util.List[] r3 = new java.util.List[r2]
            java.util.List<org.apache.poi.ss.format.CellNumberFormatter$Special> r4 = r7.numeratorSpecials
            r3[r15] = r4
            boolean r3 = hasChar(r1, r3)
            if (r3 == 0) goto L_0x0093
            goto L_0x0095
        L_0x0093:
            r6 = 0
            goto L_0x0096
        L_0x0095:
            r6 = 1
        L_0x0096:
            r17 = r6
            if (r16 == 0) goto L_0x00b6
            r3 = 35
            java.util.List[] r4 = new java.util.List[r2]
            java.util.List<org.apache.poi.ss.format.CellNumberFormatter$Special> r5 = r7.integerSpecials
            r4[r15] = r5
            boolean r3 = hasOnly(r3, r4)
            if (r3 != 0) goto L_0x00b4
            java.util.List[] r3 = new java.util.List[r2]
            java.util.List<org.apache.poi.ss.format.CellNumberFormatter$Special> r4 = r7.numeratorSpecials
            r3[r15] = r4
            boolean r3 = hasChar(r1, r3)
            if (r3 != 0) goto L_0x00b6
        L_0x00b4:
            r6 = 1
            goto L_0x00b7
        L_0x00b6:
            r6 = 0
        L_0x00b7:
            r18 = r6
            if (r16 != 0) goto L_0x00cf
            int r3 = (r21 > r11 ? 1 : (r21 == r11 ? 0 : -1))
            if (r3 != 0) goto L_0x00cf
            if (r17 == 0) goto L_0x00cf
            java.util.List[] r3 = new java.util.List[r2]
            java.util.List<org.apache.poi.ss.format.CellNumberFormatter$Special> r4 = r7.integerSpecials
            r3[r15] = r4
            boolean r1 = hasChar(r1, r3)
            if (r1 != 0) goto L_0x00cf
            r6 = 1
            goto L_0x00d0
        L_0x00cf:
            r6 = 0
        L_0x00d0:
            r19 = r6
            if (r18 != 0) goto L_0x00e6
            if (r19 == 0) goto L_0x00d7
            goto L_0x00e6
        L_0x00d7:
            java.util.List<org.apache.poi.ss.format.CellNumberFormatter$Special> r4 = r7.integerSpecials
            r6 = 0
            r1 = r20
            r2 = r23
            r3 = r26
            r5 = r27
            r1.writeInteger(r2, r3, r4, r5, r6)
            goto L_0x0116
        L_0x00e6:
            java.util.List<org.apache.poi.ss.format.CellNumberFormatter$Special> r1 = r7.integerSpecials
            int r3 = r1.size()
            int r3 = r3 - r2
            java.lang.Object r1 = r1.get(r3)
            org.apache.poi.ss.format.CellNumberFormatter$Special r1 = (org.apache.poi.ss.format.CellNumberFormatter.Special) r1
            java.util.List[] r3 = new java.util.List[r13]
            java.util.List<org.apache.poi.ss.format.CellNumberFormatter$Special> r4 = r7.integerSpecials
            r3[r15] = r4
            java.util.List<org.apache.poi.ss.format.CellNumberFormatter$Special> r4 = r7.numeratorSpecials
            r3[r2] = r4
            boolean r3 = hasChar(r14, r3)
            if (r3 == 0) goto L_0x010d
            org.apache.poi.ss.format.CellNumberFormatter$Special r3 = r7.numerator
            org.apache.poi.ss.format.CellNumberFormatter$StringMod r0 = replaceMod(r1, r2, r3, r15, r0)
            r10.add(r0)
            goto L_0x0116
        L_0x010d:
            org.apache.poi.ss.format.CellNumberFormatter$Special r0 = r7.numerator
            org.apache.poi.ss.format.CellNumberFormatter$StringMod r0 = deleteMod(r1, r2, r0, r15)
            r10.add(r0)
        L_0x0116:
            int r0 = (r8 > r11 ? 1 : (r8 == r11 ? 0 : -1))
            if (r0 == 0) goto L_0x0138
            boolean r0 = r7.improperFraction     // Catch:{ RuntimeException -> 0x0170 }
            if (r0 == 0) goto L_0x0127
            r0 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            double r0 = r8 % r0
            int r2 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r2 != 0) goto L_0x0127
            goto L_0x0138
        L_0x0127:
            org.apache.poi.ss.format.CellNumberFormatter$Fraction r0 = new org.apache.poi.ss.format.CellNumberFormatter$Fraction     // Catch:{ RuntimeException -> 0x0170 }
            int r1 = r7.maxDenominator     // Catch:{ RuntimeException -> 0x0170 }
            r0.<init>(r8, r1)     // Catch:{ RuntimeException -> 0x0170 }
            int r1 = r0.getNumerator()     // Catch:{ RuntimeException -> 0x0170 }
            int r2 = r0.getDenominator()     // Catch:{ RuntimeException -> 0x0170 }
            r0 = r2
            goto L_0x013f
        L_0x0138:
            long r0 = java.lang.Math.round(r24)     // Catch:{ RuntimeException -> 0x0170 }
            int r1 = (int) r0     // Catch:{ RuntimeException -> 0x0170 }
            r2 = 1
            r0 = r2
        L_0x013f:
            boolean r2 = r7.improperFraction     // Catch:{ RuntimeException -> 0x0170 }
            if (r2 == 0) goto L_0x0152
            long r2 = (long) r1
            double r4 = (double) r0
            java.lang.Double.isNaN(r4)
            double r4 = r4 * r21
            long r4 = java.lang.Math.round(r4)     // Catch:{ RuntimeException -> 0x0170 }
            long r2 = r2 + r4
            int r1 = (int) r2     // Catch:{ RuntimeException -> 0x0170 }
            r11 = r1
            goto L_0x0153
        L_0x0152:
            r11 = r1
        L_0x0153:
            java.lang.String r2 = r7.numeratorFmt     // Catch:{ RuntimeException -> 0x0170 }
            java.util.List<org.apache.poi.ss.format.CellNumberFormatter$Special> r5 = r7.numeratorSpecials     // Catch:{ RuntimeException -> 0x0170 }
            r1 = r20
            r3 = r11
            r4 = r26
            r6 = r27
            r1.writeSingleInteger(r2, r3, r4, r5, r6)     // Catch:{ RuntimeException -> 0x0170 }
            java.lang.String r2 = r7.denominatorFmt     // Catch:{ RuntimeException -> 0x0170 }
            java.util.List<org.apache.poi.ss.format.CellNumberFormatter$Special> r5 = r7.denominatorSpecials     // Catch:{ RuntimeException -> 0x0170 }
            r1 = r20
            r3 = r0
            r4 = r26
            r6 = r27
            r1.writeSingleInteger(r2, r3, r4, r5, r6)     // Catch:{ RuntimeException -> 0x0170 }
            goto L_0x0174
        L_0x0170:
            r0 = move-exception
            r0.printStackTrace()
        L_0x0174:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.ss.format.CellNumberFormatter.writeFraction(double, java.lang.StringBuffer, double, java.lang.StringBuffer, java.util.Set):void");
    }

    private static boolean hasChar(char ch, List<Special>... numSpecials) {
        for (List<Special> specials2 : numSpecials) {
            for (Special s : specials2) {
                if (s.ch == ch) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean hasOnly(char ch, List<Special>... numSpecials) {
        for (List<Special> specials2 : numSpecials) {
            for (Special s : specials2) {
                if (s.ch != ch) {
                    return false;
                }
            }
        }
        return true;
    }

    private void writeSingleInteger(String fmt, int num, StringBuffer output, List<Special> numSpecials, Set<StringMod> mods) {
        StringBuffer sb = new StringBuffer();
        new Formatter(sb).format(LOCALE, fmt, new Object[]{Integer.valueOf(num)});
        writeInteger(sb, output, numSpecials, mods, false);
    }

    private void writeInteger(StringBuffer result, StringBuffer output, List<Special> numSpecials, Set<StringMod> mods, boolean showCommas) {
        char resultCh;
        StringBuffer stringBuffer = result;
        List<Special> list = numSpecials;
        Set<StringMod> set = mods;
        int pos = stringBuffer.indexOf(".") - 1;
        if (pos < 0) {
            if (this.exponent == null || list != this.integerSpecials) {
                pos = result.length() - 1;
            } else {
                pos = stringBuffer.indexOf("E") - 1;
            }
        }
        int strip = 0;
        while (strip < pos && ((resultCh = stringBuffer.charAt(strip)) == '0' || resultCh == ',')) {
            strip++;
        }
        ListIterator<Special> it = list.listIterator(numSpecials.size());
        Special lastOutputIntegerDigit = null;
        int digit = 0;
        while (true) {
            boolean z = false;
            if (!it.hasPrevious()) {
                break;
            }
            if (pos >= 0) {
                resultCh = stringBuffer.charAt(pos);
            } else {
                resultCh = '0';
            }
            Special s = it.previous();
            boolean followWithComma = showCommas && digit > 0 && digit % 3 == 0;
            boolean zeroStrip = false;
            if (resultCh != '0' || s.ch == '0' || s.ch == '?' || pos >= strip) {
                if (s.ch == '?' && pos < strip) {
                    z = true;
                }
                zeroStrip = z;
                output.setCharAt(s.pos, zeroStrip ? ' ' : resultCh);
                lastOutputIntegerDigit = s;
            } else {
                StringBuffer stringBuffer2 = output;
            }
            if (followWithComma) {
                set.add(insertMod(s, zeroStrip ? " " : ",", 2));
            }
            digit++;
            pos--;
        }
        new StringBuffer();
        if (pos >= 0) {
            int pos2 = pos + 1;
            StringBuffer extraLeadingDigits = new StringBuffer(stringBuffer.substring(0, pos2));
            if (showCommas) {
                while (pos2 > 0) {
                    if (digit > 0 && digit % 3 == 0) {
                        extraLeadingDigits.insert(pos2, ',');
                    }
                    digit++;
                    pos2--;
                }
            }
            set.add(insertMod(lastOutputIntegerDigit, extraLeadingDigits, 1));
        }
    }

    private void writeFractional(StringBuffer result, StringBuffer output) {
        int strip;
        if (this.fractionalSpecials.size() > 0) {
            int digit = result.indexOf(".") + 1;
            if (this.exponent != null) {
                strip = result.indexOf("e");
            } else {
                strip = result.length();
            }
            while (true) {
                strip--;
                if (strip <= digit || result.charAt(strip) != '0') {
                    ListIterator<Special> it = this.fractionalSpecials.listIterator();
                }
            }
            ListIterator<Special> it2 = this.fractionalSpecials.listIterator();
            while (it2.hasNext()) {
                Special s = it2.next();
                char resultCh = result.charAt(digit);
                if (resultCh != '0' || s.ch == '0' || digit < strip) {
                    output.setCharAt(s.pos, resultCh);
                } else if (s.ch == '?') {
                    output.setCharAt(s.pos, ' ');
                }
                digit++;
            }
        }
    }

    public void simpleValue(StringBuffer toAppendTo, Object value) {
        SIMPLE_NUMBER.formatValue(toAppendTo, value);
    }

    private static class Fraction {
        private final int denominator;
        private final int numerator;

        private Fraction(double value, double epsilon, int maxDenominator, int maxIterations) {
            String str;
            String str2;
            long p2;
            long q2;
            long p0;
            long a1;
            long p02;
            long p1;
            Fraction fraction = this;
            double d = value;
            int i = maxDenominator;
            int i2 = maxIterations;
            long p12 = 2147483647L;
            double r0 = value;
            long a0 = (long) Math.floor(r0);
            String str3 = " to fraction (";
            String str4 = "Overflow trying to convert ";
            if (a0 <= 2147483647L) {
                double r02 = r0;
                double r03 = (double) a0;
                Double.isNaN(r03);
                double r04 = r02;
                if (Math.abs(r03 - d) < epsilon) {
                    fraction.numerator = (int) a0;
                    fraction.denominator = 1;
                    return;
                }
                long p13 = a0;
                String str5 = ")";
                String str6 = "/";
                long a02 = a0;
                long q22 = 1;
                double r05 = r04;
                boolean stop = false;
                int n = 0;
                long q1 = 1;
                long p03 = p13;
                long q0 = 0;
                while (true) {
                    str = str3;
                    int n2 = n + 1;
                    str2 = str4;
                    double d2 = (double) a02;
                    Double.isNaN(d2);
                    double r1 = 1.0d / (r05 - d2);
                    long a12 = (long) Math.floor(r1);
                    double r06 = r05;
                    p2 = (a12 * p03) + q22;
                    long p04 = q22;
                    q2 = (a12 * q1) + q0;
                    if (p2 > p12 || q2 > p12) {
                        long overflow = p12;
                        long j = a02;
                    } else {
                        long overflow2 = p12;
                        double d3 = (double) p2;
                        long a03 = a02;
                        double d4 = (double) q2;
                        Double.isNaN(d3);
                        Double.isNaN(d4);
                        double convergent = d3 / d4;
                        if (n2 >= i2 || Math.abs(convergent - d) <= epsilon || q2 >= ((long) i)) {
                            stop = true;
                            p1 = p03;
                            a1 = q1;
                            p02 = p04;
                            p0 = a03;
                        } else {
                            p02 = p03;
                            q0 = q1;
                            p0 = a12;
                            r06 = r1;
                            p1 = p2;
                            a1 = q2;
                        }
                        if (!stop) {
                            fraction = this;
                            n = n2;
                            q1 = a1;
                            q22 = p02;
                            str3 = str;
                            str4 = str2;
                            r05 = r06;
                            a02 = p0;
                            p03 = p1;
                            p12 = overflow2;
                        } else if (n2 < i2) {
                            long j2 = p02;
                            if (q2 < ((long) i)) {
                                fraction.numerator = (int) p2;
                                fraction.denominator = (int) q2;
                                return;
                            }
                            fraction.numerator = (int) p1;
                            fraction.denominator = (int) a1;
                            return;
                        } else {
                            throw new RuntimeException("Unable to convert " + d + " to fraction after " + i2 + " iterations");
                        }
                    }
                }
                long overflow3 = p12;
                long j3 = a02;
                throw new RuntimeException(str2 + d + str + p2 + str6 + q2 + str5);
            }
            double d5 = r0;
            throw new IllegalArgumentException(str4 + d + str3 + a0 + "/" + 1 + ")");
        }

        public Fraction(double value, int maxDenominator) {
            this(value, 0.0d, maxDenominator, 100);
        }

        public int getDenominator() {
            return this.denominator;
        }

        public int getNumerator() {
            return this.numerator;
        }
    }
}
