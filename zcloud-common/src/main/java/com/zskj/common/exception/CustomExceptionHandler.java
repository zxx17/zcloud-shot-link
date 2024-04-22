package com.zskj.common.exception;

import com.zskj.common.util.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

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


}
