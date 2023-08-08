package com.xyj.gulimall.member.vo;

import lombok.Data;
import lombok.ToString;

/**
 * @Author jie
 * @Date 2023/7/29 21:13
 */
@Data
@ToString
public class MemberLoginVo {
    private String loginacct;
    private String password;
}
