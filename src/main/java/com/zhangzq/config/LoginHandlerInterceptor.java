package com.zhangzq.config;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//拦截器
public class LoginHandlerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //登录成功之后，应该有用户的sessoin：
        Object loginUser = request.getSession().getAttribute("loginUser");
        //判断该用户有没有登录
        if (loginUser == null) {
            request.setAttribute("msg", "没有权限，请先登录");
            //返回页面
            request.getRequestDispatcher("/index.html").forward(request, response);
            //放行
            return false;
        } else {
            return true;
        }


    }

}
