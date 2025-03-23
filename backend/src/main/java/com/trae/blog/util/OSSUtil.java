package com.trae.blog.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.trae.blog.config.OSSConfig;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 阿里云OSS工具类
 */
public class OSSUtil {

    /**
     * 上传文件到OSS
     *
     * @param file 文件
     * @param ossConfig OSS配置
     * @return 文件访问URL
     */
    public static String uploadFile(MultipartFile file, OSSConfig ossConfig) throws IOException {
        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(
                ossConfig.getEndpoint(),
                ossConfig.getAccessKeyId(),
                ossConfig.getAccessKeySecret());

        try {
            // 获取文件名
            String originalFilename = file.getOriginalFilename();
            // 获取文件后缀
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 生成新文件名
            String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;

            // 按日期构建目录
            String dateDir = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            // 构建文件路径
            String objectName = ossConfig.getDir() + "/" + dateDir + "/" + fileName;

            // 上传文件流
            InputStream inputStream = file.getInputStream();
            
            // 创建上传Object的Metadata
            ObjectMetadata metadata = new ObjectMetadata();
            // 设置ContentLength
            metadata.setContentLength(file.getSize());
            // 设置ContentType
            metadata.setContentType(file.getContentType());

            // 上传文件
            ossClient.putObject(ossConfig.getBucketName(), objectName, inputStream, metadata);

            // 构建文件访问URL
            return ossConfig.getUrl() + "/" + objectName;
        } finally {
            // 关闭OSSClient
            ossClient.shutdown();
        }
    }
}