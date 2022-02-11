package com.frankeleyn.srb.oss.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Frankeleyn
 * @date 2022/2/11 10:19
 */
public interface FileService {

    /**
     * 文件上传
     * @param file
     * @param module
     * @return 上传地址
     */
    String upload(MultipartFile file, String module);

    /**
     * 删除文件
     * @param url
     */
    void removeFile(String url);
}
