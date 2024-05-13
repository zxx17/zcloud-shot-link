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
    ShortLinkDO findByShortLinkCode(String shortLinkCode);


    /**
     * 删除
     * @param shortLinkDO do
     * @return int
     */
    int logicDelShortLink(ShortLinkDO shortLinkDO);

    /**
     * 更新短链
     * @param shortLinkDO do
     * @return int
     */
    int update(ShortLinkDO shortLinkDO);
}
