package com.zskj.account.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zskj.account.manager.TrafficManager;
import com.zskj.account.mapper.TrafficMapper;
import com.zskj.account.model.TrafficDO;
import com.zskj.common.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/22
 * <p>
 *
 * </p>
 */

@Slf4j
@Service
public class TrafficManagerImpl implements TrafficManager {

    @Autowired
    private TrafficMapper trafficMapper;


    @Override
    public int add(TrafficDO trafficDO) {
        return trafficMapper.insert(trafficDO);
    }

    @Override
    public IPage<TrafficDO> pageAvailable(int page, int size, Long accountNo) {
        Page<TrafficDO> pageInfo = new Page<>(page, size);
        String today = TimeUtil.format(new Date(), "yyyy-MM-dd");
        // 注意过期时间要大于当前时间（可用流量包）
        return trafficMapper.selectPage(pageInfo, new QueryWrapper<TrafficDO>()
                .eq("account_no", accountNo).ge("expired_date", today).orderByDesc("gmt_create"));
    }

    @Override
    public TrafficDO findByIdAndAccountNo(Long trafficId, Long accountNo) {
        return trafficMapper.selectOne(new QueryWrapper<TrafficDO>()
                .eq("account_no", accountNo).eq("id", trafficId));
    }

    @Override
    public int addDayUsedTimes(long currentTrafficId, Long accountNo, int dayUsedTimes) {
        // TODO ？？？ day_used = day_used + 1 ？
        return trafficMapper.update(null, new UpdateWrapper<TrafficDO>()
                .eq("account_no", accountNo)
                .eq("id", currentTrafficId)
                .set("day_used", dayUsedTimes));
    }
}
