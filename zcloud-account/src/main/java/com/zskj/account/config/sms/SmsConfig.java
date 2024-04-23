package com.zskj.account.config.sms;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/22
 * <p>
 *  创建SmsClient，后续可以支持切换短信服务提供商
 * </p>
 */

@Getter
@Configuration
public class SmsConfig {

    @Autowired
    private SmsAliCloudProperties smsAliCloudProperties;


    /**
     * 阿里云sms服务配置
     * @return aliClient
     * @throws Exception e
     */
    @ConditionalOnProperty(prefix = "sms.ali-cloud", name = "enable",havingValue = "true")
    @Bean(name = "smsClient")
    public com.aliyun.dysmsapi20170525.Client createClient() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(smsAliCloudProperties.getAccessKey())
                .setAccessKeySecret(smsAliCloudProperties.getAccessSecret());
        config.endpoint = smsAliCloudProperties.getEndpoint();
        return new com.aliyun.dysmsapi20170525.Client(config);
    }

    /**
     * 阿里云市场短信服务厂商
     */
    @ConditionalOnProperty(prefix = "sms.ali-market", name = "enable",havingValue = "true")
    @Bean(name = "smsClient")
    public SmsAliCloudMarketSmsClient createClient02(){
        return new SmsAliCloudMarketSmsClient();
    }


}
