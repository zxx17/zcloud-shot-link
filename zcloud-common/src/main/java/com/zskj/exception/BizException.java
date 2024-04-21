package com.zskj.exception;

import com.zskj.enums.BizCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/21
 * <p>
 *  业务异常类
 * </p>
 */

@Data
@EqualsAndHashCode(callSuper=true)
public class BizException extends RuntimeException{
    private int code;

    private String msg;

    public BizException(Integer code, String message) {
        super(message);
        this.code = code;
        this.msg = message;
    }



    public BizException(BizCodeEnum bizCodeEnum){
        super(bizCodeEnum.getMessage());
        this.code = bizCodeEnum.getCode();
        this.msg = bizCodeEnum.getMessage();
    }

}
