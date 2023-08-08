package com.xyj.gulimall.member.exception;

/**
 * @Author jie
 * @Date 2023/7/29 20:09
 */
public class UserNameExistException extends RuntimeException{
    public UserNameExistException(){
        super("用户名已存在");
    }
}
