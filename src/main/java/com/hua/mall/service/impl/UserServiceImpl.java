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
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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

    /**
     * 用户注册
     *
     * @param userRegisterRequest 注册请求
     * @return 返回注册成功
     */
    @Override
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        String account = userRegisterRequest.getAccount();
        String password = userRegisterRequest.getPassword();
        String emailAddress = userRegisterRequest.getEmailAddress();
        String verificationCode = userRegisterRequest.getVerificationCode();
        // 1. 校验

        if (!ReUtil.isMatch(USER_ACCOUNT_CHECK, account)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误或包含特殊字符");
        }
        // 验证邮箱是否正确
        boolean validEmailAddress = EmailUtil.isValidEmailAddress(emailAddress);
        if (!validEmailAddress) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "非法邮件地址");
        }
        synchronized (account.intern()) {
            // 邮箱是否已注册
            String emailOld = userMapper.selectByEmailAddress(emailAddress);
            if (emailOld != null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱已注册");
            }
            // 验证码
            Boolean emailAndCode = emailService.verifyMailbox(emailAddress, verificationCode);
            if (!emailAndCode) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码不正确");
            }
            // 查询是否存在
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("user_account", account);
            long count = userMapper.selectCount(wrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
            // 3. 插入数据
            User user = new User();
            user.setUserAccount(account);
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
     * @param account  账号
     * @param password 密码
     * @return 返回登录用户的信息
     */
    @Override
    public String userLogin(String account, String password, HttpSession session) {
        // 1. 校验
        if (StringUtils.isAnyEmpty(account, password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码不能为空");
        }
        if (!ReUtil.isMatch(USER_ACCOUNT_CHECK, account)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误或包含特殊字符");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        // 3. 查询用户是否存在并返回登录
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_account", account);
        wrapper.eq("user_password", encryptPassword);
        User user = userMapper.selectOne(wrapper);
        // 4. 设置登录态
        if (user == null) {
            log.info("user login failed , Account cannot match Password: {}", password);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不存在或密码错误");
        }
        String token = JwtUtil.getUserToken(user);
        session.setAttribute("jwt_token", token);
        return "登录成功";
    }

    /**
     * 获取当前登录用户信息
     *
     * @param request 用户登录态
     * @return 返回当前用户登录的信息
     */
    @Override
    public User loginStatus(HttpServletRequest request) {
        User currentUser = JwtUtil.getJwtToken(request);
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
        HttpSession session = request.getSession();
        session.removeAttribute("jwt_token");
    }

}
