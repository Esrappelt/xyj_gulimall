package com.xyj.gulimall.thirdparty.controller;

import com.xyj.common.utils.R;
import com.xyj.gulimall.thirdparty.component.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author jie
 * @Date 2023/7/29 17:30
 */
@RestController
@RequestMapping("/sms")
public class SmsSendController {
    @Autowired
    SmsService service;
    @RequestMapping("/sendCode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code")String code){
        service.sendSmsCode(phone, code);
        return R.ok();
    }
}
