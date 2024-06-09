package com.zskj.account.config.web;

import com.zskj.common.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/25
 * <p>
 *
 * </p>
 */

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                //添加拦截的路径
                .addPathPatterns("/api/account/*/**", "/api/traffic/*/**")
                //排除不拦截
                // TODO 上传接口放行了，后续必须做限流
                .excludePathPatterns(
                        "/api/account/*/register","/api/account/*/upload","/api/account/*/login",
                        "/api/notify/*/captcha","/api/notify/*/send_code", "/api/traffic/*/reduce");
    }
}

