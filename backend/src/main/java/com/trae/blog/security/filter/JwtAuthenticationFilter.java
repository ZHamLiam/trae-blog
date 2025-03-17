package com.trae.blog.security.filter;

import com.trae.blog.security.service.UserDetailsServiceImpl;
import com.trae.blog.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT认证过滤器，用于拦截请求并验证JWT令牌
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;

    @Value("${jwt.tokenPrefix}")
    private String tokenPrefix;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 获取请求头中的Authorization
        String authHeader = request.getHeader(tokenHeader);

        // 判断是否有token
        if (authHeader != null && authHeader.startsWith(tokenPrefix)) {
            // 提取token
            String token = authHeader.substring(tokenPrefix.length()).trim();
            
            // 检查token是否为空
            if (token == null || token.isEmpty()) {
                logger.error("JWT令牌为空");
                chain.doFilter(request, response);
                return;
            }
            logger.info("JWT令牌: " + token);
            try {
                // 验证token格式是否正确（包含两个句点）
                if (!token.contains(".") || token.chars().filter(ch -> ch == '.').count() != 2) {
                    logger.error("JWT格式错误: 令牌必须包含两个句点字符");
                    chain.doFilter(request, response);
                    return;
                }
                
                // 从token中获取用户名
                String username = jwtUtil.getUsernameFromToken(token);

                // 如果用户名不为空且SecurityContext中没有认证信息
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 加载用户信息
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // 验证token是否有效
                    if (jwtUtil.validateToken(token, userDetails)) {
                        // 创建认证对象
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // 设置认证信息到SecurityContext
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (io.jsonwebtoken.MalformedJwtException e) {
                // JWT格式错误，记录日志但不阻止请求继续
                logger.error("JWT格式错误", e);
            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                // JWT已过期，记录日志但不阻止请求继续
                logger.error("JWT已过期", e);
            } catch (io.jsonwebtoken.SignatureException e) {
                // JWT签名验证失败，记录日志但不阻止请求继续
                logger.error("JWT签名验证失败", e);
            } catch (Exception e) {
                // 其他异常，记录日志但不阻止请求继续
                logger.error("处理JWT时发生异常", e);
            }
        }

        // 继续执行过滤器链
        chain.doFilter(request, response);
    }
}