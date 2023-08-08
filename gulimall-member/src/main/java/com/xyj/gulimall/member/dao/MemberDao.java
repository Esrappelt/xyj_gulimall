package com.xyj.gulimall.member.dao;

import com.xyj.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author xyj
 * @email xyj@gmail.com
 * @date 2023-06-15 18:12:09
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
