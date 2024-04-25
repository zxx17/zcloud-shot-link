package com.zskj.account.service.impl;

import com.zskj.account.component.SmsComponent;
import com.zskj.account.service.NotifyService;
import com.zskj.common.constant.RedisKeyConstant;
import com.zskj.common.enums.BizCodeEnum;
import com.zskj.common.enums.SendCodeEnum;
import com.zskj.common.util.CheckUtil;
import com.zskj.common.util.CommonUtil;
import com.zskj.common.util.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/23
 * <p>
 * 通知服务
 * </p>
 */

@Service
@Slf4j
public class NotifyServiceImpl implements NotifyService {


    /**
     * 验证码有效期
     */
    private static final int CODE_EXPIRED = 60 * 1000 * 10;

    /**
     * 重复发送时间限制
     */
    private static final int RESEND_LIMIT = 60 * 1000;

    /**
     * 发送短信组件
     */
    @Autowired
    private SmsComponent smsComponent;

    /**
     * redisTemplate
     */
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 发送验证码
     *
     * @param sendCodeEnum flag
     * @param to           target
     * @return jsonData
     */
    @Override
    public JsonData sendCode(SendCodeEnum sendCodeEnum, String to) {
        String cacheKey = String.format(RedisKeyConstant.CHECK_CODE_KEY, sendCodeEnum.name(), to);
        String cacheValue = stringRedisTemplate.opsForValue().get(cacheKey);
        //如果不为空，再判断是否是60秒内重复发送 0122_232131321314132
        if (StringUtils.isNotBlank(cacheValue)) {
            long ttl = Long.parseLong(cacheValue.split("_")[1]);
            long leftTime = CommonUtil.getCurrentTimestamp() - ttl;
            if (leftTime < (RESEND_LIMIT)) {
                log.info("重复发送短信验证码，时间间隔:{}ms", leftTime);
                return JsonData.buildResult(BizCodeEnum.CODE_LIMITED);
            }
        }
        String code = CommonUtil.getRandomCode(6);
        //生成拼接好验证码
        String value = code + "_" + CommonUtil.getCurrentTimestamp();
        stringRedisTemplate.opsForValue().set(cacheKey, value, CODE_EXPIRED, TimeUnit.MILLISECONDS);
        if (CheckUtil.isEmail(to)) {
            //发送邮箱验证码  TODO
        } else if (CheckUtil.isPhone(to)) {
            //发送手机验证码
            smsComponent.send(to, code);
        }
        return JsonData.buildSuccess();
    }

    /**
     * 验证码校验
     * @param sendCodeEnum flag
     * @param to target
     * @param code sms-code
     * @return jsonData
     * TODO 这里的校验和删除不是原子操作
     */
    @Override
    public boolean checkCode(SendCodeEnum sendCodeEnum, String to, String code) {
        String key = String.format(RedisKeyConstant.CHECK_CODE_KEY, sendCodeEnum.name(), to);
        String cacheValue = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(cacheValue)){
            String cacheCode = cacheValue.split("_")[0];
            if(cacheCode.equalsIgnoreCase(code)){
                //删除验证码
                stringRedisTemplate.delete(key);
                return true;
            }
        }
        return false;
    }


}
