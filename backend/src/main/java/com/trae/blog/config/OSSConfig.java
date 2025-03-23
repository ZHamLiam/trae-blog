package com.trae.blog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云OSS配置类
 */
@Configuration
@ConfigurationProperties(prefix = "aliyun.oss")
public class OSSConfig {
    
    /**
     * 阿里云OSS域名
     */
    private String endpoint;
    
    /**
     * 阿里云OSS访问密钥ID
     */
    private String accessKeyId;
    
    /**
     * 阿里云OSS访问密钥密码
     */
    private String accessKeySecret;
    
    /**
     * 阿里云OSS存储桶名称
     */
    private String bucketName;
    
    /**
     * 阿里云OSS文件存储目录
     */
    private String dir;
    
    /**
     * 阿里云OSS访问URL
     */
    private String url;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}