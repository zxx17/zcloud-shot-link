package com.zskj.link.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import lombok.*;

/**
 * <p>
 *
 * </p>
 *
 * @author Xinxuan Zhuo
 * @since 2024-05-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("group_code_mapping")
public class GroupCodeMappingDO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 组
     */
    private Long groupId;

    /**
     * 短链标题
     */
    private String title;

    /**
     * 原始url地	址
     */
    private String originalUrl;

    /**
     * 短链域名
     */
    private String domain;

    /**
     * 短链压缩码
     */
    private String code;

    /**
     * ⻓链的md5码，⽅便查找
     */
    private String sign;

    /**
     * 过期时间，	永久就是-1
     */
    private Date expired;

    /**
     * 账号唯⼀编	号
     */
    private Long accountNo;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 0是默认，1是删	除
     */
    private Integer del;

    /**
     * 状态，lock是锁定不可	⽤，active是可⽤
     */
    private String state;

    /**
     * 链接产品层	级：FIRST 免费⻘铜、SECOND⻩⾦、THIRD钻⽯
     */
    private String linkType;


}
