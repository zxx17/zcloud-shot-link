package com.zskj.account.component;

import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.tea.TeaException;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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


    @Autowired
    private com.aliyun.dysmsapi20170525.Client smsClient;

    private static final String ACCOUNT_DEFAULT_TEMPLATE_CODE = "SMS_465680780";

    public void send(String to, String value) {
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setSignName(SHOT_LINK_PROJECT_NAME)
                .setTemplateCode(ACCOUNT_DEFAULT_TEMPLATE_CODE)
                .setPhoneNumbers(to)
                .setTemplateParam("{\"code\":\"" + value + "\"}");
        RuntimeOptions runtimeOptions = new RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            smsClient.sendSmsWithOptions(sendSmsRequest, runtimeOptions);
        } catch (TeaException error) {
            log.error("发送短信验证码失败【远程调用异常】:{}",error.getMessage());
        } catch (Exception _error) {
            log.error("发送短信验证码失败【系统异常】:{}", _error.getMessage());
        }
        log.info("发送短信验证码成功");
    }


}
