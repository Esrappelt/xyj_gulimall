package com.xyj.gulimall.order.exception;

import com.xyj.common.exception.BizCideEnume;
import com.xyj.common.exception.NoStockException;
import com.xyj.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;


/*
 * 集中处理异常
 * basePackage指定包
 * 使用ExceptionHandler注解进行处理异常
 * RestControllerAdvice就是ResponseBody和ControllerAdvice的合体
 * */
@Slf4j
@RestControllerAdvice(basePackages = "com.xyj.gulimall.order")
public class GulimallExceptionControllerAdvice {
    @ExceptionHandler(value = NoStockException.class) // 捕获异常 发生异常就来到这里 进行统一处理 返回结果
    public R handleValidException(NoStockException e) {
        log.error("数据校验异常{},类型:{}", e.getMessage(), e.getClass());
        return R.error(BizCideEnume.VALID_EXCEPTION.getCode(),BizCideEnume.VALID_EXCEPTION.getMsg()).put("data", e.getMessage());
    }

    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable t) {
        log.debug("异常：{}", t);
        return R.error(t.getMessage());
    }
}
