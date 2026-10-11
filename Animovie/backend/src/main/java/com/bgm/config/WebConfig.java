package com.bgm.config;

import com.bgm.security.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    JwtInterceptor jwtInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor).addPathPatterns("/subject/**","/admin/**");
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET","POST","PUT","DELETE","OPTIONS").allowedHeaders("*").exposedHeaders("Authorization");
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String filePath = java.nio.file.Paths.get("uploads").toAbsolutePath().toString().replace('\\','/');
        if(!filePath.endsWith("/")) filePath = filePath + "/";
        registry.addResourceHandler("/uploads/**").addResourceLocations("file:" + filePath);
    }
}
