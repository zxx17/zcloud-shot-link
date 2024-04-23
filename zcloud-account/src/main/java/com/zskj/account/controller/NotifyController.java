package com.zskj.account.controller;

import com.google.code.kaptcha.Producer;
import com.zskj.account.service.NotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/23
 * <p>
 * 短信业务前端控制器
 * </p>
 */

@Slf4j
@RestController
@RequestMapping("/api/account/v1")
public class NotifyController {

    @Autowired
    private Producer captchaProducer;


    @Autowired
    private NotifyService notifyService;


    /**
     * 生成验证码
     * @param request request
     * @param response response
     */
    @GetMapping("/captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response){
        String captchaText = captchaProducer.createText();
        log.info("验证码内容:{}",captchaText);
        //存储redis,配置过期时间 TODO
        BufferedImage bufferedImage = captchaProducer.createImage(captchaText);
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            ImageIO.write(bufferedImage,"jpg",outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            log.error("获取流出错:{}",e.getMessage());
        }
    }

}
