package com.hua.mall.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 描述：
 *
 * @author hua
 * @date 2022/10/20 17:57
 */
@Component
public class UploadConstant {

    public static final String WATER_MARK_JPG = "logo.jpg";
    public static final Integer SCALE_SIZE = 1;
    public static final Float IMAGE_OPACITY = 0.5F;

    /**
     * 上传文件地址
     */
    public static String FILE_UPLOAD_DIR;

    @Value("${web.upload-path}")
    private void setFileUploadDir(String fileUploadDir) {
        FILE_UPLOAD_DIR = fileUploadDir;
    }
}
