package com.zskj.shop.service.impl;

import com.zskj.common.constant.TimeConstant;
import com.zskj.common.enums.BizCodeEnum;
import com.zskj.common.enums.EventMessageType;
import com.zskj.common.enums.shop.BillTypeEnum;
import com.zskj.common.enums.shop.ProductOrderPayTypeEnum;
import com.zskj.common.enums.shop.ProductOrderStateEnum;
import com.zskj.common.exception.BizException;
import com.zskj.common.interceptor.LoginInterceptor;
import com.zskj.common.model.EventMessage;
import com.zskj.common.model.LoginUser;
import com.zskj.common.util.CommonUtil;
import com.zskj.common.util.JsonData;
import com.zskj.common.util.JsonUtil;
import com.zskj.shop.config.rbtmq.RabbitMQConfig;
import com.zskj.shop.controller.request.ConfirmOrderRequest;
import com.zskj.shop.controller.request.ProductOrderPageRequest;
import com.zskj.shop.manager.ProductManager;
import com.zskj.shop.manager.ProductOrderManager;
import com.zskj.shop.model.ProductDO;
import com.zskj.shop.model.ProductOrderDO;
import com.zskj.shop.service.ProductOrderService;
import com.zskj.shop.strategy.PayStrategy;
import com.zskj.shop.strategy.PayStrategyFactory;
import com.zskj.shop.vo.PayInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/14
 * <p>
 *
 * </p>
 */

@Service
@Slf4j
public class ProductOrderServiceImpl implements ProductOrderService {

    @Autowired
    private ProductOrderManager productOrderManager;

    @Autowired
    private ProductManager productManager;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMQConfig rabbitMQConfig;

    @Autowired
    private PayStrategyFactory payStrategyFactory;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Override
    public Map<String, Object> page(ProductOrderPageRequest request) {
        Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        int page = request.getPage() == 0 ? 1 : request.getPage();
        int size = request.getPage() == 0 ? 10 : request.getSize();
        return productOrderManager.page(
                page,
                size,
                accountNo,
                request.getState());
    }

    @Override
    public String queryProductOrderState(String outTradeNo) {
        Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        ProductOrderDO productOrderDO = productOrderManager.findByOutTradeNoAndAccountNo(outTradeNo, accountNo);
        if (productOrderDO == null) {
            return "";
        } else {
            return productOrderDO.getState();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JsonData confirmOrder(ConfirmOrderRequest request) {
        // 获取当前用户
        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        // 生成订单号
        String orderOutTradeNo = CommonUtil.getStringNumRandom(32);
        // 查找商品用于验证价格
        ProductDO productDO = productManager.findDetailById(request.getProductId());
        // 验证价格
        this.checkPrice(productDO, request);
        // 创建保存订单
        this.saveProductOrder(request, loginUser, orderOutTradeNo, productDO);
        // 创建支付对象
        PayInfoVO payInfoVO = PayInfoVO.builder().accountNo(loginUser.getAccountNo())
                .outTradeNo(orderOutTradeNo).clientType(request.getClientType())
                .payType(request.getPayType()).title(productDO.getTitle()).description("")
                .payFee(request.getPayAmount()).orderPayTimeoutMills(TimeConstant.ORDER_PAY_TIMEOUT_MILLS)
                .build();
        // 发送延迟消息 用于超时自动关单
        EventMessage eventMessage = EventMessage.builder()
                .eventMessageType(EventMessageType.PRODUCT_ORDER_NEW.name())
                .messageId(CommonUtil.getStringNumRandom(32))
                .accountNo(loginUser.getAccountNo())
                .bizId(orderOutTradeNo)
                .build();
        rabbitTemplate.convertAndSend(
                rabbitMQConfig.getOrderEventExchange(),
                rabbitMQConfig.getOrderCloseDelayRoutingKey(),
                eventMessage
        );
        //调用支付信息 第三方支付
        PayStrategy payStorage = payStrategyFactory.getPayStorage(ProductOrderPayTypeEnum.valueOf(request.getPayType()));
        String codeUrl = payStorage.unifiedOrder(payInfoVO);
        if (StringUtils.isNotBlank(codeUrl)) {
            Map<String, String> resultMap = new HashMap<>(2);
            resultMap.put("code_url", codeUrl);
            resultMap.put("out_trade_no", payInfoVO.getOutTradeNo());
            return JsonData.buildSuccess(resultMap);
        }
        return JsonData.buildResult(BizCodeEnum.PAY_ORDER_FAIL);
    }

    /**
     * 处理订单
     * @param eventMessage event
     * @return bool
     */
    @Override
    public boolean handleProductOrderMessage(EventMessage eventMessage) {
        // 获取消息类型（关闭订单 | 支付回调更新订单状态）
        String messageType = eventMessage.getEventMessageType();
        try {
            if (messageType.equalsIgnoreCase(EventMessageType.PRODUCT_ORDER_NEW.name())){
                //关闭订单
                return this.closeProductOrder(eventMessage);
            }else if(EventMessageType.PRODUCT_ORDER_PAY.name().equalsIgnoreCase(messageType)){
                //订单已经支付，更新订单状态
                String outTradeNo = eventMessage.getBizId();
                Long accountNo = eventMessage.getAccountNo();
                int rows = productOrderManager.updateOrderPayState(
                        outTradeNo,
                        accountNo,
                        ProductOrderStateEnum.PAY.name(),
                        ProductOrderStateEnum.NEW.name()
                );
                log.info("订单更新成功:rows={},eventMessage={}",rows,eventMessage);
                return true;
            }
            // TODO 退款等其他业务
            return false;
        }catch (Exception e){
            log.error("处理订单业务失败:{}",eventMessage);
            throw new BizException(BizCodeEnum.MQ_CONSUME_EXCEPTION);
        }
    }

    /**
     * 关闭订单
     * @param eventMessage 消息体
     * @return bool
     */
    private boolean closeProductOrder(EventMessage eventMessage){
        // 获取订单号
        String outTradeNo = eventMessage.getBizId();
        // 获取用户账号
        Long accountNo = eventMessage.getAccountNo();
        // 查询数据库是否已经存在订单
        ProductOrderDO productOrderDO = productOrderManager.findByOutTradeNoAndAccountNo(outTradeNo, accountNo);
        //订单不存在
        if (productOrderDO == null) {
            log.warn("订单不存在");
            return true;
        }
        // 存在且支付
        if (productOrderDO.getState().equalsIgnoreCase(ProductOrderStateEnum.PAY.name())) {
            //已经支付
            log.info("直接确认消息，订单已经支付:{}", eventMessage);
            return true;
        }
        // 存在未支付，需要向第三方支付平台查询状态
        if (productOrderDO.getState().equalsIgnoreCase(ProductOrderStateEnum.NEW.name())) {
            //向第三方查询状态
            PayInfoVO payInfoVO = new PayInfoVO();
            payInfoVO.setPayType(productOrderDO.getPayType());
            payInfoVO.setOutTradeNo(outTradeNo);
            payInfoVO.setAccountNo(accountNo);

            PayStrategy payStorage = payStrategyFactory.getPayStorage(ProductOrderPayTypeEnum.valueOf(productOrderDO.getPayType()));
            String payResult = payStorage.queryPayStatus(payInfoVO);
            log.info("第三方支付平台查询状态:{}", payResult);
            if (StringUtils.isBlank(payResult)) {
                //如果为空，则未支付成功，本地取消订单
                productOrderManager.updateOrderPayState(outTradeNo,
                        accountNo,
                        ProductOrderStateEnum.CANCEL.name(),
                        ProductOrderStateEnum.NEW.name());
                log.info("未支付成功，本地取消订单:{}", eventMessage);
            } else {
                //支付成功，主动把订单状态更新成支付
                log.warn("支付成功，但是支付回调通知失败，需要排查问题:{}", eventMessage);
                productOrderManager.updateOrderPayState(outTradeNo,
                        accountNo,
                        ProductOrderStateEnum.PAY.name(),
                        ProductOrderStateEnum.NEW.name());
                //触发支付成功后的逻辑， TODO（进行权益补偿）
            }
        }
        return true;
    }


    /**
     * 支付回调处理
     *
     * @param payType   支付类型
     * @param paramsMap 支付回调参数
     * @return jsonData
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public JsonData processOrderCallbackMsg(ProductOrderPayTypeEnum payType, Map<String, String> paramsMap) {
        // 获取订单号
        String outTradeNo = paramsMap.get("out_trade_no");
        //获取交易状态
        String tradeState = paramsMap.get("trade_state");
        // 获取账号
        Long accountNo = Long.valueOf(paramsMap.get("account_no"));
        // 根据账号和订单号查询订单信息
        ProductOrderDO productOrderDO = productOrderManager.findByOutTradeNoAndAccountNo(outTradeNo, accountNo);
        // 构建map封装消息体
        Map<String, Object> content = new HashMap<>(4);
        content.put("outTradeNo", outTradeNo);
        content.put("buyNum", productOrderDO.getBuyNum());
        content.put("accountNo", accountNo);
        content.put("product", productOrderDO.getProductSnapshot());
        // 构建mq消息
        EventMessage eventMessage = EventMessage.builder()
                .bizId(outTradeNo)
                .accountNo(accountNo)
                .messageId(outTradeNo)
                .content(JsonUtil.obj2Json(content))
                .eventMessageType(EventMessageType.PRODUCT_ORDER_PAY.name())
                .build();
        // 支付类型分支，发送消息
        if (payType.name().equalsIgnoreCase(ProductOrderPayTypeEnum.WECHAT_PAY.name())) {
            // 微信支付
            if("SUCCESS".equals(tradeState)){
                // 防止微信重复回调导致的消息重复发送
                Boolean flag = redisTemplate.opsForValue().setIfAbsent(outTradeNo, Thread.currentThread().getName().toString(), 10, TimeUnit.MINUTES);
                if (Boolean.TRUE.equals(flag)) {
                    // 更新订单状态队列、发放流量包队列
                    rabbitTemplate.convertAndSend(rabbitMQConfig.getOrderEventExchange(),
                            rabbitMQConfig.getOrderUpdateTrafficRoutingKey(), eventMessage);
                    return JsonData.buildSuccess();
                }
            }
        }else {
            // TODO 后续做支付类型扩展
        }
        return JsonData.buildResult(BizCodeEnum.PAY_ORDER_CALLBACK_NOT_SUCCESS);
    }

    /**
     * 创建订单
     *
     * @param orderRequest    orderInfo
     * @param loginUser       loginUser
     * @param orderOutTradeNo 订单号
     * @param productDO       商品信息
     * @return 订单do
     */
    private ProductOrderDO saveProductOrder(ConfirmOrderRequest orderRequest, LoginUser loginUser, String orderOutTradeNo, ProductDO productDO) {
        ProductOrderDO productOrderDO = new ProductOrderDO();

        //设置用户信息
        productOrderDO.setAccountNo(loginUser.getAccountNo());
        productOrderDO.setNickname(loginUser.getUsername());


        //设置商品信息
        productOrderDO.setProductId(productDO.getId());
        productOrderDO.setProductTitle(productDO.getTitle());
        productOrderDO.setProductSnapshot(JsonUtil.obj2Json(productDO));
        productOrderDO.setProductAmount(productDO.getAmount());

        //设置订单信息
        productOrderDO.setBuyNum(orderRequest.getBuyNum());
        productOrderDO.setOutTradeNo(orderOutTradeNo);
        productOrderDO.setCreateTime(new Date());
        productOrderDO.setDel(0);

        //发票信息
        productOrderDO.setBillType(BillTypeEnum.valueOf(orderRequest.getBillType()).name());
        productOrderDO.setBillHeader(orderRequest.getBillHeader());
        productOrderDO.setBillReceiverPhone(orderRequest.getBillReceiverPhone());
        productOrderDO.setBillReceiverEmail(orderRequest.getBillReceiverEmail());
        productOrderDO.setBillContent(orderRequest.getBillContent());


        //实际支付总价
        productOrderDO.setPayAmount(orderRequest.getPayAmount());
        //总价，没使用优惠券
        productOrderDO.setTotalAmount(orderRequest.getTotalAmount());
        //订单状态
        productOrderDO.setState(ProductOrderStateEnum.NEW.name());
        //支付类型
        productOrderDO.setPayType(ProductOrderPayTypeEnum.valueOf(orderRequest.getPayType()).name());

        //插入数据库
        productOrderManager.add(productOrderDO);

        return productOrderDO;
    }


    /**
     * 验算价格
     *
     * @param productDO    商品信息
     * @param orderRequest 新增订单请求
     */
    private void checkPrice(ProductDO productDO, ConfirmOrderRequest orderRequest) {
        //后端计算价格
        BigDecimal bizTotal = BigDecimal.valueOf(orderRequest.getBuyNum()).multiply(productDO.getAmount());
        //前端传递总价和后端计算总价格是否一致, 如果有优惠券，也在这里进行计算
        if (bizTotal.compareTo(orderRequest.getPayAmount()) != 0) {
            log.error("验证价格失败{}", orderRequest);
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_PRICE_FAIL);
        }
    }


}
