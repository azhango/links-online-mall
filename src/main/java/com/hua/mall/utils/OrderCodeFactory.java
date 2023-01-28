package com.hua.mall.utils;

import cn.hutool.core.util.RandomUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 描述：生成订单No工具类
 *
 * @author hua
 * @date 2022/10/31 14:45
 */
public class OrderCodeFactory {

    /**
     * 获取当前时间
     *
     * @return
     */
    private static String getDateTime() {
        DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return sdf.format(new Date());
    }

    /**
     * 获取随机数
     *
     * @return
     */
    private static Long getRandom(Long n) {
        return RandomUtil.randomLong(10000, 100000) + n;
    }

    /**
     * 生成订单No
     *
     * @param userId 用户主键id
     * @return
     */
    public static String getOrderCode(Long userId) {
        return getDateTime() + getRandom(userId);
    }
}
