package com.hua.mall.utils;

import com.hua.mall.common.ErrorCode;
import com.hua.mall.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 描述： 邮箱工具
 *
 * @author hua
 * @date 2022/11/08 00:09
 */
public class EmailUtil {
    /**
     * A 6-bit verification code is randomly generated
     * @return verification code
     */
    public static String getVerificationCode() {
        List<String> verificationChars = Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
        // shuffle() 这个方法会打散数组顺序
        Collections.shuffle(verificationChars);
        String result = "";
        for (int i = 0; i < 6; i++) {
            // 获取前六位
            result += verificationChars.get(i);
        }
        return result;
    }

    /**
     * Verify that the mailbox is correct
     * @param emailAddress Email
     * @return True Or False
     */
    public static boolean isValidEmailAddress(String emailAddress) {
        boolean valid = true;
        if (StringUtils.isEmpty(emailAddress)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean matches = Pattern.matches("^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$", emailAddress);
        if (!matches) {
            valid = false;
        }
        return valid;
    }
}
