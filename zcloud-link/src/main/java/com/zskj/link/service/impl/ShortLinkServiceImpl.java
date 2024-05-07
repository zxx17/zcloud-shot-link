package com.zskj.link.service.impl;

import com.zskj.common.enums.link.EventMessageType;
import com.zskj.common.interceptor.LoginInterceptor;
import com.zskj.common.model.EventMessage;
import com.zskj.common.util.IDUtil;
import com.zskj.common.util.JsonData;
import com.zskj.common.util.JsonUtil;
import com.zskj.link.config.rbtmq.RabbitMQConfig;
import com.zskj.link.controller.request.ShortLinkAddRequest;
import com.zskj.link.manager.ShortLinkManager;
import com.zskj.link.mapper.ShortLinkMapper;
import com.zskj.link.model.ShortLinkDO;
import com.zskj.link.service.ShortLinkService;
import com.zskj.link.vo.ShortLinkVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/2
 * <p>
 *
 * </p>
 */
@SuppressWarnings("ALL")
@Slf4j
@Service
public class ShortLinkServiceImpl implements ShortLinkService {


    @Autowired
    private ShortLinkManager shortLinkManager;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMQConfig rabbitMQConfig;

    @Override
    public ShortLinkVO parseShortLinkCode(String shortLinkCode) {
        ShortLinkDO shortLinkDO = shortLinkManager.findByShortLinkCode(shortLinkCode);
        if (shortLinkDO == null) {
            return null;
        }
        ShortLinkVO shortLinkVO = new ShortLinkVO();
        BeanUtils.copyProperties(shortLinkDO, shortLinkVO);
        return shortLinkVO;
    }

    @Override
    public JsonData createShortLink(ShortLinkAddRequest request) {
        Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        // 构建mq消息
        EventMessage eventMessage = EventMessage.builder().accountNo(accountNo)
                .content(JsonUtil.obj2Json(request))
                .messageId(IDUtil.geneSnowFlakeId().toString())
                .eventMessageType(EventMessageType.SHORT_LINK_ADD.name())
                .build();
        // 发送mq消息
        rabbitTemplate.convertAndSend(rabbitMQConfig.getShortLinkEventExchange(),
                rabbitMQConfig.getShortLinkAddRoutingKey(),
                eventMessage);

        return JsonData.buildSuccess();
    }
}
