package com.hua.mall.service;

/**
 * @author hua
 * @description 针对表【user(用户表 )】的数据库操作Service
 * @createDate 2022-10-06 02:14:15
 */
public interface EmailService {
    /**
     * 发送邮箱
     *
     * @param to      发给谁？
     * @param subject 邮件主题
     * @param text    邮件内容
     */
    void sendSimpleEmail(String to, String subject, String text);

    /**
     * Email 保存到 Redis
     *
     * @param emailAddress     邮箱
     * @param verificationCode 验证码
     * @return Redis
     */
    Boolean saveEmailToRedis(String emailAddress, String verificationCode);

    /**
     * 验证邮箱
     *
     * @param emailAddress     邮箱
     * @param verificationCode 验证码
     * @return 验证是否成功
     */
    Boolean verifyMailbox(String emailAddress, String verificationCode);
}
