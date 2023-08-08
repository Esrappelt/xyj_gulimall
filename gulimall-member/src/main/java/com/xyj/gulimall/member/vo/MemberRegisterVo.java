package com.xyj.gulimall.member.vo;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @Author jie
 * @Date 2023/7/29 19:54
 */
@Data
@ToString
public class MemberRegisterVo {
    private String userName;
    private String password;
    private String phone;
}
