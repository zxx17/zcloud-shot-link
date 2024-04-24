package com.zskj.account.storage.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import com.zskj.account.storage.IFileStorage;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/24
 * <p>
 * 阿里云oss文件操作
 * </p>
 */
@Slf4j
public class AliCloudOssFileStorageImpl implements IFileStorage {
    private final String bucketName;
    private final String endpoint;
    private final String accessKey;
    private final String accessSecret;

    public AliCloudOssFileStorageImpl(String bucketName, String endpoint, String accessKey, String accessSecret) {
        this.bucketName = bucketName;
        this.endpoint = endpoint;
        this.accessKey = accessKey;
        this.accessSecret = accessSecret;
    }

    /**
     * 文件上传
     *
     * @param key           文件路径
     * @param inputStream   文件输入流
     * @param contentLength contentLen
     * @return url
     */
    @Override
    public String uploadFile(String key, InputStream inputStream, long contentLength) {
        OSS ossClient = new OSSClientBuilder()
                .build(endpoint, accessKey, accessSecret);
        try {
            // 上传文件元数据处理
            ObjectMetadata objectMeta = new ObjectMetadata();
            objectMeta.setContentLength(contentLength);
            PutObjectResult putObjectResult = ossClient.putObject(bucketName, key, inputStream, objectMeta);
            //拼装返回路径
            // TODO 是否持久化到数据库？那就是拿到requestID【参考学堂项目】
            if (putObjectResult != null) {
                return "https://" + bucketName + "." + endpoint + "/" + key;
            }
        } catch (Exception e) {
            log.error("文件上传[oss]失败:{}", e.getMessage());
        } finally {
            if (ossClient != null){
                ossClient.shutdown();
            }
        }
        return null;
    }


}
