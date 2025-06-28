package com.fzb.config;
//设置Cors配置，因为前端使用非标准访问请求头，需要服务器返回cors验证

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:7000") // 允许的前端地址
                .allowedMethods("*") // 允许的方法
                .allowedHeaders("*") // 允许所有头
                .maxAge(3600) //预检请求缓存时间，1小时
                .allowCredentials(true); // 允许携带 Cookie
    }
}