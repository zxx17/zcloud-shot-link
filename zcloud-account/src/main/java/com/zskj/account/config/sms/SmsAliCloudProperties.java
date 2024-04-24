package com.zskj.account.config.sms;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/22
 * <p>
 * 阿里云sms服务配置参数
 * </p>
 */

@ConfigurationProperties(prefix = "sms.ali-cloud")
@Component
@Data
public class SmsAliCloudProperties {

    /**
     * 地域url
     */
    private String endpoint;

    /**
     * sms-user accessKey
     */
    private String accessKey;

    /**
     * sms-user accessSecret
     */
    private String accessSecret;
}
