package com.zskj.common.exception;

import com.zskj.common.util.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/21
 * <p>
 *  异常处理器
 * </p>
 */

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler {


    /**
     * 业务异常 | 系统异常 拦截
     * @param e 异常
     */
    @ExceptionHandler
    @ResponseBody
    public JsonData handler(Exception e){
        if(e instanceof BizException){
            BizException bizException = (BizException) e;
            log.error("[业务异常]{}",e.getMessage());
            return JsonData.buildCodeAndMsg(bizException.getCode(),bizException.getMsg());
        }else {
            log.error("[系统异常]{}",e.getMessage());
            return JsonData.buildError("系统异常");
        }
    }

    /**
     * 处理Get请求中 使用@Valid 验证路径中请求实体校验失败后抛出的异常
     * @param e 异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public JsonData bindExceptionHandler(BindException e) {
        String message = e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining());
        return JsonData.buildError(message);
    }

    /**
     * 处理请求参数格式错误 @RequestParam上validate失败后抛出的异常是javax.validation.ConstraintViolationException
     * @param e 异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public JsonData constraintViolationExceptionHandler(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining());
        return JsonData.buildError(message);
    }

    /**
     * 处理请求参数格式错误 @RequestBody上validate失败后抛出的异常是MethodArgumentNotValidException异常。
     * @param e 异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public JsonData methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining());
        return JsonData.buildError(message);
    }



}
