package com.zskj.shop.controller;

import com.zskj.common.enums.BizCodeEnum;
import com.zskj.common.enums.shop.ClientTypeEnum;
import com.zskj.common.enums.shop.ProductOrderPayTypeEnum;
import com.zskj.common.interceptor.LoginInterceptor;
import com.zskj.common.util.CommonUtil;
import com.zskj.common.util.JsonData;
import com.zskj.shop.controller.request.ConfirmOrderRequest;
import com.zskj.shop.controller.request.ProductOrderPageRequest;
import com.zskj.shop.service.ProductOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.zskj.common.constant.RedisKeyConstant.SUBMIT_ORDER_TOKEN_KEY;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/14
 * <p>
 *
 * </p>
 */
@RestController
@RequestMapping("/api/order/v1")
@Slf4j
public class ProductOrderController {


    @Autowired
    private ProductOrderService productOrderService;


    @Autowired
    private StringRedisTemplate redisTemplate;


    /**
     * 下单前获取令牌用于防重提交
     *
     * @return token
     */
    @GetMapping("/token")
    public JsonData getOrderToken() {
        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        String token = CommonUtil.getStringNumRandom(32);
        // order:submit:账号:令牌
        String key = String.format(SUBMIT_ORDER_TOKEN_KEY, accountNo, token);
        redisTemplate.opsForValue().setIfAbsent(
                key,
                String.valueOf(Thread.currentThread().getId()),
                30,
                TimeUnit.MINUTES);
        return JsonData.buildSuccess(token);
    }

    /**
     * 分页查询订单
     */
    @PostMapping("/page")
    public JsonData page(@RequestBody ProductOrderPageRequest request) {
        Map<String, Object> pageResult = productOrderService.page(request);
        return JsonData.buildSuccess(pageResult);
    }

    /**
     * 查询订单状态
     */
    @GetMapping("/query_state")
    public JsonData queryState(@RequestParam("out_trade_no") String outTradeNo) {
        String state = productOrderService.queryProductOrderState(outTradeNo);
        return StringUtils.isBlank(state) ?
                JsonData.buildResult(BizCodeEnum.ORDER_CONFIRM_NOT_EXIST) : JsonData.buildSuccess(state);
    }


    /**
     * 下单
     */
    @PostMapping("/confirm")
    public void confirm(@RequestBody @Validated ConfirmOrderRequest request,
                                                HttpServletResponse response) {
        JsonData jsonData = productOrderService.confirmOrder(request);

        if (jsonData.getCode() == 0) {
            //端类型
            String client = request.getClientType();
            //支付类型
            String payType = request.getPayType();

            //如果是支付宝支付，跳转网页，sdk除外
            if (payType.equalsIgnoreCase(ProductOrderPayTypeEnum.ALI_PAY.name())) {
                if (client.equalsIgnoreCase(ClientTypeEnum.PC.name())) {
                    CommonUtil.sendHtmlMessage(response, jsonData);
                } else if (client.equalsIgnoreCase(ClientTypeEnum.APP.name())) {
                    // TODO
                } else if (client.equalsIgnoreCase(ClientTypeEnum.H5.name())) {
                    // TODO
                }
            } else if (payType.equalsIgnoreCase(ProductOrderPayTypeEnum.WECHAT_APY.name())) {
                //微信支付
                CommonUtil.sendJsonMessage(response, jsonData);
            }
        } else {
            log.error("创建订单失败：{}", jsonData);
            CommonUtil.sendJsonMessage(response, jsonData);
        }
    }


}
