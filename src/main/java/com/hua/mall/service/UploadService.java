package com.hua.mall.service;

import com.hua.mall.model.entity.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 描述：
 *
 * @author hua
 * @date 2022/11/16 01:00
 */
public interface UploadService {
    /**
     * 创建文件
     *
     * @param file
     * @param fileDirectory
     * @param destFile
     */
    void createFile(MultipartFile file, File fileDirectory, File destFile);

    /**
     * 新的文件名
     *
     * @param file 源文件
     * @return 文件名
     */
    String newFileName(MultipartFile file);

    /**
     * 读取 Excel 文件
     *
     * @param excelFile Excel 文件
     * @return
     * @throws IOException
     */
    List<Product> readProductsFromExcel(File excelFile) throws IOException;
}
