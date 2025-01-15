package org.apache.poi.hssf.record.formula.function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.poi.hssf.record.formula.Ptg;

final class FunctionMetadataReader {
    private static final String[] DIGIT_ENDING_FUNCTION_NAMES;
    private static final Set DIGIT_ENDING_FUNCTION_NAMES_SET;
    private static final String ELLIPSIS = "...";
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private static final String METADATA_FILE_NAME = "functionMetadata.txt";
    private static final Pattern SPACE_DELIM_PATTERN = Pattern.compile(" ");
    private static final Pattern TAB_DELIM_PATTERN = Pattern.compile("\t");

    FunctionMetadataReader() {
    }

    static {
        String[] strArr = {"LOG10", "ATAN2", "DAYS360", "SUMXMY2", "SUMX2MY2", "SUMX2PY2"};
        DIGIT_ENDING_FUNCTION_NAMES = strArr;
        DIGIT_ENDING_FUNCTION_NAMES_SET = new HashSet(Arrays.asList(strArr));
    }

    public static FunctionMetadataRegistry createRegistry() {
        InputStream is = FunctionMetadataReader.class.getResourceAsStream(METADATA_FILE_NAME);
        if (is != null) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                FunctionDataBuilder fdb = new FunctionDataBuilder(400);
                while (true) {
                    try {
                        String line = br.readLine();
                        if (line == null) {
                            br.close();
                            return fdb.build();
                        } else if (line.length() >= 1) {
                            if (line.charAt(0) != '#') {
                                if (line.trim().length() >= 1) {
                                    processLine(fdb, line);
                                }
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (UnsupportedEncodingException e2) {
                throw new RuntimeException(e2);
            }
        } else {
            throw new RuntimeException("resource 'functionMetadata.txt' not found");
        }
    }

    private static void processLine(FunctionDataBuilder fdb, String line) {
        String str = line;
        String[] parts = TAB_DELIM_PATTERN.split(str, -2);
        if (parts.length == 8) {
            int functionIndex = parseInt(parts[0]);
            String functionName = parts[1];
            int minParams = parseInt(parts[2]);
            int maxParams = parseInt(parts[3]);
            byte returnClassCode = parseReturnTypeCode(parts[4]);
            byte[] parameterClassCodes = parseOperandTypeCodes(parts[5]);
            boolean hasNote = parts[7].length() > 0;
            validateFunctionName(functionName);
            fdb.add(functionIndex, functionName, minParams, maxParams, returnClassCode, parameterClassCodes, hasNote);
            return;
        }
        throw new RuntimeException("Bad line format '" + str + "' - expected 8 data fields");
    }

    private static byte parseReturnTypeCode(String code) {
        if (code.length() == 0) {
            return 0;
        }
        return parseOperandTypeCode(code);
    }

    private static byte[] parseOperandTypeCodes(String codes) {
        if (codes.length() < 1) {
            return EMPTY_BYTE_ARRAY;
        }
        if (isDash(codes)) {
            return EMPTY_BYTE_ARRAY;
        }
        String[] array = SPACE_DELIM_PATTERN.split(codes);
        int nItems = array.length;
        if (ELLIPSIS.equals(array[nItems - 1])) {
            nItems--;
        }
        byte[] result = new byte[nItems];
        for (int i = 0; i < nItems; i++) {
            result[i] = parseOperandTypeCode(array[i]);
        }
        return result;
    }

    private static boolean isDash(String codes) {
        return codes.length() == 1 && codes.charAt(0) == '-';
    }

    private static byte parseOperandTypeCode(String code) {
        if (code.length() == 1) {
            char charAt = code.charAt(0);
            if (charAt == 'A') {
                return Ptg.CLASS_ARRAY;
            }
            if (charAt == 'R') {
                return 0;
            }
            if (charAt == 'V') {
                return 32;
            }
            throw new IllegalArgumentException("Unexpected operand type code '" + code + "' (" + code.charAt(0) + ")");
        }
        throw new RuntimeException("Bad operand type code format '" + code + "' expected single char");
    }

    private static void validateFunctionName(String functionName) {
        int ix = functionName.length() - 1;
        if (Character.isDigit(functionName.charAt(ix))) {
            while (ix >= 0 && Character.isDigit(functionName.charAt(ix))) {
                ix--;
            }
            if (!DIGIT_ENDING_FUNCTION_NAMES_SET.contains(functionName)) {
                throw new RuntimeException("Invalid function name '" + functionName + "' (is footnote number incorrectly appended)");
            }
        }
    }

    private static int parseInt(String valStr) {
        try {
            return Integer.parseInt(valStr);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Value '" + valStr + "' could not be parsed as an integer");
        }
    }
}
