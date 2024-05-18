package com.zskj.shop.strategy.impl;

import com.alibaba.fastjson.JSONObject;
import com.zskj.common.enums.shop.ProductOrderPayTypeEnum;
import com.zskj.shop.config.wxpay.WechatPayProperties;
import com.zskj.shop.strategy.PayStrategy;
import com.zskj.shop.vo.PayInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


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
     * @param payInfoVO 交易信息
     * @return 请求参数json
     */
    private JSONObject buildParam(PayInfoVO payInfoVO) {
        return null;
    }

    @Override
    public ProductOrderPayTypeEnum getPayType() {
        return ProductOrderPayTypeEnum.WECHAT_PAY;
    }

    @Override
    public String unifiedOrder(PayInfoVO payInfoVO) {
        return null;
    }

    @Override
    public String refundOrder(PayInfoVO payInfoVO) {
        return null;
    }

    @Override
    public String queryPayStatus(PayInfoVO payInfoVO) {
        return null;
    }

    @Override
    public String closeOrder(PayInfoVO payInfoVO) {
        return null;
    }
}
