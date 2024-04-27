package com.myworld.gradution_project_backend.config;

import com.myworld.gradution_project_backend.utils.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ClassName InterceptorConfig
 * @Descripton TODO
 * @Author wkz
 * @Date 2024/3/18 15:17
 * @Version 1.0
 */

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")  // 其他接口token验证
                .excludePathPatterns("/user/login");  // 所有用户都放行
    }
}

