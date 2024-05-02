package com.zskj.link.service;

import com.zskj.link.vo.DomainVO;

import java.util.List;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/2
 * <p>
 *
 * </p>
 */

public interface DomainService {
    /**
     * 列举全部可用域名列表
     * @return list
     */
    List<DomainVO> listAll();
}
