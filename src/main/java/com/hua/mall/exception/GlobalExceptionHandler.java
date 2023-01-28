package com.hua.mall.exception;

import com.hua.mall.common.BaseResponse;
import com.hua.mall.common.ErrorCode;
import com.hua.mall.common.ResultUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 全局异常处理器
 *
 * @author hua
 */
@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    /**
     * 全局业务异常处理
     *
     * @param e 传进来的是什么信息就返回什么信息
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("businessException: " + e.getMessage(), e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    /**
     * 全局运行时异常处理
     *
     * @param e 运行时出现的错误
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("runtimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException：", e);
        return handleBindingResult(e.getBindingResult());
    }

    private BaseResponse handleBindingResult(BindingResult result) {
        // 将异常处理为对外暴露的提示
        List<String> list = new ArrayList<>();
        // 判断里边是不是包含错误
        if (result.hasErrors()) {
            //拿到错误列表
            List<ObjectError> allErrors = result.getAllErrors();
            for (ObjectError objectError : allErrors) {
                // 拿到错误信息
                String message = objectError.getDefaultMessage();
                list.add(message);
            }
        }
        if (list.size() == 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return ResultUtils.error(ErrorCode.SUCCESS.getCode(), list.toString());
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse handle(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        StringBuilder builder = new StringBuilder();
        for (ConstraintViolation<?> violation : violations) {
            builder.append(violation.getMessage());
            break;
        }
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR.getCode(), builder.toString());
    }
}
