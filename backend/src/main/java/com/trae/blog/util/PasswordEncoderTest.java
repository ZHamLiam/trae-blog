package com.trae.blog.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码加密测试工具类
 * 用于生成BCryptPasswordEncoder加密后的密码
 */
public class PasswordEncoderTest {

    public static void main(String[] args) {
        // 创建BCryptPasswordEncoder实例
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 要加密的明文密码
        String[] passwords = {"admin123", "user123", "test123"};
        
        System.out.println("密码加密结果：");
        System.out.println("--------------------");
        
        // 对每个密码进行加密并输出结果
        for (String password : passwords) {
            String encodedPassword = encoder.encode(password);
            System.out.println("原始密码: " + password);
            System.out.println("加密密码: " + encodedPassword);
            System.out.println("验证结果: " + encoder.matches(password, encodedPassword));
            System.out.println("--------------------");
        }
        
        // 自定义密码加密
        System.out.println("\n自定义密码加密：");
        System.out.println("请修改下面的代码，输入你想要加密的密码");
        System.out.println("--------------------");
        
        String customPassword = "123456"; // 在这里修改为你想要加密的密码
        String encodedCustomPassword = encoder.encode(customPassword);
        System.out.println("原始密码: " + customPassword);
        System.out.println("加密密码: " + encodedCustomPassword);
        System.out.println("验证结果: " + encoder.matches(customPassword, encodedCustomPassword));
    }
}