package com.stone.aiexam.filter;

import com.stone.aiexam.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class AdminFilter implements Filter {

    private final JwtUtil jwtUtil;

    public AdminFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String uri = request.getRequestURI();

        // 不是管理端，放行
        if (!uri.startsWith("/api/admin/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 管理端，必须带 token
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            writeJson(response, 401, "未登录");
            return;
        }

        if (!jwtUtil.validateToken(token)) {
            writeJson(response, 401, "token无效或已过期");
            return;
        }

        Claims claims = jwtUtil.parseToken(token);
        if (!"ADMIN".equals(claims.get("role", String.class))) {
            writeJson(response, 403, "无管理员权限");
            return;
        }

        log.info("管理端请求通过: {}", uri);
        filterChain.doFilter(request, response);
    }

    private void writeJson(HttpServletResponse response, int status, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(status);
        response.getWriter().write(String.format("{\"code\":%d,\"message\":\"%s\"}", status, message));
    }
}
