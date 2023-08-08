package com.xyj.gulimall.seckill.interceptor;

import com.xyj.common.vo.MemberResponseVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static com.xyj.common.utils.Constant.LOGIN_USER;

/**
 * @Author jie
 * @Date 2023/7/31 15:56
 */
@Component
public class LoginUserInterceptor implements HandlerInterceptor {
    public static ThreadLocal<MemberResponseVo> loginUser = new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        boolean match = new AntPathMatcher().match("/kill", requestURI);
        if(!match) {
            return true;
        }
        // 登录拦截器
        HttpSession session = request.getSession();
        MemberResponseVo responseVo = (MemberResponseVo) session.getAttribute(LOGIN_USER);
        if(responseVo != null){
            loginUser.set(responseVo);
            return true;
        }else {
            //没有登录 就去登录
            request.getSession().setAttribute("msg", "请先登录");
            response.sendRedirect("http://auth.gulimall.com/login.html");
            return false;
        }
    }
}
