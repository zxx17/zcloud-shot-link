package com.zskj.account.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zskj.account.model.TrafficDO;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/22
 * <p>
 *
 * </p>
 */

public interface TrafficManager {

    /**
     * 新增流量包
     * @param trafficDO 流量包实体
     * @return 添加成功与否
     */
    int add(TrafficDO trafficDO);


    /**
     * 分页查询可用的流量包
     * @param page 页数
     * @param size 页大小
     * @param accountNo 当前账号
     * @return 当前可用流量包列表
     */
    IPage<TrafficDO> pageAvailable(int page, int size, Long accountNo);


    /**
     * 查找详情
     * @param trafficId 流量包id
     * @param accountNo 账号
     * @return 流量包实体
     */
    TrafficDO findByIdAndAccountNo(Long trafficId,Long accountNo);


    /**
     * 增加某个流量包天使用次数
     * @param currentTrafficId 当前流量包id
     * @param accountNo 账号
     * @param dayUsedTimes 增加的每天使用次数
     * @return 增加成功与否
     */
    int addDayUsedTimes(long currentTrafficId, Long accountNo, int dayUsedTimes);


    /**
     * 删除过期流量包
     * @return bool
     */
    boolean deleteExpireTraffic();
}
