package com.perfect.filesystem.config.spring;

import com.perfect.filesystem.config.interceptor.ActionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author zhangxh
 */
@Configuration
public class ActionInterceptorConfig implements WebMvcConfigurer {

    /**
     * 处理拦截器，主要处理controller中的处理前，中，后
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //  /**表示拦截所有请求
        String[] addPath = {"/**"};
        // 放行getStudent和getUser两个请求：其中getUser请求没有对应的处理器
        String[] excludePath = {"/getStudent","/getUser"};
        registry.addInterceptor(new ActionInterceptor()).addPathPatterns(addPath).excludePathPatterns(excludePath);
    }

    /**
     *
     * @param returnValueHandlers
     */
    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        // 处理返回值
//        WebMvcConfigurer.super.addReturnValueHandlers(returnValueHandlers);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        converters.add(0, new CallbackMappingJackson2HttpMessageConverter());
    }
}
