package com.tem.frame.config;

import com.tem.frame.handle.GlobalRequestHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * Web 配置
 */
@Slf4j
@Configuration
public class WebAppConfig extends WebMvcConfigurationSupport {


    @Autowired
    private GlobalRequestHandler globalRequestHandler;

    /**
     * 注册拦截器
     *
     * @param registry
     */
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(this.globalRequestHandler)  //注册改拦截器
                .addPathPatterns("/**")     //表示拦截所有的请求，
                .excludePathPatterns("/login", "/upload", "/captcha");  //表示除了登陆之外，因为登陆不需要登陆也可以访问
    }

    /**
     * 配置允许跨域访问
     *
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedHeaders("*")
                .allowedMethods("*")
                .maxAge(3600)
                .allowCredentials(true);
    }

}
