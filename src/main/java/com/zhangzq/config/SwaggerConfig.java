package com.zhangzq.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2  //开启swagger2
public class SwaggerConfig {

    //配置了swagger的Docket的bean实例
    @Bean
    public Docket docket(){
        return new Docket(DocumentationType.SWAGGER_2)
                //.apiInfo(apiInfo())//swagger基本信息(标题、介绍、联系URL...),默认使用自定义的，也可以自己新建apiInfo()
                .groupName("xlyoung")//配置API的分组，默认为default
                .enable(false) //enable是否启用默认为true,swagger如果为false，则Swagger不能在浏览器中访问
                .select()
                //RequestHandlerSelectors，配置要扫描接口的方式
                //-->basePackage，指定要扫描的包
                //-->any()，扫描全面
                //-->none()，都不扫描
                //-->withClassAnnotation，扫描类上的注解
                //-->withMethodAnnotation，扫描方法上的注解
                .apis(RequestHandlerSelectors.basePackage("com.zhangzq.controller"))
                //paths过滤什么路径
                .paths(PathSelectors.ant("/user/**"))
                .build();
    }



    //如何去配置多个分组，只需要配置多个Docket实例就行
    @Bean
    public Docket docket1(){
        return new Docket(DocumentationType.SWAGGER_2).groupName("A");
    }
    @Bean
    public Docket docket2(){
        return new Docket(DocumentationType.SWAGGER_2).groupName("B");
    }
    @Bean
    public Docket docket3(){
        return new Docket(DocumentationType.SWAGGER_2).groupName("C");
    }
}
