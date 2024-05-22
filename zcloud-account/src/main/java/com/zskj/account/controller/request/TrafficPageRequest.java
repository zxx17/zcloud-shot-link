package com.zskj.account.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/22
 * <p>
 *
 * </p>
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrafficPageRequest {

    private  int page;

    private int size;
}