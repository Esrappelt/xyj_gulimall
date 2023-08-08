package com.xyj.gulimall.authserver.vo;

import lombok.Data;
import lombok.ToString;

/**
 * @Author jie
 * @Date 2023/7/29 21:07
 */
@Data
@ToString
public class UserLoginVo {
    private String loginacct;
    private String password;
}
