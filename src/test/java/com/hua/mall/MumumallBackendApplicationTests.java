package com.hua.mall;

import cn.hutool.core.util.ReUtil;
import cn.hutool.crypto.SecureUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.IOException;

@SpringBootTest
class MumumallBackendApplicationTests {

    private static final String SALT = "{mumu}[mall]+plus";

    @Test
    void contextLoads() {
        String digestAsHex = DigestUtils.md5DigestAsHex((SALT + "mumumall1").getBytes());
        String s = SecureUtil.md5(digestAsHex);
        System.out.println(s);
    }

    public static void main(String[] args) {
        String content = "test11";
        if (!ReUtil.isMatch("^[a-zA-Z0-9_-]{6,16}$", content)) {
            System.out.println("不匹配：" + content);
        } else {
            System.out.println("匹配：" + content);
        }
    }

    public static void setSalt(String[] args) throws IOException {
        File dir = new File("i");
        System.out.println(System.getProperty("user.dir") + "\\images\\");
        System.out.println(dir.getCanonicalPath());
        System.out.println(dir.getAbsolutePath());
    }
}
