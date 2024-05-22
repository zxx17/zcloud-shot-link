package com.zskj.account.listener;

import com.rabbitmq.client.Channel;
import com.zskj.account.service.TrafficService;
import com.zskj.common.enums.BizCodeEnum;
import com.zskj.common.exception.BizException;
import com.zskj.common.model.EventMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/22
 * <p>
 *
 * </p>
 */

@Slf4j
@Component
@RabbitListener(queuesToDeclare = {
        @Queue("order.traffic.queue"),
        @Queue("traffic.free_init.queue")
})
public class TrafficMQListener {


    @Autowired
    private TrafficService trafficService;

    @RabbitHandler
    public void trafficHandler(EventMessage eventMessage, Message message, Channel channel) {
        log.info("监听到消息trafficHandler:{}", message);
        try {
            boolean flag = trafficService.handleTrafficMessage(eventMessage);
            if (!flag){
                throw new RuntimeException();
            }
        } catch (Exception e) {
            log.error("消费者失败:{}", eventMessage);
            throw new BizException(BizCodeEnum.MQ_CONSUME_EXCEPTION);
        }

        log.info("消费成功:{}", eventMessage);

    }
}
