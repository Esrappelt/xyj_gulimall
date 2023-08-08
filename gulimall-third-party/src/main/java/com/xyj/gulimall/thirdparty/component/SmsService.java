package com.xyj.gulimall.thirdparty.component;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @Author jie
 * @Date 2023/7/29 17:27
 */
@Component
@Data
public class SmsService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private String code;
    private String phone;
    public void sendSmsCode(String phone, String code){
        this.phone = phone;
        this.code = code;
        sendToRedis();
    }
    private void sendToRedis(){
        // 随机生成一个验证码 在redis里面
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set(String.valueOf(phone), String.valueOf(code), 300, TimeUnit.SECONDS);
    }
}
