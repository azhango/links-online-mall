package com.hua.mall.service.impl;

import com.hua.mall.constant.CommonConstant;
import com.hua.mall.service.EmailService;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 描述：
 *
 * @author hua
 * @date 2022/11/08 15:46
 */
@Service
public class EmailServiceImpl implements EmailService {

    @Resource
    private JavaMailSender mailSender;

    @Override
    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        // 来自
        mailMessage.setFrom(CommonConstant.MAIL_FROM);
        // 去哪
        mailMessage.setTo(to);
        // 主题
        mailMessage.setSubject(subject);
        // 内容
        mailMessage.setText(text);
        // 发送
        mailSender.send(mailMessage);
    }

    @Override
    public Boolean saveEmailToRedis(String emailAddress, String verificationCode) {
        // 创建客户端
        RedissonClient client = Redisson.create();
        // 获取Key
        RBucket<String> bucket = client.getBucket(emailAddress);
        // 检查对象是否存在
        boolean exists = bucket.isExists();
        if (!exists) {
            // 设置Value 300S 过期
            bucket.set(verificationCode, 300, TimeUnit.SECONDS);
            return true;
        }
        return false;
    }

    @Override
    public Boolean verifyMailbox(String emailAddress, String verificationCode) {
        // 创建客户端
        RedissonClient client = Redisson.create();
        // 获取Key
        RBucket<String> bucket = client.getBucket(emailAddress);
        // 检查对象是否存在
        boolean exists = bucket.isExists();
        if (exists) {
            String key = bucket.get();
            if (key.equals(verificationCode)) {
                return true;
            }
        }
        return false;
    }
}
