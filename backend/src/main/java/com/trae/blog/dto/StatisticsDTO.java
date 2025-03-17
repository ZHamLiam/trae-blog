package com.trae.blog.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 统计数据传输对象
 * 用于仪表盘图表展示
 */
@Data
public class StatisticsDTO {
    
    /**
     * 文章发布趋势数据
     * key: 日期（格式：yyyy-MM）
     * value: 文章数量
     */
    private Map<String, Long> articleTrend;
    
    /**
     * 用户注册趋势数据
     * key: 日期（格式：yyyy-MM）
     * value: 用户数量
     */
    private Map<String, Long> userTrend;
    
    /**
     * 评论趋势数据
     * key: 日期（格式：yyyy-MM）
     * value: 评论数量
     */
    private Map<String, Long> commentTrend;
    
    /**
     * 文章分类统计
     * key: 分类名称
     * value: 文章数量
     */
    private Map<String, Long> categoryStats;
    
    /**
     * 浏览量趋势数据
     * key: 日期（格式：yyyy-MM）
     * value: 浏览量
     */
    private Map<String, Long> viewTrend;
}