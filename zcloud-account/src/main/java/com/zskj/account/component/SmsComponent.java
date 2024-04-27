package com.zskj.account.component;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.tea.TeaException;
import com.aliyun.teautil.models.RuntimeOptions;
import com.zskj.account.config.sms.SmsAliCloudMarketProperties;
import com.zskj.account.config.sms.SmsAliCloudMarketSmsClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

import static com.zskj.common.constant.CommonConstant.SHOT_LINK_PROJECT_NAME;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/22
 * <p>
 * 发送短信组件
 * </p>
 */

@Slf4j
@Component
public class SmsComponent {

    /**
     * 短信服务client
     */
    @Resource(name = "smsClient")
    private Object smsClient;

    /**
     * 【第三方】短信服务配置信息
     */
    @Resource
    private SmsAliCloudMarketProperties smsAliCloudMarketProperties;

    /**
     * 调用【第三方】短信服务
     */
    @Resource
    private RestTemplate restTemplate;

    /**
     * 账号服务默认短信模板【阿里云】
     */
    private static final String ACCOUNT_DEFAULT_TEMPLATE_CODE = "SMS_465680780";


    /**
     * 发送短信
     *
     * @param to    手机号码
     * @param value code
     */
    public void send(String to, String value) {
        send(to, ACCOUNT_DEFAULT_TEMPLATE_CODE, value);
    }

    /**
     * 发送短信
     *
     * @param to           手机号码
     * @param templateCode 模板码
     * @param value        code
     */
    public void send(String to, String templateCode, String value) {
        try {
            // 阿里云sms
            if (smsClient instanceof com.aliyun.dysmsapi20170525.Client) {
                JSONObject templateParam = new JSONObject();
                templateParam.put("code", value);
                SendSmsRequest sendSmsRequest = new SendSmsRequest()
                        .setSignName(SHOT_LINK_PROJECT_NAME)
                        .setTemplateCode(templateCode)
                        .setPhoneNumbers(to)
                        .setTemplateParam(templateParam.toJSONString());
                RuntimeOptions runtimeOptions = new RuntimeOptions();
                ((Client) smsClient).sendSmsWithOptions(sendSmsRequest, runtimeOptions);
            }
            // 【第三方】阿里云市场聚美智数
            else if (smsClient instanceof SmsAliCloudMarketSmsClient) {
                String domain = smsAliCloudMarketProperties.getUrl();
                domain += "?mobile=%s&templateId=%s&value=%s";
                String url = String.format(domain, to, smsAliCloudMarketProperties.getTemplateId(), value);
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "APPCODE " + smsAliCloudMarketProperties.getAppCode());
                HttpEntity<String> entity = new HttpEntity<>(headers);
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
                log.info("url={},body={}", url, response.getBody());
                if(!response.getStatusCode().is2xxSuccessful()){
                    log.error("发送短信验证码失败:{}",response.getBody());
                }
            }
            log.info("发送短信验证码成功");
        } catch (TeaException error) {
            log.error("发送短信验证码失败【阿里SMS远程调用异常】:{}", error.getMessage());
        } catch (Exception sError) {
            log.error("发送短信验证码失败【系统异常】:{}", sError.getMessage());
        }
    }


}
