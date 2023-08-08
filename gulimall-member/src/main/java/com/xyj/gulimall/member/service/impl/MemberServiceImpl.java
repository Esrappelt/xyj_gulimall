package com.xyj.gulimall.member.service.impl;

import com.xyj.gulimall.member.dao.MemberLevelDao;
import com.xyj.gulimall.member.entity.MemberLevelEntity;
import com.xyj.gulimall.member.exception.PhoneExistException;
import com.xyj.gulimall.member.exception.UserNameExistException;
import com.xyj.gulimall.member.vo.MemberLoginVo;
import com.xyj.gulimall.member.vo.MemberRegisterVo;
import org.apache.commons.codec.digest.Md5Crypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyj.common.utils.PageUtils;
import com.xyj.common.utils.Query;

import com.xyj.gulimall.member.dao.MemberDao;
import com.xyj.gulimall.member.entity.MemberEntity;
import com.xyj.gulimall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void register(MemberRegisterVo memberRegisterVo) {
        MemberEntity memberEntity = new MemberEntity();
        MemberLevelEntity memberLevelEntity = memberLevelDao.getDefaultLevel();
        // 校验手机号和用户名
        checkPhoneUnique(memberRegisterVo.getPhone());
        checkUserNameUnique(memberRegisterVo.getUserName());
        memberEntity.setUsername(memberRegisterVo.getUserName());
        memberEntity.setLevelId(memberLevelEntity.getId());
        memberEntity.setMobile(memberRegisterVo.getPhone());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();// spring的密码加密器
        String encodedPwd = passwordEncoder.encode(memberRegisterVo.getPassword());
        memberEntity.setPassword(encodedPwd);
        this.baseMapper.insert(memberEntity);
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneExistException {
        Integer selectCount = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if(selectCount > 0){
            throw new PhoneExistException();
        }
    }

    @Override
    public void checkUserNameUnique(String userName) throws UserNameExistException {
        Integer selectCount = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName));
        if(selectCount > 0){
            throw new UserNameExistException();
        }
    }

    @Override
    public MemberEntity login(MemberLoginVo memberLoginVo) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        // 账号
        String loginacct = memberLoginVo.getLoginacct();
        // 明文密码
        String rwaPassword = memberLoginVo.getPassword();
        MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("username", loginacct).or().eq("mobile", loginacct));
        if(memberEntity == null){
            // 登录失败
            return null;
        }
        // 数据库加密的密码
        String dbPassword = memberEntity.getPassword();
        // 拿明文密码和数据库的加密密码进行匹配
        boolean matches = passwordEncoder.matches(rwaPassword, dbPassword);
        if(!matches){
            return null;
        }
        // 登录成功
        return memberEntity;
    }

}