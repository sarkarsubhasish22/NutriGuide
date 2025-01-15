package org.apache.poi.hssf.util;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.Region;
import org.apache.poi.ss.util.RegionUtil;

public final class HSSFRegionUtil {
    private HSSFRegionUtil() {
    }

    private static CellRangeAddress toCRA(Region region) {
        return Region.convertToCellRangeAddress(region);
    }

    public static void setBorderLeft(short border, Region region, HSSFSheet sheet, HSSFWorkbook workbook) {
        setBorderLeft((int) border, toCRA(region), sheet, workbook);
    }

    public static void setBorderLeft(int border, CellRangeAddress region, HSSFSheet sheet, HSSFWorkbook workbook) {
        RegionUtil.setBorderLeft(border, region, sheet, workbook);
    }

    public static void setLeftBorderColor(short color, Region region, HSSFSheet sheet, HSSFWorkbook workbook) {
        setLeftBorderColor((int) color, toCRA(region), sheet, workbook);
    }

    public static void setLeftBorderColor(int color, CellRangeAddress region, HSSFSheet sheet, HSSFWorkbook workbook) {
        RegionUtil.setLeftBorderColor(color, region, sheet, workbook);
    }

    public static void setBorderRight(short border, Region region, HSSFSheet sheet, HSSFWorkbook workbook) {
        setBorderRight((int) border, toCRA(region), sheet, workbook);
    }

    public static void setBorderRight(int border, CellRangeAddress region, HSSFSheet sheet, HSSFWorkbook workbook) {
        RegionUtil.setBorderRight(border, region, sheet, workbook);
    }

    public static void setRightBorderColor(short color, Region region, HSSFSheet sheet, HSSFWorkbook workbook) {
        setRightBorderColor((int) color, toCRA(region), sheet, workbook);
    }

    public static void setRightBorderColor(int color, CellRangeAddress region, HSSFSheet sheet, HSSFWorkbook workbook) {
        RegionUtil.setRightBorderColor(color, region, sheet, workbook);
    }

    public static void setBorderBottom(short border, Region region, HSSFSheet sheet, HSSFWorkbook workbook) {
        setBorderBottom((int) border, toCRA(region), sheet, workbook);
    }

    public static void setBorderBottom(int border, CellRangeAddress region, HSSFSheet sheet, HSSFWorkbook workbook) {
        RegionUtil.setBorderBottom(border, region, sheet, workbook);
    }

    public static void setBottomBorderColor(short color, Region region, HSSFSheet sheet, HSSFWorkbook workbook) {
        setBottomBorderColor((int) color, toCRA(region), sheet, workbook);
    }

    public static void setBottomBorderColor(int color, CellRangeAddress region, HSSFSheet sheet, HSSFWorkbook workbook) {
        RegionUtil.setBottomBorderColor(color, region, sheet, workbook);
    }

    public static void setBorderTop(short border, Region region, HSSFSheet sheet, HSSFWorkbook workbook) {
        setBorderTop((int) border, toCRA(region), sheet, workbook);
    }

    public static void setBorderTop(int border, CellRangeAddress region, HSSFSheet sheet, HSSFWorkbook workbook) {
        RegionUtil.setBorderTop(border, region, sheet, workbook);
    }

    public static void setTopBorderColor(short color, Region region, HSSFSheet sheet, HSSFWorkbook workbook) {
        setTopBorderColor((int) color, toCRA(region), sheet, workbook);
    }

    public static void setTopBorderColor(int color, CellRangeAddress region, HSSFSheet sheet, HSSFWorkbook workbook) {
        RegionUtil.setTopBorderColor(color, region, sheet, workbook);
    }
}
