package com.xyj.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.xyj.common.validator.group.ListValue;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 *
 * @author xyj
 * @email xyj@gmail.com
 * @date 2023-06-15 16:16:56
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 * 必须是非空的，并且不能是空格
	 */
	@NotBlank(message = "必须提交")
	private String name;
	/**
	 * 品牌logo地址
	 */
	@URL(message = "Logo地址不合法!")
	@NotNull
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@ListValue(value = {0, 1})
	private Integer showStatus;
	/**
	 * 检索首字母，正则表达式 只能是一个字母
	 */
	@Pattern(regexp = "^[a-zA-z]$", message = "首字母必须为a-z或者A-Z！")
	@NotNull
	private String firstLetter;
	/**
	 * 排序
	 */
	@Min(value = 0, message = "排序字段必须大于等于0!")
	@NotNull
	private Integer sort;

}
