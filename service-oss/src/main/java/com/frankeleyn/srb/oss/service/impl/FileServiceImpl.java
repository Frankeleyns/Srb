package com.frankeleyn.srb.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.frankeleyn.srb.oss.service.FileService;
import com.frankeleyn.srb.oss.utils.OssProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * @author Frankeleyn
 * @date 2022/2/11 10:22
 */
@Service
public class FileServiceImpl implements FileService {

    @Override
    public String upload(MultipartFile file, String module) {
        // 1. 创建 OSS 连接
        OSS client = new OSSClientBuilder().build(OssProperties.ENDPOINT, OssProperties.KEY_ID, OssProperties.KEY_SECRET);

        // 2. 定义上传路径规则: 模块名/日期/文件名,例 user/2022/02/11/uuid.png
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uuid = UUID.randomUUID().toString();
        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String path = module + "/" + date + "/" + uuid + "." + ext;

        // 3. 上传文件
        boolean exist = client.doesBucketExist(OssProperties.BUCKET_NAME);
        if(!exist) {
            // 如果桶不存在则创建
            client.createBucket(OssProperties.BUCKET_NAME);
            client.setBucketAcl(OssProperties.BUCKET_NAME, CannedAccessControlList.PublicRead);
        }
        try {
            client.putObject(OssProperties.BUCKET_NAME, path, file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 4. 关闭连接
        client.shutdown();

        // 5. 返回文件上传地址 https://桶名.节点名/模块名/日期/文件名 例: https://bucket.shenzhen/user/2022/02/11/uuid.png
        String urlPre = "https://" + OssProperties.BUCKET_NAME + "." + OssProperties.ENDPOINT + "/";
        return  urlPre + path;
    }

    @Override
    public void removeFile(String url) {
        // 1. 创建 OSS 实例
        OSS client = new OSSClientBuilder().build(OssProperties.ENDPOINT, OssProperties.KEY_ID, OssProperties.KEY_SECRET);

        // 2. 删除文件
        String urlPre = "https://" + OssProperties.BUCKET_NAME + "." + OssProperties.ENDPOINT + "/";
        String objectName = url.substring(urlPre.length());
        client.deleteObject(OssProperties.BUCKET_NAME, objectName);

        // 3. 关闭 OSS
        client.shutdown();
    }

}
