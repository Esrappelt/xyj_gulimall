package com.xyj.gulimall.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.xyj.common.exception.BizCideEnume;
import com.xyj.gulimall.member.exception.PhoneExistException;
import com.xyj.gulimall.member.exception.UserNameExistException;
import com.xyj.gulimall.member.openfeign.CouponFeignService;
import com.xyj.gulimall.member.vo.MemberLoginVo;
import com.xyj.gulimall.member.vo.MemberRegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.xyj.gulimall.member.entity.MemberEntity;
import com.xyj.gulimall.member.service.MemberService;
import com.xyj.common.utils.PageUtils;
import com.xyj.common.utils.R;



/**
 * 会员
 *
 * @author xyj
 * @email xyj@gmail.com
 * @date 2023-06-15 18:12:09
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Autowired
    CouponFeignService couponFeignService;
    /*
    获取会员拥有的优惠券
     */
    @RequestMapping("/coupons")
    public R test(){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("张三");
        System.out.println("开始获取");
        R memberCoupons = couponFeignService.memberCoupons();
        Object coupons = memberCoupons.get("coupons");
        return R.ok("成功").put("member", memberEntity).put("coupons", coupons);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @PostMapping("/register")
    public R register(@RequestBody MemberRegisterVo memberRegisterVo){
        try {
            memberService.register(memberRegisterVo);
        }catch (PhoneExistException e){
            int code = BizCideEnume.PHONE_EXIST_EXCEPTION.getCode();
            String msg = BizCideEnume.PHONE_EXIST_EXCEPTION.getMsg();
            return R.error(code, msg);
        }catch (UserNameExistException e){
            int code = BizCideEnume.USER_EXIST_EXCEPTION.getCode();
            String msg = BizCideEnume.USER_EXIST_EXCEPTION.getMsg();
            return R.error(code, msg);
        }
        return R.ok();
    }

    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo memberLoginVo){
        MemberEntity member = memberService.login(memberLoginVo);
        if(member != null){
            return R.ok().setData(member);
        }
        return R.error(BizCideEnume.LOGINACCT_PASSWORD_ERROR_EXCEPTION.getCode(), BizCideEnume.LOGINACCT_PASSWORD_ERROR_EXCEPTION.getMsg());
    }

}
