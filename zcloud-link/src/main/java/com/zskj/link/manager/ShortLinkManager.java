package com.zskj.link.manager;

import com.zskj.link.model.ShortLinkDO;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/30
 * <p>
 *
 * </p>
 */

public interface ShortLinkManager {


    /**
     * 新增
     * @param shortLinkDO do
     * @return int
     */
    int addShortLink(ShortLinkDO shortLinkDO);


    /**
     * 根据短链码找短链
     * @param shortLinkCode code
     * @return ShortLinkDO
     */
    ShortLinkDO findByShortLinCode(String shortLinkCode);


    /**
     * 删除
     * @param shortLinkCode code
     * @param accountNo accountNo
     * @return int
     */
    int logicDelShortLink(String shortLinkCode,Long accountNo);
}
