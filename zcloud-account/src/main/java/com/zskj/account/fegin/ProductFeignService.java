package com.zskj.account.fegin;

import com.zskj.common.util.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/22
 * <p>
 *
 * </p>
 */

@FeignClient(name = "zcloud-shop-service")
public interface ProductFeignService {

    /**
     * 获取流量包商品详情
     * @param productId 商品id
     * @return 商品信息
     */
    @GetMapping("/api/product/v1/detail/{product_id}")
    JsonData detail(@PathVariable("product_id") long productId);

}
