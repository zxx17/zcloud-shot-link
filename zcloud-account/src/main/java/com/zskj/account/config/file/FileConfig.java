package com.zskj.account.config.file;

import com.zskj.account.storage.IFileStorage;
import com.zskj.account.storage.impl.AliCloudOssFileStorageImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/24
 * <p>
 * 文件操作配置
 * local和cloud同时为true表示 私密文件放私有云【minio】 普通文件放云厂商
 * TODO: local
 * </p>
 */

@Configuration
@EnableConfigurationProperties(OssProperties.class)
public class FileConfig {


    /**
     * 阿里云
     */
    @Bean
    @ConditionalOnProperty(prefix = "file-upload.oss", name = "enable", havingValue = "true")
    public IFileStorage cloud01FileStorage(OssProperties prop) {
        return new AliCloudOssFileStorageImpl(
                prop.getBucketName(),
                prop.getEndpoint(),
                prop.getAccessKey(),
                prop.getAccessSecret());
    }


}
