package com.fzb.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.fzb.pojo.Result;
import com.fzb.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override  //目标资源方法运行前运行，返回ture，放行，返回false，不放行
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        /*如果当前请求是 OPTIONS 方法（不区分大小写），则直接返回 true，表示允许该预检请求通过。
        通常在 CORS 过滤器 或 拦截器 中使用，避免对 OPTIONS 请求进行额外的权限校验或业务逻辑处理。*/
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            return true;
        }
        //1.获取请求url
        String url = req.getRequestURI().toString();
        log.info("请求的url:{}",url);

        //2.判断请求url中是否包含login，如果包含，说明是登录操作，放行
        if(url.contains("login") || url.contains("register")){
            log.info("登录或注册操作，放行...");
            return true;
        }

        //3.获取请求头中的令牌（token）
        String jwt = req.getHeader("token");

        //4.判断令牌是否存在，如果不存在，返回错误结果（未登录）
        if(!StringUtils.hasLength(jwt)){
            log.info("请求头为空，返回未登录的信息");
            Result error = Result.error("NOT_LOGIN");//返回错误的信息
            String notLogin = JSONObject.toJSONString(error);//调用fastJSON的转换格式
            resp.getWriter().write(notLogin);
            return false;
        }

        //5.解析token，如果解析失败，返回错误结果（未登录）
        try {
            JwtUtils.parseJWT(jwt);
        } catch (Exception e) {//解析失败
            e.printStackTrace();
            log.info("解析失败");
            Result error = Result.error("NOT_LOGIN");//返回错误的信息
            String notLogin = JSONObject.toJSONString(error);//调用fastJSON的转换格式
            resp.getWriter().write(notLogin);
            return false;
        }

        //6.放行
        log.info("令牌合法");
        return true;

    }

    @Override  //目标资源方法运行后运行
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
        System.out.println("postHandle...");
    }

    @Override  //视图渲染完毕后执行，最后运行
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
        System.out.println("afterCompletion...");
    }

}
