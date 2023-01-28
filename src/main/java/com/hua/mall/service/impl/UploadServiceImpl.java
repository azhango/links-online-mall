package com.hua.mall.service.impl;

import com.hua.mall.common.ErrorCode;
import com.hua.mall.exception.BusinessException;
import com.hua.mall.model.entity.Product;
import com.hua.mall.service.UploadService;
import com.hua.mall.utils.ExcelUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * 描述：
 *
 * @author hua
 * @date 2022/11/16 01:01
 */
@Service
public class UploadServiceImpl implements UploadService {
    /**
     * 创建文件
     *
     * @param file          源文件
     * @param fileDirectory 文件目录
     * @param destFile      目标文件
     */
    @Override
    public void createFile(MultipartFile file, File fileDirectory, File destFile) {
        if (!fileDirectory.exists()) {
            if (!fileDirectory.mkdir()) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件夹创建失败");
            }
        }
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 新的文件名
     *
     * @param file 源文件
     * @return 文件名
     */
    @Override
    public String newFileName(MultipartFile file) {
        if (file == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件不存在");
        }
        // 获取源文件名
        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        // 生成UUID
        UUID uuid = UUID.randomUUID();
        String newFileName = uuid.toString().replaceAll("-", "") + suffixName;
        return newFileName;
    }

    /**
     * 读取 Excel 文件
     *
     * @param excelFile Excel 文件
     * @return
     * @throws IOException
     */
    @Override
    public List<Product> readProductsFromExcel(File excelFile) throws IOException {
        List<Product> products = new ArrayList<>();
        // 创建输入流
        FileInputStream inputStream = new FileInputStream(excelFile);
        // 处理新版本 Excel
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        // 获取给定索引处的XSSFSheet对象
        XSSFSheet firstSheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = firstSheet.iterator();
        // 如果迭代有更多的元素，则返回true。(换句话说，如果next将返回一个元素而不是抛出异常，则返回true。)
        while (iterator.hasNext()) {
            Row nextRow = iterator.next();
            Iterator<Cell> cellIterator = nextRow.cellIterator();
            Product aProduct = new Product();

            while (cellIterator.hasNext()) {
                Cell nextCell = cellIterator.next();
                // 返回此单元格的列索引
                int index = nextCell.getColumnIndex();
                switch (index) {
                    case 0:
                        aProduct.setProductName((String) ExcelUtil.getCellValue(nextCell));
                        break;
                    case 1:
                        aProduct.setImage((String) ExcelUtil.getCellValue(nextCell));
                        break;
                    case 2:
                        aProduct.setDetail((String) ExcelUtil.getCellValue(nextCell));
                        break;
                    case 3:
                        Double cellValue = (Double) ExcelUtil.getCellValue(nextCell);
                        aProduct.setCategoryId(cellValue.longValue());
                        break;
                    case 4:
                        cellValue = (Double) ExcelUtil.getCellValue(nextCell);
                        aProduct.setPrice(cellValue.intValue());
                        break;
                    case 5:
                        cellValue = (Double) ExcelUtil.getCellValue(nextCell);
                        aProduct.setStock(cellValue.intValue());
                        break;
                    case 6:
                        cellValue = (Double) ExcelUtil.getCellValue(nextCell);
                        aProduct.setProductStatus(cellValue.intValue());
                        break;
                }
            }
            products.add(aProduct);
        }
        workbook.close();
        inputStream.close();
        return products;
    }
}
