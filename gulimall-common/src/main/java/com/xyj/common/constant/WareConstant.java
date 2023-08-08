package com.xyj.common.constant;

/**
 * @Author jie}
 * @Date 2023/6/22 23:07}
 */
public class WareConstant {
    public enum PurchaseStatusEnum {
        CREATED(0, "新建"),
        ASSIGNED(1, "已分配"),
        RECEIVE(2, "已领取"),
        FINISH(3, "已完成"),
        HASERROR(4, "有异常");
        private Integer code;
        private String msg;
        PurchaseStatusEnum(Integer code, String msg) {
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
    public enum PurchaseDetailStatusEnum {
        CREATED(0, "新建"),
        ASSIGNED(1, "已分配"),
        BUYING(2, "正在采购"),
        FINISH(3, "已完成"),
        HASERROR(4, "采购失败");
        private Integer code;
        private String msg;
        PurchaseDetailStatusEnum(Integer code, String msg) {
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
