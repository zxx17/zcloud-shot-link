package com.zskj.account.config.sms;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/23
 * <p>
 *
 * </p>
 */
@ConfigurationProperties(prefix = "sms.ali-market")
@Component
@Data
public class SmsAliCloudMarketProperties {

    /**
     * 第三方短信服务地址
     */
    private String url;

    /**
     * 短信模板id
     */
    private String templateId;

    /**
     * 第三方appCode用于鉴权
     */
    private String appCode;

}
