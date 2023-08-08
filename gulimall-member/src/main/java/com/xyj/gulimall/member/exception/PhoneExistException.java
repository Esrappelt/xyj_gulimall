package com.xyj.gulimall.member.exception;

/**
 * @Author jie
 * @Date 2023/7/29 20:09
 */
public class PhoneExistException extends RuntimeException{
    public PhoneExistException() {
        super("手机号已存在!");
    }
}
