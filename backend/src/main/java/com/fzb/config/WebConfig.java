package com.fzb.config;

import com.fzb.interceptor.LoginCheckInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration //配置类
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginCheckInterceptor loginCheckInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*") // 允许的前端地址
                .allowedMethods("*") // 允许的方法
                .allowedHeaders("Content-Type", "token") // 允许所有头
                .maxAge(3600) //预检请求缓存时间，1小时
                .allowCredentials(false); // 允许携带 Cookie
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {//拦截器
        registry.addInterceptor(loginCheckInterceptor).addPathPatterns("/**")
                .excludePathPatterns("/login","/register","/error");
    }
}
