package com.zskj.account.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/22
 * <p>
 *  阿里云sms服务配置
 * </p>
 */

@Configuration
public class SmsConfig {

    @Autowired
    private SmsProperties smsProperties;

    @ConditionalOnProperty(prefix = "sms", name = "enable",havingValue = "true")
    @Bean
    public com.aliyun.dysmsapi20170525.Client createClient() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(smsProperties.getAccessKey())
                .setAccessKeySecret(smsProperties.getAccessSecret());
        config.endpoint = smsProperties.getEndpoint();
        return new com.aliyun.dysmsapi20170525.Client(config);
    }

}
