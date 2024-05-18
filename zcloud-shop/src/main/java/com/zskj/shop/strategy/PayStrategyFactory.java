package com.zskj.shop.strategy;


import com.zskj.common.enums.shop.ProductOrderPayTypeEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Zhuo
 */
@Component
public class PayStrategyFactory implements InitializingBean {

    @Autowired
    private List<PayStrategy> payStrategyList;

    private final Map<ProductOrderPayTypeEnum, PayStrategy> payStorageMap = new HashMap<>();


    public PayStrategy getPayStorage(ProductOrderPayTypeEnum payType) {
        return payStorageMap.get(payType);
    }

    @Override
    public void afterPropertiesSet() {
        payStrategyList.forEach(
                payStorage -> payStorageMap.put(payStorage.getPayType(), payStorage)
        );
    }


}
