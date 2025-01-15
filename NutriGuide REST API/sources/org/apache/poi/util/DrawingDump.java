package org.apache.poi.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class DrawingDump {
    public static void main(String[] args) throws IOException {
        HSSFWorkbook wb = new HSSFWorkbook(new POIFSFileSystem(new FileInputStream(args[0])));
        System.out.println("Drawing group:");
        wb.dumpDrawingGroupRecords(true);
        for (int sheetNum = 1; sheetNum <= wb.getNumberOfSheets(); sheetNum++) {
            PrintStream printStream = System.out;
            printStream.println("Sheet " + sheetNum + ":");
            wb.getSheetAt(sheetNum + -1).dumpDrawingRecords(true);
        }
    }
}
