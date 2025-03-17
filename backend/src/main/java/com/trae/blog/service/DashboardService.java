package com.trae.blog.service;

import com.trae.blog.dto.DashboardDTO;
import com.trae.blog.dto.StatisticsDTO;

/**
 * 仪表盘服务接口
 * 提供仪表盘所需的统计数据和详细信息
 */
public interface DashboardService {
    
    /**
     * 获取仪表盘统计数据
     *
     * @return 仪表盘数据传输对象
     */
    DashboardDTO getDashboardStats();
    
    /**
     * 获取统计图表数据
     *
     * @return 统计数据传输对象
     */
    StatisticsDTO getStatisticsData();
}