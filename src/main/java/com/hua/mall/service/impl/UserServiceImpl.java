package com.hua.mall.service.impl;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.ReUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hua.mall.common.ErrorCode;
import com.hua.mall.exception.BusinessException;
import com.hua.mall.mapper.UserMapper;
import com.hua.mall.model.dto.UserRegisterRequest;
import com.hua.mall.model.entity.User;
import com.hua.mall.service.EmailService;
import com.hua.mall.service.UserService;
import com.hua.mall.utils.EmailUtil;
import com.hua.mall.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.concurrent.TimeUnit;

import static com.hua.mall.constant.UserConstant.*;

/**
 * @author hua
 * @description 针对表【user(用户表 )】的数据库操作Service实现
 * @createDate 2022-10-06 02:14:15
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {


    @Resource
    private UserMapper userMapper;

    @Resource
    private EmailService emailService;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 用户注册
     *
     * @param userRegisterRequest 注册请求
     * @return 返回注册成功
     */
    @Override
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        String password = userRegisterRequest.getPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String emailAddress = userRegisterRequest.getEmailAddress();
        String verificationCode = userRegisterRequest.getVerificationCode();
        // 1. 校验
        // 验证邮箱是否正确
        boolean validEmailAddress = EmailUtil.isValidEmailAddress(emailAddress);
        if (!validEmailAddress) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "非法邮件地址");
        }
        // 判断两个密码是否一致
        if (!password.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不一致");
        }
        synchronized (emailAddress.intern()) {
            // 邮箱是否已注册
            int emailCount = userMapper.selectByEmail(emailAddress);
            if (emailCount > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱已注册");
            }
            // 验证码
            Boolean emailAndCode = emailService.verifyMailbox(emailAddress, verificationCode);
            if (!emailAndCode) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码不正确");
            }
            // 查询是否存在
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("email_address", emailAddress);
            long count = userMapper.selectCount(wrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱已存在");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
            // 3. 插入数据
            User user = new User();
            user.setUserAccount(emailAddress);
            user.setUserPassword(encryptPassword);
            user.setEmailAddress(emailAddress);
            // 判断是否插入数据成功
            boolean result = this.save(user);
            if (!result) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return DesensitizedUtil.userId();
        }
    }

    /**
     * 用户登录
     *
     * @param emailAddress 账号
     * @param password     密码
     * @return 返回登录用户的信息
     */
    @Override
    public String userLogin(String emailAddress, String password, HttpSession session) {
        // 1. 校验
        if (StringUtils.isAnyEmpty(emailAddress, password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码不能为空");
        }
        // 验证邮箱格式
        if (!EmailUtil.isValidEmailAddress(emailAddress)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        // 3. 查询用户是否存在并返回登录
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("email_address", emailAddress);
        wrapper.eq("user_password", encryptPassword);
        User user = userMapper.selectOne(wrapper);
        // 4. 设置登录态
        if (user == null) {
            log.info("user login failed , Account cannot match Password: {}", password);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不存在或密码错误");
        }
        String token = JwtUtil.getUserToken(user);
        RBucket<Object> bucket = redissonClient.getBucket(String.valueOf(user.getId()));
        if (!bucket.isExists()) {
            bucket.set(token, 604800, TimeUnit.SECONDS);
        }
        return token;
    }

    /**
     * 获取当前登录用户信息
     *
     * @param request
     * @return 返回当前用户登录的信息
     */
    @Override
    public User loginStatus(HttpServletRequest request) {
        String token = request.getHeader("token");
        User currentUser = JwtUtil.getJwtToken(token);
        // 2. 通过ID主键查找当前用户
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 退出登录
     *
     * @param request 登录状态
     */
    @Override
    public void logout(HttpServletRequest request) {
        User currentUser = loginStatus(request);
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 刷新Token
        RBucket<String> bucket = redissonClient.getBucket(String.valueOf(currentUser.getId()));
        String token = "";
        if (bucket.isExists()) {
            token = bucket.get();
        } else {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
    }

}
