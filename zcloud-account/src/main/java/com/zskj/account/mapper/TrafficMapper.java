package com.zskj.account.mapper;

import com.zskj.account.model.TrafficDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author Xinxuan Zhuo
 * @since 2024-04-22
 */
public interface TrafficMapper extends BaseMapper<TrafficDO> {

    int addDayUsedTimes(@Param("accountNo") Long accountNo,
                        @Param("trafficId") Long trafficId,
                        @Param("usedTimes") Integer usedTimes);

    int releaseUsedTimes(@Param("accountNo") Long accountNo,
                         @Param("trafficId") Long trafficId,
                         @Param("usedTimes") Integer usedTimes);
}
