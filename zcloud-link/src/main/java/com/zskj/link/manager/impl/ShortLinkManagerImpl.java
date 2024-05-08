package com.zskj.link.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zskj.link.manager.ShortLinkManager;
import com.zskj.link.mapper.ShortLinkMapper;
import com.zskj.link.model.ShortLinkDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/30
 * <p>
 *
 * </p>
 */

@Service
public class ShortLinkManagerImpl  implements ShortLinkManager {

    @Autowired
    private ShortLinkMapper shortLinkMapper;

    @Override
    public int addShortLink(ShortLinkDO shortLinkDO) {
        return shortLinkMapper.insert(shortLinkDO);
    }

    @Override
    public ShortLinkDO findByShortLinkCode(String shortLinkCode) {
        LambdaQueryWrapper<ShortLinkDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShortLinkDO::getCode, shortLinkCode).eq(ShortLinkDO::getDel, 0);
        return shortLinkMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    public int logicDelShortLink(String shortLinkCode, Long accountNo) {
        ShortLinkDO shortLinkDO = new ShortLinkDO();
        shortLinkDO.setDel(1);
        return shortLinkMapper.update(shortLinkDO, Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getCode, shortLinkCode)
                .eq(ShortLinkDO::getAccountNo, accountNo));
    }
}
