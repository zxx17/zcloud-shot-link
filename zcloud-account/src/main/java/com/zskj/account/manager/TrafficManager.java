package com.zskj.account.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zskj.account.model.TrafficDO;

import java.util.List;

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
     * 删除过期流量包
     * @return bool
     */
    boolean deleteExpireTraffic();


    /**
     * 给某个流量包增加使用次数
     * @param trafficId 流量包id
     * @param accountNo 账号
     * @param usedTimes 使用次数
     * @return 增加成功与否
     */
    int addDayUsedTimes(Long accountNo, Long trafficId, Integer usedTimes);


    /**
     * 查找可用的短链流量包(未过期),包括免费流量包
     * @param accountNo 账号
     * @return 可用流量包列表
     */
    List<TrafficDO> selectAvailableTraffics(Long accountNo);

    /**
     * 恢复流量包使用当天次数
     * @param accountNo 账号
     * @param trafficId 流量包id
     * @param useTimes 次数
     * @return 恢复数量
     */
    int releaseUsedTimes(Long accountNo, Long trafficId, Integer usedTimes);

    /**
     * 批量更新流量包使用次数为0（用于惰性更新）
     * @param accountNo 账号
     * @param unUpdatedTrafficIds 未更新的流量包id
     * @return 更新数量
     */
    int batchUpdateUsedTimes(Long accountNo, List<Long> unUpdatedTrafficIds);

}
