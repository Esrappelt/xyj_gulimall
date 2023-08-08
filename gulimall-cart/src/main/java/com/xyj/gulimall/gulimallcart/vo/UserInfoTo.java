package com.xyj.gulimall.gulimallcart.vo;

import lombok.Data;

/**
 * @Author jie
 * @Date 2023/7/30 12:06
 */
@Data
public class UserInfoTo {
    private Long userId;
    private String userKey;
    private Boolean tempUser=false;
}
