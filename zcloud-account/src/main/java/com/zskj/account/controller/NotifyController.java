package com.zskj.account.controller;

import com.google.code.kaptcha.Producer;
import com.zskj.account.controller.request.SendCodeRequest;
import com.zskj.account.service.NotifyService;
import com.zskj.common.enums.BizCodeEnum;
import com.zskj.common.enums.account.SendCodeEnum;
import com.zskj.common.util.CommonUtil;
import com.zskj.common.util.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/23
 * <p>
 * 短信业务前端控制器
 * </p>
 */

@Slf4j
@RestController
@RequestMapping("/api/notify/v1")
public class NotifyController {

    /**
     * 生成验证码
     */
    @Autowired
    private Producer captchaProducer;


    /**
     * 短信业务服务
     */
    @Autowired
    private NotifyService notifyService;

    /**
     * redisTemplate
     */
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 验证码过期时间
     */
    private static final long CAPTCHA_CODE_EXPIRED = 1000 * 10 *  60;

    /**
     * 生成图形验证码
     *
     * @param request  request
     * @param response response
     */
    @GetMapping("/captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) {
        String captchaText = captchaProducer.createText();
        log.info("验证码内容:{}", captchaText);
        //存储redis,配置过期时间
        stringRedisTemplate.opsForValue()
                .set(getCaptchaKey(request), captchaText, CAPTCHA_CODE_EXPIRED, TimeUnit.MILLISECONDS);
        BufferedImage bufferedImage = captchaProducer.createImage(captchaText);
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            ImageIO.write(bufferedImage, "jpg", outputStream);
            outputStream.flush();
        } catch (IOException e) {
            log.error("获取流出错:{}", e.getMessage());
        }
    }

    /**
     * 发送验证码
     * @param sendCodeRequest 手机号/邮箱 验证码
     * @return resp
     */
    @PostMapping("/send_code")
    public JsonData sendCode(@RequestBody @Validated SendCodeRequest sendCodeRequest, HttpServletRequest request){
        // 校验验证码合法性
        String key = getCaptchaKey(request);
        String cacheCaptcha = stringRedisTemplate.opsForValue().get(key);
        String captcha = sendCodeRequest.getCaptcha();
        if(cacheCaptcha != null  && cacheCaptcha.equalsIgnoreCase(captcha)){
            stringRedisTemplate.delete(key);
            return notifyService.sendCode(SendCodeEnum.USER_REGISTER,sendCodeRequest.getTo());
        }else {
            return JsonData.buildResult(BizCodeEnum.CODE_CAPTCHA_ERROR);
        }
    }


    /**
     * 获取缓存验证码的key
     * @param request 用户请求
     * @return key
     */
    private String getCaptchaKey(HttpServletRequest request){
        String ip = CommonUtil.getIpAddr(request);
        String userAgent = request.getHeader("User-Agent");
        String key = "account-service:captcha:"+CommonUtil.MD5(ip+userAgent);
        log.info("验证码key:{}",key);
        return key;
    }

}
