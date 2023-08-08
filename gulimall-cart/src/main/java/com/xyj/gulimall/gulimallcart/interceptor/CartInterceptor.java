package com.xyj.gulimall.gulimallcart.interceptor;

import com.xyj.common.vo.MemberResponseVo;
import com.xyj.gulimall.gulimallcart.vo.UserInfoTo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.UUID;

import static com.xyj.common.utils.Constant.LOGIN_USER;
import static com.xyj.gulimall.gulimallcart.constant.CartConstant.TEMP_USER_COOKIE_NAME;
import static com.xyj.gulimall.gulimallcart.constant.CartConstant.TEMP_USER_COOKIE_TIMEOUT;

/**
 * @Author jie
 * @Date 2023/7/30 11:45
 */
@Component
public class CartInterceptor implements HandlerInterceptor {
    // 线程的共享数据
    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        UserInfoTo userInfoTo = new UserInfoTo();
        HttpSession session = request.getSession();
        // 判断是否登录了
        MemberResponseVo login = (MemberResponseVo) session.getAttribute(LOGIN_USER);
        if(login != null){
            // 登录了
            userInfoTo.setUserId(login.getId());
        }
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                String name = cookie.getName();
                if(TEMP_USER_COOKIE_NAME.equals(name)){
                    userInfoTo.setUserKey(cookie.getValue());
                    userInfoTo.setTempUser(true);
                }
            }
        }
        // 没有临时用户  分配一个临时用户
        if(StringUtils.isEmpty(userInfoTo.getUserKey())){
            userInfoTo.setUserKey(UUID.randomUUID().toString());
        }
        // 目标方法执行之前 进行数据保存
        threadLocal.set(userInfoTo);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoTo userInfoTo = threadLocal.get();
        // 如果已经有了临时用户 直接放行
        if(userInfoTo.getTempUser()){
            return;
        }
        // 否则设置cookie
        // 持续延长过期时间
        Cookie cookie = new Cookie(TEMP_USER_COOKIE_NAME, userInfoTo.getUserKey());
        cookie.setDomain("gulimall.com");
        cookie.setMaxAge(TEMP_USER_COOKIE_TIMEOUT);
        response.addCookie(cookie);
    }
}
