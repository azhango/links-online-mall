package com.hua.mall.config;

import com.hua.mall.constant.UploadConstant;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 描述：
 *
 * @author hua
 * @date 2022/10/20 18:11
 */
@Configuration
public class MallWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + UploadConstant.FILE_UPLOAD_DIR);
    }
}
