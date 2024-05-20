package com.zskj.biz;

import com.zskj.common.util.CommonUtil;
import com.zskj.shop.ShopApplication;
import com.zskj.shop.manager.ProductOrderManager;
import com.zskj.shop.model.ProductOrderDO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/14
 * <p>
 *
 * </p>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ShopApplication.class)

@Slf4j
public class ProductOrderTest {

    @Autowired
    private ProductOrderManager productOrderManager;


    @Test
    public void testAdd(){
        for(long i=0L;i<5; i++){
            ProductOrderDO productOrderDO = ProductOrderDO.builder()
                    .outTradeNo(CommonUtil.generateUUID())
                    .payAmount(new BigDecimal(11))
                    .state("NEW")
                    .nickname("nickname i"+i)
                    .accountNo(991488246552723456L)
                    .del(0)
                    .productId(2L)
                    .build();

            productOrderManager.add(productOrderDO);
        }

    }



    @Test
    public void testPage(){
        Map<String, Object> page = productOrderManager.page(1, 2, 101L, null);
        log.info(page.toString());
    }

}

