package com.stone.aiexam.config;

import com.stone.aiexam.filter.AdminFilter;
import com.stone.aiexam.filter.StudentFilter;
import com.stone.aiexam.utils.JwtUtil;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<AdminFilter> adminFilter(JwtUtil jwtUtil){
        FilterRegistrationBean<AdminFilter> bean=new FilterRegistrationBean<>();
        bean.setFilter(new AdminFilter(jwtUtil));
        bean.addUrlPatterns("/*");
        bean.setOrder(1);
        return bean;
    }

    @Bean
    public FilterRegistrationBean<StudentFilter> studentFilter(JwtUtil jwtUtil) {
        FilterRegistrationBean<StudentFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new StudentFilter(jwtUtil));
        bean.addUrlPatterns("/*");
        bean.setOrder(2);
        return bean;
    }


}
