package com.zskj.account.storage;

import java.io.InputStream;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/24
 * <p>
 * 文件操作策略
 * </p>
 */

public interface IFileStorage {

    /**
     * 文件上传
     * @param key 文件路径
     * @param inputStream 文件输入流
     * @param contentLength contentLen
     * @return url
     */
    String uploadFile(String key, InputStream inputStream, long contentLength);

}
