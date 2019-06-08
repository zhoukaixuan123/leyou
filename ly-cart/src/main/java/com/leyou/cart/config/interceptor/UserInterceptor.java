package com.leyou.cart.config.interceptor;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.cart.config.JwtProperties;
import com.leyou.common.utils.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 功能描述
 *
 * @author zhoukx
 * @date 2019/6/7$
 * @description 拦截器解析用户$
 */
@Component
@Slf4j
@EnableConfigurationProperties(JwtProperties.class)
public class UserInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    private static  final  ThreadLocal<UserInfo>  tl = new ThreadLocal<>();
    /**
     * 前置拦截器
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取cookie
     String token =    CookieUtils.getCookieValue(request,jwtProperties.getCookieName());

        try {
            UserInfo userInfo = JwtUtils.getUserInfo(jwtProperties.getPublicKey(),token);
            //传递用户 userInfo
            //ThreadLocal 的使用原理  底层是k—v 结构  因为k 是当前线程 所以k 不用写  直接是值就可以
            tl.set(userInfo);
            //放行
            return  true;
        }catch (Exception e){
            log.error("[购物车服务]：解析用户身份失败",e);
        }
        return false;
    }

    /***
    * @Description: 后置  使用完成后清空ThreadLocal
     * @Param: [request, response, handler, ex]
    * @return: void
    * @Author: zhoukx
    * @Date: 2019/6/8
    */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //用完数据一定要清空
        tl.remove();
    }

    public static  UserInfo getUser(){
        return tl.get();
    }
}
