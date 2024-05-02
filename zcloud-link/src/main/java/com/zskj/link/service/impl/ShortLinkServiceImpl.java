package com.zskj.link.service.impl;

import com.zskj.link.manager.ShortLinkManager;
import com.zskj.link.mapper.ShortLinkMapper;
import com.zskj.link.model.ShortLinkDO;
import com.zskj.link.service.ShortLinkService;
import com.zskj.link.vo.ShortLinkVO;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
public class ShortLinkServiceImpl implements ShortLinkService {


    @Autowired
    private ShortLinkManager shortLinkManager;

    @Override
    public ShortLinkVO parseShortLinkCode(String shortLinkCode) {
        ShortLinkDO shortLinkDO = shortLinkManager.findByShortLinkCode(shortLinkCode);
        if(shortLinkDO == null){
            return null;
        }
        ShortLinkVO shortLinkVO = new ShortLinkVO();
        BeanUtils.copyProperties(shortLinkDO,shortLinkVO);
        return shortLinkVO;
    }
}
