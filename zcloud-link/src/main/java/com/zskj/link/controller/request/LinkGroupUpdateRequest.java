package com.zskj.link.controller.request;

import lombok.Data;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/29
 * <p>
 *
 * </p>
 */

@Data
public class LinkGroupUpdateRequest {

    /**
     * 组id
     */
    private Long id;
    /**
     * 组名
     */
    private String title;
}
