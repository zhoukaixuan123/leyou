package com.leyou.config.filters;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.config.FilterProperties;
import com.leyou.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 功能描述
 *
 * @author zhoukx
 * @date 2019/6/3$
 * @description 自定义过滤器$
 */
@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class AuthFiler extends ZuulFilter {

    @Autowired
    private JwtProperties pops;

    @Autowired
    private FilterProperties filterProperties;

    @Override
    public String filterType() {
        //过滤器类型，前置过滤
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        //过滤器顺序
        return FilterConstants.PRE_DECORATION_FILTER_ORDER-1;
    }

    /*** 
    * @Description: 是否过滤 
    * @Param: [] 
    * @return: boolean 
    * @Author: zhoukx
    * @Date: 2019/6/3 
    */ 
    @Override
    public boolean shouldFilter() {
        //获取上下文
        RequestContext currentContext = RequestContext.getCurrentContext();
        //获取request
        HttpServletRequest request = currentContext.getRequest();
        //获取请求的url路径
        String path = request.getRequestURI();
        //判断是否在白名单
        return !isAllowPath(path);
    }

    private boolean isAllowPath(String path) {
        for (String allowPath : filterProperties.getAllowPaths()) {
            if(path.startsWith(allowPath)){
                return  true;
            }
        }
        return  false;
    }

    /*** 
    * @Description:   过滤器逻辑
    * @Param: [] 
    * @return: java.lang.Object 
    * @Author: zhoukx
    * @Date: 2019/6/3 
    */ 
    @Override
    public Object run() throws ZuulException {
        //获取上下文
      RequestContext ctx =  RequestContext.getCurrentContext();
      //获取request
        HttpServletRequest request = ctx.getRequest();
      //request 中获取token
        String token = CookieUtils.getCookieValue(request,pops.getCookieName());
       try{
           //解析token
           UserInfo  userInfo = JwtUtils.getUserInfo(pops.getPublicKey(),token);
       }catch(Exception e){
           //拦截
        ctx.setSendZuulResponse(false);
        //返回状态吗
        ctx.setResponseStatusCode(403);
       }
        //校验权限
        return null;
    }
}
