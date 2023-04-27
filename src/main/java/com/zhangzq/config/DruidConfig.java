package com.zhangzq.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DruidConfig {
    @ConfigurationProperties(prefix = "spring.datasource") //将全局配置文件中前缀为spring.datasource的属性值注入到 com.alibaba.druid.pool.DruidDataSource 的同名参数中
    @Bean //将自定义的 Druid数据源添加到容器中，不再让 Spring Boot 自动创建
    public DataSource druidDataSource() {
        return new DruidDataSource();
    }

    //配置Druid监控管理后台的Servlet；
    //注册到bean中
    @Bean
    public ServletRegistrationBean statViewServlet() {
        //创建一个servlet,并定义请求路径
        ServletRegistrationBean<StatViewServlet> servlet = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");

        //初始化参数设置:后台登录的账号密码、允许访问者
        HashMap<String, String> initParameters = new HashMap<>();
        initParameters.put("loginUsername", "admin");//key名固定,这些参数可以在StatViewServlet的父类ResourceServlet中找到
        initParameters.put("loginPassword", "123456");//key名固定,这些参数可以在StatViewServlet的父类ResourceServlet中找到
        initParameters.put("allow", "localhost");//设置允许访问的人:这里表示只有本机可以访问(后面参数为空则所有人都可以访问)
        //设置初始化参数
        servlet.setInitParameters(initParameters);

        //返回servlet
        return servlet;
    }

    //配置Druid监控之web监控的filter
    //注册到bean中
    @Bean
    public FilterRegistrationBean webStatFilter() {
        //创建一个filter
        FilterRegistrationBean filter = new FilterRegistrationBean(new WebStatFilter());

        //初始化参数设置
        Map<String, String> initParams = new HashMap<>();
        initParams.put("exclusions", "*.js,*.css,/druid/*,/jdbc/*");//exclusions:设置哪些请求进行过滤排除掉，从而不进行统计
        //设置初始化参数
        filter.setInitParameters(initParams);

        //添加过滤规则:/*表示过滤所有请求
        filter.setUrlPatterns(Arrays.asList("/*"));

        //返回filter
        return filter;
    }


}
