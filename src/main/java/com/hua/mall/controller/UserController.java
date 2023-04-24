package com.hua.mall.controller;

import cn.hutool.core.util.DesensitizedUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hua.mall.annotation.AuthCheck;
import com.hua.mall.common.BaseResponse;
import com.hua.mall.common.ErrorCode;
import com.hua.mall.common.ResultUtils;
import com.hua.mall.constant.CommonConstant;
import com.hua.mall.exception.BusinessException;
import com.hua.mall.mapper.UserMapper;
import com.hua.mall.model.dto.UserLoginRequest;
import com.hua.mall.model.dto.UserRegisterRequest;
import com.hua.mall.model.entity.User;
import com.hua.mall.model.vo.UserVO;
import com.hua.mall.service.EmailService;
import com.hua.mall.service.UserService;
import com.hua.mall.utils.EmailUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * 用户服务
 *
 * @author hua
 */
@RestController
@RequestMapping("/user")
@Api(tags = "用户服务")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private EmailService emailService;

    /**
     * 注册账号
     *
     * @return id
     */
    @PostMapping("/register")
    @ApiOperation("注册")
    public BaseResponse<Long> register(@RequestBody @Valid UserRegisterRequest userRegisterRequest) {
        // 1. 判断注册请求体是否为空
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.userRegister(userRegisterRequest);
        return ResultUtils.success(result);
    }

    /**
     * 登陆账号
     *
     * @param userLoginRequest 登录
     * @return 用户信息
     */
    @PostMapping("/login")
    @ApiOperation("登录")
    public BaseResponse<String> login(@RequestBody @Valid UserLoginRequest userLoginRequest, HttpSession session) {
        // 1. 判断参数不能为空
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String emailAddress = userLoginRequest.getEmailAddress();
        String userPassword = userLoginRequest.getPassword();
        String jwtToken = userService.userLogin(emailAddress, userPassword, session);
        return ResultUtils.success(jwtToken);
    }

    /**
     * 获取当前登录用户信息
     *
     * @param request 用户登录态
     * @return 返回当前用户登录的信息
     */
    @GetMapping("/current")
    @ApiOperation("登录用户信息")
    public BaseResponse<UserVO> loginStatus(HttpServletRequest request) {
        User currentUser = userService.loginStatus(request);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(currentUser, userVO);
        // 1. 信息脱敏
        userVO.setEmailAddress(DesensitizedUtil.email(currentUser.getEmailAddress()));
        return ResultUtils.success(userVO);
    }

    /**
     * 更新签名
     *
     * @param signature
     * @return
     */
    @AuthCheck()
    @PostMapping("/update")
    @ApiOperation("更新签名")
    public BaseResponse updateSignature(@RequestParam String signature, HttpServletRequest request) {
        if (StringUtils.isAnyBlank(signature)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "签名内容不能为空");
        }
        User currentUser = userService.loginStatus(request);
        UpdateWrapper<User> wrapper = new UpdateWrapper();
        wrapper
                .eq("user_account", currentUser.getUserAccount())
                .set("personalized_signature", signature);
        userService.update(wrapper);
        return ResultUtils.success(null);
    }

    /**
     * 退出登录
     *
     * @param request 登录状态
     * @return 移除登录状态
     */
    @GetMapping("/logout")
    @ApiOperation("登出")
    public BaseResponse logout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        userService.logout(request);
        return ResultUtils.success(ErrorCode.SUCCESS);
    }

    /**
     * 发送验证码
     *
     * @param emailAddress 邮件
     * @return 验证码
     */
    @PostMapping("/send_email")
    @ApiOperation("发送验证码")
    public BaseResponse sendEmail(@RequestParam String emailAddress) {
        // 1.检查邮件地址是否有效
        boolean validEmailAddress = EmailUtil.isValidEmailAddress(emailAddress);
        if (!validEmailAddress) {
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "非法的邮件地址");
        }
        int emailCount = userMapper.selectByEmail(emailAddress);
        if (emailCount > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱已注册");
        }
        // 2.生成验证码
        String verificationCode = EmailUtil.getVerificationCode();
        // 3.添加到Redis
        Boolean result = emailService.saveEmailToRedis(emailAddress, verificationCode);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码已发送,请稍后再试");
        }
        // 4.发送邮箱
        emailService.sendSimpleEmail(emailAddress, CommonConstant.MAIL_SUBJECT,
                "欢迎注册,您的验证码是:" + verificationCode + ",请在5分钟内验证。");
        return ResultUtils.success(ErrorCode.SUCCESS);
    }
}
