package com.zskj.link.controller.request;

import lombok.Data;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/13
 * <p>
 *
 * </p>
 */
@Data
public class ShortLinkDelRequest {

    /**
     * 组
     */
    private Long groupId;

    /**
     * 映射id
     */
    private Long mappingId;


    /**
     * 短链码
     */
    private String code;



}
