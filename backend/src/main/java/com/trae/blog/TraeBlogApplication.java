package com.trae.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

/**
 * 博客系统启动类
 */
@SpringBootApplication
@MapperScan("com.trae.blog.mapper")
public class TraeBlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(TraeBlogApplication.class, args);
    }

}