package com.zskj.link.service;

import com.zskj.common.util.JsonData;
import com.zskj.link.controller.request.ShortLinkAddRequest;
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

    /**
     * 新增短链
     * @param request req
     * @return jsonData
     */
    JsonData createShortLink(ShortLinkAddRequest request);
}
