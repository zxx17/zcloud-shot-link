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

    private String url;

    private String templateId;

    private String appCode;

}
