package com.xyj.gulimall.authserver.vo;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @Author jie
 * @Date 2023/7/29 18:33
 */
@Data
@ToString
public class UserRegistVo {
    @NotEmpty(message = "用户名必须提交")
    private String userName;
    @NotEmpty(message = "密码必须填写")
    @Length(min = 6, max = 18, message = "密码必须是6-18位字符")
    private String password;
    @Pattern(regexp = "(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}", message = "手机号格式不正确")
    private String phone;
    @Length(min = 5, max = 5, message = "必须是5位验证码")
    private String code;
}
