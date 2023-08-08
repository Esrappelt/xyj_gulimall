package com.xyj.common.exception;

public enum BizCideEnume {
    UNKNOWN_EXCEPTION(10000, "参数格式校验失败"),
    VALID_EXCEPTION(10001, "参数格式校验失败"),
    PRODUCT_UP_EXCEPTION(10002, "商品上架失败!"),
    USER_EXIST_EXCEPTION(15001, "用户已存在"),
    PHONE_EXIST_EXCEPTION(15002, "手机号已存在"),
    LOGINACCT_PASSWORD_ERROR_EXCEPTION(20000, "账号或密码错误"),
    TOO_MANY_REQUEST(8888, "请求流量过大"),
    NO_STOCK_EXCEPTION(21000, "库存不足");
    private int code;
    private String msg;
    BizCideEnume(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
