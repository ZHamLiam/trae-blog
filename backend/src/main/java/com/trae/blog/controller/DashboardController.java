package com.trae.blog.controller;

import com.trae.blog.common.Result;
import com.trae.blog.dto.DashboardDTO;
import com.trae.blog.dto.StatisticsDTO;
import com.trae.blog.service.ArticleService;
import com.trae.blog.service.CommentService;
import com.trae.blog.service.DashboardService;
import com.trae.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 仪表盘控制器
 * 提供仪表盘所需的统计数据
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private DashboardService dashboardService;

    /**
     * 获取仪表盘统计数据
     *
     * @return 统计数据，包括文章总数、总浏览量、评论总数、用户总数、最近评论、热门文章、最新用户
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('dashboard:view')")
    public Result<DashboardDTO> getDashboardStats() {
        DashboardDTO dashboardDTO = dashboardService.getDashboardStats();
        return Result.success(dashboardDTO);
    }
    
    /**
     * 获取仪表盘基础统计数据（兼容旧版本）
     *
     * @return 基础统计数据，包括文章总数、总浏览量、评论总数、用户总数
     */
    @GetMapping("/basic-stats")
    @PreAuthorize("hasAuthority('dashboard:view')")
    public Result<Map<String, Object>> getBasicStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取文章总数
        long articleCount = articleService.count();
        stats.put("articleCount", articleCount);
        
        // 获取总浏览量
        long viewCount = articleService.getTotalViewCount();
        stats.put("viewCount", viewCount);
        
        // 获取评论总数
        long commentCount = commentService.count();
        stats.put("commentCount", commentCount);
        
        // 获取用户总数
        long userCount = userService.count();
        stats.put("userCount", userCount);
        
        return Result.success(stats);
    }
    
    /**
     * 获取统计图表数据
     *
     * @return 统计图表数据，包括文章发布趋势、用户注册趋势、评论趋势、分类统计等
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('dashboard:view')")
    public Result<StatisticsDTO> getStatisticsData() {
        StatisticsDTO statisticsDTO = dashboardService.getStatisticsData();
        return Result.success(statisticsDTO);
    }
}