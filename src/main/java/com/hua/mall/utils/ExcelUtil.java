package com.hua.mall.utils;

import org.apache.poi.ss.usermodel.Cell;

/**
 * 描述：
 *
 * @author hua
 * @date 2022/11/13 22:53
 */
public class ExcelUtil {

    public static Object getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case NUMERIC:
                return cell.getNumericCellValue();
        }
        return null;
    }
}
