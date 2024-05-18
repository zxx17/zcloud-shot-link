package com.zskj.shop.config.wxpay;


import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.ScheduledUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.stream.Collectors;

/**
 * @author Zhuo
 */
@Configuration
@EnableConfigurationProperties(WechatPayProperties.class)
public class WechatPayConfig {

    /**
     * 加载并返回支付私钥。
     * 该方法从指定路径加载私钥文件，解析私钥内容，并通过RSA算法生成私钥对象。
     *
     * @param payConfig 微信支付配置属性，包含私钥路径等配置信息。
     * @return 加载成功的私钥对象。
     * @throws IOException      如果读取私钥文件发生错误。
     * @throws RuntimeException 如果遇到RSA算法不支持或私钥格式无效的情况。
     */
    public PrivateKey getPrivateKey(WechatPayProperties payConfig) throws IOException {
        // 从classpath加载私钥文件内容
        InputStream inputStream = new ClassPathResource(payConfig.getPrivateKeyPath()
                .replace("classpath:", "")).getInputStream();
        String content = new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining(System.lineSeparator()));

        try {
            // 移除私钥字符串中的头部和尾部标识，并移除所有空格
            String privateKey = content.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            // 实例化RSA算法的KeyFactory
            KeyFactory kf = KeyFactory.getInstance("RSA");

            // 通过私钥字符串生成PrivateKey对象
            return kf.generatePrivate(
                    new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey)));
        } catch (NoSuchAlgorithmException e) {
            // 如果不支持RSA算法，则抛出运行时异常
            throw new RuntimeException("当前Java环境不支持RSA", e);
        } catch (InvalidKeySpecException e) {
            // 如果私钥格式无效，则抛出运行时异常
            throw new RuntimeException("无效的密钥格式");
        }
    }

    /**
     * 定时获取微信签名验证器，自动获取微信平台证书（证书里面包括微信平台公钥）
     */
    @Bean
    public ScheduledUpdateCertificatesVerifier getCertificatesVerifier(WechatPayProperties payConfig) throws IOException {

        // 使用定时更新的签名验证器，不需要传入证书
        ScheduledUpdateCertificatesVerifier verifier = null;
        verifier = new ScheduledUpdateCertificatesVerifier(
                new WechatPay2Credentials(payConfig.getMchId(),
                        new PrivateKeySigner(payConfig.getMchSerialNo(),
                                this.getPrivateKey(payConfig))),
                payConfig.getApiv3Key().getBytes(StandardCharsets.UTF_8)
        );
        return verifier;
    }

    /**
     * 获取http请求对象，会自动的处理签名和验签，
     * 并进行证书自动更新
     */
    @Bean("wechatPayClient")
    public CloseableHttpClient getWechatPayClient(ScheduledUpdateCertificatesVerifier verifier, WechatPayProperties payConfig) throws IOException {
        WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
                .withMerchant(payConfig.getMchId(), payConfig.getMchSerialNo(), this.getPrivateKey(payConfig))
                .withValidator(new WechatPay2Validator(verifier));

        // 通过WechatPayHttpClientBuilder构造的HttpClient，会自动的处理签名和验签，并进行证书自动更新
        return builder.build();
    }


}
