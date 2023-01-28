package com.hua.mall.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hua.mall.common.ErrorCode;
import com.hua.mall.exception.BusinessException;
import com.hua.mall.model.entity.User;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * 描述：Jwt 工具类
 *
 * @author hua
 * @date 2022/11/11 19:10
 */
public class JwtUtil {

    // key
    public static final String JWT_KEY = "jwt_key";
    // token
    public static final String JWT_TOKEN = "jwt_token";
    // 用户id
    public static final String JWT_USER_ID = "jwt_user_id";
    // 用户名
    public static final String JWT_USER_NAME = "jwt_user_name";
    // 用户权限
    public static final String JWT_USER_ROLE = "jwt_user_role";
    // 缓存到期时间
    public static final Long JWT_EXPIRED_TIME = 1000 * 60 * 60 * 24 * 7L;

    /**
     * 根据用户返回Token
     *
     * @param user 用户信息
     * @return JwtToken
     */
    public static String getUserToken(User user) {
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Algorithm algorithm = Algorithm.HMAC256(JWT_KEY);
        String jwtToken = JWT.create()
                .withClaim(JWT_USER_ID, user.getId())
                .withClaim(JWT_USER_NAME, user.getUserAccount())
                .withClaim(JWT_USER_ROLE, user.getUserRole())
                // 过期时间
                .withExpiresAt(new Date(System.currentTimeMillis() + JWT_EXPIRED_TIME))
                .sign(algorithm);
        return jwtToken;
    }

    /**
     * 根据 Request Header 参数获取用户信息
     *
     * @param request Http 请求
     * @return 用户
     */
    public static User getJwtToken(HttpServletRequest request) {
        HttpSession session = request.getSession();
        // 获取请中的token
        String token = (String) session.getAttribute(JWT_TOKEN);
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Token 不能为空");
        }
        // 创建新的算法实例
        Algorithm algorithm = Algorithm.HMAC256(JWT_KEY);
        // 构建JWT校验工具
        JWTVerifier verifier = JWT.require(algorithm).build();
        User currentUser;
        try {
            // 解码请求头中的Token
            DecodedJWT jwt = verifier.verify(token);
            currentUser = new User();
            // 设置User属性
            currentUser.setId(jwt.getClaim(JWT_USER_ID).asLong());
            currentUser.setUserAccount(jwt.getClaim(JWT_USER_NAME).asString());
            currentUser.setUserRole(jwt.getClaim(JWT_USER_ROLE).asString());
        } catch (TokenExpiredException e) {
            // Token 过期
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Token 已过期");
        } catch (JWTDecodeException e) {
            // Token 解析失败
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Token 解析失败");
        }
        return currentUser;
    }
}
