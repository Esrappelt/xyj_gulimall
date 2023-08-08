package com.xyj.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * spu信息介绍
 * 
 * @author xyj
 * @email xyj@gmail.com
 * @date 2023-06-19 20:55:29
 */
@Data
@TableName("pms_spu_info_desc")
public class SpuInfoDescEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品id
	 */
	@TableId(type = IdType.INPUT) // 这个是外部输入  不是主键 因此需要重新设置
	private Long spuId;
	/**
	 * 商品介绍
	 */
	private String decript;

}
