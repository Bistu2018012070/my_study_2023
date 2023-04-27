package com.zhangzq.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyMvcConfig implements WebMvcConfigurer {

    @Override
    //添加视图控制器
    public void addViewControllers(ViewControllerRegistry registry) {
        //当url为  /  时跳转到index.html页面    以此类推
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/index.html").setViewName("index");
        registry.addViewController("/main.html").setViewName("dashboard");
    }

    //国际化就配置好了
    @Bean
    public LocaleResolver localeResolver() {
        return new MyLocaleResolver();
    }

    //添加拦截器，当url直接访问页面的话会启动拦截
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //addPathPatterns("/**")值拦截所有请求       excludePathPatterns("/index.html","/","/user/login")放行那些请求
        // registry.addInterceptor(new LoginHandlerInterceptor())
        //         .addPathPatterns("/**").excludePathPatterns("/index.html", "/","/user/*","/queryEmpList", "/user/login", "/css/*", "/js/**", "/img/**");
    }
}
