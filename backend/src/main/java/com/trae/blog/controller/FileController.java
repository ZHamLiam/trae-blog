package com.trae.blog.controller;

import com.trae.blog.common.Result;
import com.trae.blog.config.OSSConfig;
import com.trae.blog.util.OSSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/files")
public class FileController {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.url}")
    private String uploadUrl;
    
    @Autowired
    private OSSConfig ossConfig;

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件访问URL
     */
    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('file:upload')")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }

        // 获取文件名
        String originalFilename = file.getOriginalFilename();
        // 获取文件后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 生成新文件名
        String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;

        // 按日期构建目录
        String dateDir = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        File targetDir = new File(uploadPath + File.separator + dateDir);
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        // 构建文件路径
        File targetFile = new File(targetDir.getAbsolutePath() + File.separator + fileName);

        try {
            // 保存文件
            file.transferTo(targetFile);
            // 返回文件访问URL
            String fileUrl = uploadUrl + "/" + dateDir + "/" + fileName;
            return Result.success("上传成功", fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传文件到阿里云OSS
     *
     * @param file 文件
     * @return 文件访问URL
     */
    @PostMapping("/upload/oss")
    @PreAuthorize("hasAuthority('file:upload')")
    public Result<String> uploadFileToOSS(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }
        
        try {
            // 上传文件到OSS
            String fileUrl = OSSUtil.uploadFile(file, ossConfig);
            return Result.success("上传成功", fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }
}