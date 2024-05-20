package com.zskj.biz;

import com.zskj.account.AccountApplication;
import com.zskj.account.mapper.AccountMapper;
import com.zskj.account.mapper.TrafficMapper;
import com.zskj.account.model.AccountDO;
import com.zskj.account.model.TrafficDO;
import com.zskj.common.util.IDUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/27
 * <p>
 * 分表以及分表后主键问题测试
 * </p>
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountApplication.class)
@Slf4j
public class ShardingSphereTest {

    /**
     * 测试雪花算法
     */
    @Test
    public void snowFlakeTest(){
        Comparable<?> comparable = IDUtil.geneSnowFlakeId();
        System.out.println(comparable.toString());
    }


    @Autowired
    private TrafficMapper trafficMapper;
    /**
     * 测试分表
     */
    @Test
    public void testSaveTraffic(){
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            TrafficDO trafficDO = new TrafficDO();
            trafficDO.setAccountNo((long) random.nextInt(1000));
            Long aLong = Long.valueOf(IDUtil.geneSnowFlakeId().toString());
            System.out.println(aLong);
            trafficDO.setId(aLong);
            trafficMapper.insert(trafficDO);
        }
    }




}
