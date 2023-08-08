package com.xyj.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xyj.common.utils.PageUtils;
import com.xyj.gulimall.member.entity.MemberEntity;
import com.xyj.gulimall.member.exception.PhoneExistException;
import com.xyj.gulimall.member.exception.UserNameExistException;
import com.xyj.gulimall.member.vo.MemberLoginVo;
import com.xyj.gulimall.member.vo.MemberRegisterVo;

import java.util.Map;

/**
 * 会员
 *
 * @author xyj
 * @email xyj@gmail.com
 * @date 2023-06-15 18:12:09
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(MemberRegisterVo memberRegisterVo);

    void checkPhoneUnique(String phone) throws PhoneExistException;
    void checkUserNameUnique(String userName) throws UserNameExistException;

    MemberEntity login(MemberLoginVo memberLoginVo);
}

