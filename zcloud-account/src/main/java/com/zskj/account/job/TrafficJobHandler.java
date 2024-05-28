package com.zskj.account.job;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.zskj.account.service.TrafficService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/28
 * <p>
 *
 * </p>
 */

@Component
@Slf4j
public class TrafficJobHandler {

    @Autowired
    private TrafficService trafficService;


    /**
     * 过期流量包处理
     * @return bool
     * TODO 考虑定时任务是否需要开启线程池 | 执行失败情况
     */
    @XxlJob(value = "trafficExpiredHandler",init = "init",destroy = "destroy")
    public ReturnT<String> execute(){
        log.info("execute 任务方法触发成功,删除过期流量包");
        trafficService.deleteExpireTraffic();
        return ReturnT.SUCCESS;
    }

    private void init(){
        log.info("execute init >>>>>");
    }

    private void destroy(){
        log.info("execute destroy >>>>>");
    }

}
