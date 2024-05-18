package com.zskj.shop.biz;


import com.zskj.common.enums.shop.ProductOrderPayTypeEnum;
import com.zskj.shop.ShopApplication;
import com.zskj.shop.strategy.PayStrategyFactory;
import com.zskj.shop.strategy.impl.WechatPayStrategy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ShopApplication.class)
public class TestPayFactory {

    @Autowired
    private PayStrategyFactory payStrategyFactory;

    @Test
    public void testPayFactory() {
        if (payStrategyFactory.getPayStorage(ProductOrderPayTypeEnum.WECHAT_PAY) instanceof WechatPayStrategy){
            System.out.println("微信支付");
        }
    }

}
