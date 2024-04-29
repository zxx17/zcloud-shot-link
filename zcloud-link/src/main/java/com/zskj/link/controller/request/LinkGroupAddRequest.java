package com.zskj.link.controller.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/29
 * <p>
 *
 * </p>
 */

@Data
public class LinkGroupAddRequest {

    /**
     * 组名
     */
    @NotNull(message = "组名不能为空")
    private String title;

}
