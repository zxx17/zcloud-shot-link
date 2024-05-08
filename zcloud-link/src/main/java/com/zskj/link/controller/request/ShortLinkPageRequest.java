package com.zskj.link.controller.request;

import lombok.Data;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/8
 * <p>
 *
 * </p>
 */
@Data
public class ShortLinkPageRequest {


    /**
     * 组
     */
    private Long groupId;

    /**
     * 第几页
     */
    private int page;

    /**
     * 每页多少条
     */
    private int size;

}

