package com.hua.mall.aop;

import cn.hutool.core.util.IdUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 打印请求和响应信息
 *
 * @author hua
 * @date 2022-10-05 12:14:15
 */
@Aspect
@Component
@Log4j2
public class WebLogAspect {

    /**
     * 执行拦截
     */
    @Around("execution(* com.hua.mall.controller.*.*(..))")
    public Object doInterceptor(ProceedingJoinPoint joinPoint) throws Throwable {
        // 记秒
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 获取请求路径
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 生成请求唯一ID
        String requestId = IdUtil.simpleUUID();
        String requestURI = httpServletRequest.getRequestURI();
        // 获取请求参数
        Object[] args = joinPoint.getArgs();
        log.info("Args:{}", args);
        String reqParam = "[" + StringUtils.join(args, ", ") + "]";
        // 输出请求日志
        log.info("request start, Id: {}, Path: {}, IP: {}, Params: {}",
                requestId, requestURI, httpServletRequest.getRemoteHost(), reqParam);
        // 执行原方法
        Object result = joinPoint.proceed();
        // 输出日志
        stopWatch.stop();
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
        log.info("request end, Id: {}, Time: {}ms", requestId, totalTimeMillis);
        return result;
    }
}
