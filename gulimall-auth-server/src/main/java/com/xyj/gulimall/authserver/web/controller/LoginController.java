package com.xyj.gulimall.authserver.web.controller;

import com.alibaba.fastjson.TypeReference;
import com.xyj.common.utils.R;
import com.xyj.common.vo.MemberResponseVo;
import com.xyj.gulimall.authserver.feign.MemberFeignService;
import com.xyj.gulimall.authserver.feign.ThirdPartFeignService;
import com.xyj.gulimall.authserver.vo.UserLoginVo;
import com.xyj.gulimall.authserver.vo.UserRegistVo;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.math.raw.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.xyj.common.utils.Constant.LOGIN_USER;

/**
 * @Author jie
 * @Date 2023/7/29 16:52
 */
@Controller
public class LoginController {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ThirdPartFeignService thirdPartFeignService;

    @Autowired
    MemberFeignService memberFeignService;

    @GetMapping({"/", "/login.html"})
    public String index(HttpSession session) {
        Object attribute = session.getAttribute(LOGIN_USER);
        //如果用户没登录那就跳转到登录页面
        if (attribute == null) {
            System.out.println("没有session");
            return "login";
        } else {
            System.out.println("有session");
            return "redirect:http://gulimall.com";
        }
    }

    @GetMapping({"/reg.html"})
    public String reg() {
        return "reg";
    }


    @GetMapping("/sms/sendCode")
    @ResponseBody
    public R sendCode(@RequestParam("phone") String phone) {
        // TODO 接口防刷
        String code = UUID.randomUUID().toString().substring(0, 5); // 随机生成验证码
        thirdPartFeignService.sendCode(phone, code);
        return R.ok();
    }

    @PostMapping("/register")
    public String registry(@Valid UserRegistVo userRegistVo, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField,
                    FieldError::getDefaultMessage));
            redirectAttributes.addFlashAttribute("errors", errors);
            // 校验出错转发到注册页
            return "redirect:http://auth.gulimall.com/reg.html";
        }
        // 注册 远程服务 校验验证码
        String code = userRegistVo.getCode();
        String phone = userRegistVo.getPhone();
        String theCode = stringRedisTemplate.opsForValue().get(phone);
        if (StringUtils.isNotEmpty(theCode) && Objects.equals(code, theCode)) {
            //删除验证码
            stringRedisTemplate.delete(phone);
            //远程调用注册
            R register = memberFeignService.register(userRegistVo);
            if(register.getCode() != 0){
                Map<String, String> errors = new HashMap<>();
                errors.put("msg", register.getMsg());
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.gulimall.com/reg.html";
            }
        } else {
            // 验证码不正确
            Map<String, String> errors = new HashMap<>();
            errors.put("code", "验证码错误");
            redirectAttributes.addFlashAttribute("errors", errors);
            // 校验出错转发到注册页
            return "redirect:http://auth.gulimall.com/reg.html";
        }
        return "redirect:http://auth.gulimall.com/login.html";
    }

    @PostMapping("/login")
    public String login(UserLoginVo userLoginVo, RedirectAttributes redirectAttributes, HttpSession session){
        System.out.println(userLoginVo);
        // 远程登录
        R r = memberFeignService.login(userLoginVo);
        if(r.getCode() == 0){
            // 进入首页
            MemberResponseVo data = r.getData("data", new TypeReference<MemberResponseVo>(){});
            System.out.println("登录成功：用户信息:" + data);
            //TODO 以后实现JWT+REDIS进行登录验证,现在使用的是spring-session+redis
            session.setAttribute(LOGIN_USER, data);
            return "redirect:http://gulimall.com";
        }
        // 返回错误消息
        Map<String, String> errors = new HashMap<>();
        errors.put("msg", r.getMsg());
        redirectAttributes.addFlashAttribute("errors", errors);
        // 重定向到登录界面
        return "redirect:http://auth.gulimall.com/login.html";
    }
    @GetMapping(value = "/loguot.html")
    public String logout(HttpServletRequest request) {
        request.getSession().removeAttribute(LOGIN_USER);
        request.getSession().invalidate();
        return "redirect:http://gulimall.com";
    }
}
