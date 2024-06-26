package com.zskj.link.service;

import com.zskj.common.model.EventMessage;
import com.zskj.common.util.JsonData;
import com.zskj.link.controller.request.ShortLinkAddRequest;
import com.zskj.link.controller.request.ShortLinkDelRequest;
import com.zskj.link.controller.request.ShortLinkPageRequest;
import com.zskj.link.controller.request.ShortLinkUpdateRequest;
import com.zskj.link.vo.ShortLinkVO;

import java.util.Map;

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

    /**
     * 处理短链新增事件
     * @param eventMessage event
     * @return boolean
     */
    boolean handlerAddShortLink(EventMessage eventMessage);

    /**
     * 分页查找短链 B端
     * @param request req
     * @return map
     */
    Map<String, Object> pageByGroupId(ShortLinkPageRequest request);

    /**
     * 删除短链
     * @param request req
     * @return JsonData
     */
    JsonData delShortLink(ShortLinkDelRequest request);

    /**
     * 更新短链
     * @param request req
     * @return jsonData
     */
    JsonData updateShortLink(ShortLinkUpdateRequest request);

    /**
     * 处理短链删除事件
     * @param eventMessage event
     * @return boolean
     */
    boolean handleDelShortLink(EventMessage eventMessage);

    /**
     * 处理短链更新事件
     * @param eventMessage event
     * @return boolean
     */
    boolean handleUpdateShortLink(EventMessage eventMessage);
}
