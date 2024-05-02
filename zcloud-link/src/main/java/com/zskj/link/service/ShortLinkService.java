package com.zskj.link.service;

import com.zskj.link.vo.ShortLinkVO;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/2
 * <p>
 *
 * </p>
 */

public interface ShortLinkService {

    /**
     * 解析短链
     * @param shortLinkCode 短链
     * @return vo
     */
    ShortLinkVO parseShortLinkCode(String shortLinkCode);
}
