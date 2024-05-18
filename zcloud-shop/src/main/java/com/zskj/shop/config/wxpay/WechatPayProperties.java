package com.zskj.shop.config.wxpay;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Zhuo
 */
@Data
@ConfigurationProperties(prefix = "wechat.pay")
public class WechatPayProperties {

    private String appid;

    private String mchId;

    private String mchSerialNo;

    private String privateKeyPath;

    private String apiv3Key;

    private String successRedirectUrl;

    private String callbackUrl;


}
