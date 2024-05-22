package com.zskj.shop.controller;

import com.alibaba.fastjson.JSONObject;
import com.wechat.pay.contrib.apache.httpclient.auth.ScheduledUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import com.zskj.common.enums.shop.ProductOrderPayTypeEnum;
import com.zskj.shop.config.wxpay.WechatPayProperties;
import com.zskj.shop.service.ProductOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Zhuo
 */
@Slf4j
@Controller
@RequestMapping("/api/callback/order/v1/")
public class PayCallBackController {

    @Autowired
    private WechatPayProperties wechatPayProperties;

    @Autowired
    private ScheduledUpdateCertificatesVerifier verifier;

    @Autowired
    private ProductOrderService productOrderService;


    /**
     * * 获取报文
     * <p>
     * * 验证签名（确保是微信传输过来的）
     * <p>
     * * 解密（AES对称解密出原始数据）
     * <p>
     * * 处理业务逻辑
     * <p>
     * * 响应请求
     *
     * @param request  req
     * @param response resp
     * @return map  ==> wechat
     */
    @RequestMapping("/wechat")
    @ResponseBody
    public Map<String, String> wechatPayCallback(HttpServletRequest request, HttpServletResponse response) {
        //获取报文
        String body = this.getRequestBody(request);
        // 随机串
        String nonceStr = request.getHeader("Wechatpay-Nonce");
        // 微信传递过来的签名
        String signature = request.getHeader("Wechatpay-Signature");
        // 证书序列号（微信平台）
        String serialNo = request.getHeader("Wechatpay-Serial");
        // 时间戳
        String timestamp = request.getHeader("Wechatpay-Timestamp");

        // 构造签名串 应答时间戳\n应答随机串\n应答报文主体\n
        String signStr = Stream.of(timestamp, nonceStr, body).collect(Collectors.joining("\n", "", "\n"));
        Map<String, String> respMap = new HashMap<>(2);
        try {
            //验证签名是否通过
            boolean result = this.verifiedSign(serialNo, signStr, signature);
            if (result) {
                //解密数据
                String plainBody = this.decryptBody(body);
                log.info("微信支付回调解密后的明文:{}", plainBody);

                Map<String, String> paramsMap = this.convertWechatPayMsgToMap(plainBody);
                //处理业务逻辑（更新订单状态，发放流量包）
                productOrderService.processOrderCallbackMsg(ProductOrderPayTypeEnum.WECHAT_PAY, paramsMap);

                //响应微信
                respMap.put("code", "SUCCESS");
                respMap.put("message", "成功");
            }else {
                respMap.put("code", "FAIL");
                respMap.put("message", "失败");
            }
        } catch (Exception e) {
            log.error("微信支付回调异常:{}", e.getMessage());
        }
        return respMap;
    }

    /**
     * 将微信支付回调报文转换为map
     */
    private Map<String, String> convertWechatPayMsgToMap(String plainBody) {
        Map<String, String> paramsMap = new HashMap<>(2);
        JSONObject jsonObject = JSONObject.parseObject(plainBody);
        //商户订单号
        paramsMap.put("out_trade_no", jsonObject.getString("out_trade_no"));
        //交易状态
        paramsMap.put("trade_state", jsonObject.getString("trade_state"));
        //附加数据
        paramsMap.put("account_no", jsonObject.getJSONObject("attach").getString("accountNo"));
        return paramsMap;
    }

    /**
     * 解密
     */
    private String decryptBody(String body) throws GeneralSecurityException {
        // apiv3密钥用来解密
        AesUtil aesUtil = new AesUtil(wechatPayProperties.getApiv3Key().getBytes(StandardCharsets.UTF_8));

        // 获取密文
        JSONObject object = JSONObject.parseObject(body);
        JSONObject resource = object.getJSONObject("resource");
        String ciphertext = resource.getString("ciphertext");
        String associatedData = resource.getString("associated_data");
        String nonce = resource.getString("nonce");

        return aesUtil.decryptToString(associatedData.getBytes(StandardCharsets.UTF_8),nonce.getBytes(StandardCharsets.UTF_8),ciphertext);
    }

    /**
     * 验证签名
     *
     * @param serialNo  微信平台-证书序列号
     * @param signStr   自己组装的签名串
     * @param signature 微信返回的签名
     */
    private boolean verifiedSign(String serialNo, String signStr, String signature) {
        return verifier.verify(serialNo, signStr.getBytes(StandardCharsets.UTF_8), signature);
    }


    /**
     * 读取请求数据流
     */
    private String getRequestBody(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        try (ServletInputStream inputStream = request.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            log.error("读取数据流异常:{}", e.getMessage());
        }
        return sb.toString();
    }

}
