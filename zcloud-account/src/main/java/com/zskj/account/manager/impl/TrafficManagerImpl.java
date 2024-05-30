package com.zskj.account.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import java.util.List;

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
    public boolean deleteExpireTraffic() {
        int rows = trafficMapper.delete(new QueryWrapper<TrafficDO>().le("expired_date",new Date()));
        log.info("删除过期流量包行数：rows={}",rows);
        return true;
    }

    @Override
    public int addDayUsedTimes(Long accountNo, Long trafficId, Integer usedTimes) {
        return trafficMapper.addDayUsedTimes(accountNo,trafficId,usedTimes);
    }

    @Override
    public List<TrafficDO> selectAvailableTraffics(Long accountNo) {
        // select * from traffic where account_no = #{accountNo} and expired_date >= #{today} or out_trade_no = 'free_init'
        List<TrafficDO> trafficDOList = trafficMapper.selectList(new LambdaQueryWrapper<TrafficDO>()
                .eq(TrafficDO::getAccountNo, accountNo)
                .ge(TrafficDO::getExpiredDate, TimeUtil.format(new Date(), "yyyy-MM-dd"))
                .or()
                .eq(TrafficDO::getOutTradeNo, "free_init")
        );
        log.info("查询可用流量包：{}", trafficDOList);
        return trafficDOList;
    }

    @Override
    public int releaseUsedTimes(Long accountNo, Long trafficId, Integer usedTimes) {
        return trafficMapper.releaseUsedTimes(accountNo,trafficId,usedTimes);
    }

    @Override
    public int batchUpdateUsedTimes(Long accountNo, List<Long> unUpdatedTrafficIds) {
        int rows = trafficMapper.update(null, new UpdateWrapper<TrafficDO>()
                .eq("account_no", accountNo)
                .in("id", unUpdatedTrafficIds)
                .set("day_used", 0));
        log.info("批量更新可用流量包行数（使用次数置为0）：rows={}",rows);
        return rows;
    }

}
