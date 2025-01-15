package org.apache.poi.hssf.usermodel;

import java.util.HashSet;
import java.util.Iterator;
import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.hssf.record.FontRecord;
import org.apache.poi.hssf.record.common.UnicodeString;

public class HSSFOptimiser {
    public static void optimiseFonts(HSSFWorkbook workbook) {
        short[] newPos = new short[(workbook.getWorkbook().getNumberOfFontRecords() + 1)];
        boolean[] zapRecords = new boolean[newPos.length];
        for (int i = 0; i < newPos.length; i++) {
            newPos[i] = (short) i;
            zapRecords[i] = false;
        }
        FontRecord[] frecs = new FontRecord[newPos.length];
        for (int i2 = 0; i2 < newPos.length; i2++) {
            if (i2 != 4) {
                frecs[i2] = workbook.getWorkbook().getFontRecordAt(i2);
            }
        }
        for (int i3 = 5; i3 < newPos.length; i3++) {
            int earlierDuplicate = -1;
            for (int j = 0; j < i3 && earlierDuplicate == -1; j++) {
                if (j != 4 && workbook.getWorkbook().getFontRecordAt(j).sameProperties(frecs[i3])) {
                    earlierDuplicate = j;
                }
            }
            if (earlierDuplicate != -1) {
                newPos[i3] = (short) earlierDuplicate;
                zapRecords[i3] = true;
            }
        }
        for (int i4 = 5; i4 < newPos.length; i4++) {
            short preDeletePos = newPos[i4];
            short newPosition = preDeletePos;
            for (int j2 = 0; j2 < preDeletePos; j2++) {
                if (zapRecords[j2]) {
                    newPosition = (short) (newPosition - 1);
                }
            }
            newPos[i4] = newPosition;
        }
        for (int i5 = 5; i5 < newPos.length; i5++) {
            if (zapRecords[i5]) {
                workbook.getWorkbook().removeFontRecord(frecs[i5]);
            }
        }
        workbook.resetFontCache();
        for (int i6 = 0; i6 < workbook.getWorkbook().getNumExFormats(); i6++) {
            ExtendedFormatRecord xfr = workbook.getWorkbook().getExFormatAt(i6);
            xfr.setFontIndex(newPos[xfr.getFontIndex()]);
        }
        HashSet doneUnicodeStrings = new HashSet();
        for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
            Iterator rIt = workbook.getSheetAt(sheetNum).rowIterator();
            while (rIt.hasNext()) {
                Iterator cIt = ((HSSFRow) rIt.next()).cellIterator();
                while (cIt.hasNext()) {
                    HSSFCell cell = (HSSFCell) cIt.next();
                    if (cell.getCellType() == 1) {
                        UnicodeString u = cell.getRichStringCellValue().getRawUnicodeString();
                        if (!doneUnicodeStrings.contains(u)) {
                            for (short i7 = 5; i7 < newPos.length; i7 = (short) (i7 + 1)) {
                                if (i7 != newPos[i7]) {
                                    u.swapFontUse(i7, newPos[i7]);
                                }
                            }
                            doneUnicodeStrings.add(u);
                        }
                    }
                }
            }
        }
    }

    public static void optimiseCellStyles(HSSFWorkbook workbook) {
        short[] newPos = new short[workbook.getWorkbook().getNumExFormats()];
        boolean[] zapRecords = new boolean[newPos.length];
        for (int i = 0; i < newPos.length; i++) {
            newPos[i] = (short) i;
            zapRecords[i] = false;
        }
        ExtendedFormatRecord[] xfrs = new ExtendedFormatRecord[newPos.length];
        for (int i2 = 0; i2 < newPos.length; i2++) {
            xfrs[i2] = workbook.getWorkbook().getExFormatAt(i2);
        }
        for (int i3 = 21; i3 < newPos.length; i3++) {
            int earlierDuplicate = -1;
            for (int j = 0; j < i3 && earlierDuplicate == -1; j++) {
                if (workbook.getWorkbook().getExFormatAt(j).equals(xfrs[i3])) {
                    earlierDuplicate = j;
                }
            }
            if (earlierDuplicate != -1) {
                newPos[i3] = (short) earlierDuplicate;
                zapRecords[i3] = true;
            }
        }
        for (int i4 = 21; i4 < newPos.length; i4++) {
            short preDeletePos = newPos[i4];
            short newPosition = preDeletePos;
            for (int j2 = 0; j2 < preDeletePos; j2++) {
                if (zapRecords[j2]) {
                    newPosition = (short) (newPosition - 1);
                }
            }
            newPos[i4] = newPosition;
        }
        for (int i5 = 21; i5 < newPos.length; i5++) {
            if (zapRecords[i5]) {
                workbook.getWorkbook().removeExFormatRecord(xfrs[i5]);
            }
        }
        for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
            Iterator rIt = workbook.getSheetAt(sheetNum).rowIterator();
            while (rIt.hasNext()) {
                Iterator cIt = ((HSSFRow) rIt.next()).cellIterator();
                while (cIt.hasNext()) {
                    HSSFCell cell = (HSSFCell) cIt.next();
                    cell.setCellStyle(workbook.getCellStyleAt(newPos[cell.getCellValueRecord().getXFIndex()]));
                }
            }
        }
    }
}
