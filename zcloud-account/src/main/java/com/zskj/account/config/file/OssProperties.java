package com.zskj.account.config.file;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/24
 * <p>
 * oss属性
 * </p>
 */

@Data
@ConfigurationProperties(prefix = "file-upload.oss")
public class OssProperties {

    /**
     * 地域url（外网）
     */
    private String endpoint;

    /**
     * 地域url（内网）
     */
    private String endpointEcs;

    /**
     * 存储桶名称
     */
    private String bucketName;

    /**
     * oss-user accessKey
     */
    private String accessKey;

    /**
     * oss-user accessSecret
     */
    private String accessSecret;
}
