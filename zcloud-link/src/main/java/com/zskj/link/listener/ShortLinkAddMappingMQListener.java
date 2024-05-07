package com.zskj.link.listener;

import com.rabbitmq.client.Channel;
import com.zskj.common.enums.BizCodeEnum;
import com.zskj.common.exception.BizException;
import com.zskj.common.model.EventMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/7
 * <p>
 * 新增短链mapping消息监听器
 * queuesToDeclare 使用queuesToDeclare在生产者没有启动创建队列的时候，消费者会自动创建队列
 * </p>
 */

@SuppressWarnings("ALL")
@Slf4j
@Component
@RabbitListener(queuesToDeclare = { @Queue("short_link.add.mapping.queue") })
public class ShortLinkAddMappingMQListener {

    @RabbitHandler
    public void shortLinkHandler(EventMessage eventMessage, Message message, Channel channel) throws IOException {
        log.info("监听到消息ShortLinkAddMappingMQListener message消息内容:{}",message);
        try{
            // TODO 处理业务逻辑

        }catch (Exception e){
            //处理业务异常，还有进行其他操作，比如记录失败原因
            log.error("消费失败:{}",eventMessage);
            throw new BizException(BizCodeEnum.MQ_CONSUME_EXCEPTION);
        }
        log.info("消费成功:{}",eventMessage);
        //确认消息消费成功
        //channel.basicAck(tag,false);

    }
}
