package com.zskj.shop.strategy.impl;

import com.alibaba.fastjson.JSONObject;
import com.zskj.common.enums.BizCodeEnum;
import com.zskj.common.enums.shop.ProductOrderPayTypeEnum;
import com.zskj.common.exception.BizException;
import com.zskj.shop.config.wxpay.WechatPayApi;
import com.zskj.shop.config.wxpay.WechatPayProperties;
import com.zskj.shop.strategy.PayStrategy;
import com.zskj.shop.vo.PayInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;


@Slf4j
@Service
public class WechatPayStrategy implements PayStrategy {

    @Autowired
    @Qualifier("wechatPayClient")
    private CloseableHttpClient wechatPayClient;

    @Autowired
    private WechatPayProperties wechatPayProperties;

    /**
     * 构建请求参数
     *
     * @param payInfoVO 交易信息
     * @return 请求参数
     */
    private String buildPayParam(PayInfoVO payInfoVO) {
        // 交易金额对象
        JSONObject amountObj = new JSONObject();
        int amount = payInfoVO.getPayFee().multiply(BigDecimal.valueOf(100)).intValue();
        amountObj.put("total", amount);
        amountObj.put("currency", "CNY");
        // 交易信息对象
        JSONObject payObj = new JSONObject();
        payObj.put("mchid", wechatPayProperties.getMchId());
        payObj.put("out_trade_no", payInfoVO.getOutTradeNo());
        payObj.put("appid", wechatPayProperties.getAppid());
        payObj.put("description", payInfoVO.getTitle());
        payObj.put("notify_url", wechatPayProperties.getCallbackUrl());
        //过期时间  RFC 3339格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        //支付订单过期时间（当前时间加上订单过期时间）
        String timeExpire = sdf.format(new Date(System.currentTimeMillis() + payInfoVO.getOrderPayTimeoutMills()));
        payObj.put("time_expire", timeExpire);
        payObj.put("amount", amountObj);
        //回调携带
        payObj.put("attach", "{\"accountNo\":" + payInfoVO.getAccountNo() + "}");
        // 处理请求body参数
        String body = payObj.toJSONString();
        log.debug("微信支付请求参数：{}", body);
        return body;
    }

    @Override
    public ProductOrderPayTypeEnum getPayType() {
        return ProductOrderPayTypeEnum.WECHAT_PAY;
    }


    @Override
    public String unifiedOrder(PayInfoVO payInfoVO) {
        // 构建请求参数
        String body = this.buildPayParam(payInfoVO);
        StringEntity entity = new StringEntity(body, "utf-8");
        entity.setContentType("application/json");
        // 发起请求
        HttpPost httpPost = new HttpPost(WechatPayApi.NATIVE_ORDER);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setEntity(entity);
        String result = "";
        try (CloseableHttpResponse response = wechatPayClient.execute(httpPost)) {
            //响应码
            int statusCode = response.getStatusLine().getStatusCode();
            //响应体
            String responseStr = EntityUtils.toString(response.getEntity());
            log.debug("下单响应码:{},响应体:{}", statusCode, responseStr);
            if (statusCode == HttpStatus.OK.value()) {
                JSONObject jsonObject = JSONObject.parseObject(responseStr);
                if (jsonObject.containsKey("code_url")) {
                    result = jsonObject.getString("code_url");
                }
            } else {
                log.error("下单响应失败:{},响应体:{}", statusCode, responseStr);
            }
            return result;
        } catch (Exception e) {
            log.error("微信支付响应异常:{}", e.getMessage());
            throw new BizException(BizCodeEnum.PAY_ORDER_FAIL);
        }
    }


    @Override
    public String refundOrder(PayInfoVO payInfoVO) {
        return null;
    }

    @Override
    public String queryPayStatus(PayInfoVO payInfoVO) {
        String outTradeNo = payInfoVO.getOutTradeNo();
        String url = String.format(WechatPayApi.NATIVE_QUERY, outTradeNo, wechatPayProperties.getMchId());
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "application/json");
        String result = "";
        try(CloseableHttpResponse response = wechatPayClient.execute(httpGet)){
            //响应码
            int statusCode = response.getStatusLine().getStatusCode();
            //响应体
            String responseStr = EntityUtils.toString(response.getEntity());
            log.debug("查询支付状态响应码:{},响应体:{}",statusCode,responseStr);
            if(statusCode == HttpStatus.OK.value()){
                JSONObject jsonObject = JSONObject.parseObject(responseStr);
                if(jsonObject.containsKey("trade_state")){
                    result = jsonObject.getString("trade_state");
                }
            }else {
                log.error("查询支付状态响应失败:{},响应体:{}",statusCode,responseStr);
            }
        }catch (Exception e){
            log.error("微信支付响应异常:{}",e.getMessage());
            throw new BizException(BizCodeEnum.PAY_ORDER_STATE_ERROR);

        }
        return result;
    }

    @Override
    public String closeOrder(PayInfoVO payInfoVO) {
        return null;
    }
}
