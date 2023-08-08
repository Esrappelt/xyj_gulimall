package com.xyj.gulimall.order.constant;

import lombok.Data;

/**
 * @Author jie
 * @Date 2023/7/31 22:21
 */
public class OrderConstant {
    public static final String USER_ORDER_TOKEN_PREFIX = "order:token:";

    public enum OrderStatus {
        CREATE_NEW(0, "待付款"),
        PAYED(1, "已付款"),
        SENDED(2, "已发货"),
        RECIEVED(3, "已完成"),
        CANCLED(4, "已取消"),
        SERVICING(5, "售后中"),
        SERVICED(6, "售后完成");
        private Integer code;
        private String msg;

        OrderStatus(Integer code, String msg) {
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
