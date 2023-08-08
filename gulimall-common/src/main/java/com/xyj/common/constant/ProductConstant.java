package com.xyj.common.constant;

/**
 * @Author jie}
 * @Date 2023/6/22 0:13}
 */
public class ProductConstant {
    public enum AttrEnum {
        ATTR_TYPE_BASE(1, "基本属性"), ATTR_TYPE_SALE(0, "销售属性");
        private Integer code;
        private String msg;
        AttrEnum(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }
        public Integer getCode() {
            return code;
        }
        public void setCode(Integer code) {
            this.code = code;
        }
        public String getMsg() {
            return msg;
        }
        public void setMsg(String msg) {
            this.msg = msg;
        }
    }
    public enum Status {
        SPU_NEW(0, "新建"), SPU_UP(1, "上架"), SPU_DOWN(2, "下架");
        private Integer code;
        private String msg;
        Status(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }
        public Integer getCode() {
            return code;
        }
        public void setCode(Integer code) {
            this.code = code;
        }
        public String getMsg() {
            return msg;
        }
        public void setMsg(String msg) {
            this.msg = msg;
        }
    }
}
