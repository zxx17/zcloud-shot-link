package com.zskj.shop.listener;

import com.rabbitmq.client.Channel;
import com.zskj.common.enums.BizCodeEnum;
import com.zskj.common.exception.BizException;
import com.zskj.common.model.EventMessage;
import com.zskj.shop.service.ProductOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/17
 * <p>
 *
 * </p>
 */

@Slf4j
@Component
@RabbitListener(queuesToDeclare = {@Queue("order.close.queue")})
public class ProductOrderMQListener {

    @Autowired
    private ProductOrderService productOrderService;


    @RabbitHandler
    public void productOrderHandler(EventMessage eventMessage, Message message, Channel channel){
        log.info("监听到消息ProductOrderMQListener message消息内容:{}",message);

        try{
            //关闭订单
            boolean flag = productOrderService.closeProductOrder(eventMessage);
            if (!flag){
                throw new RuntimeException();
            }
        }catch (Exception e){
            log.error("消费者失败:{}",eventMessage);
            throw new BizException(BizCodeEnum.MQ_CONSUME_EXCEPTION);
        }
        log.info("消费成功:{}",eventMessage);
    }

}
