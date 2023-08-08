package com.xyj.common.exception;

/**
 * @Author jie
 * @Date 2023/8/1 11:08
 */
public class NoStockException extends RuntimeException {
    private Long skuId;
    public NoStockException(Long skuId) {
        super("商品ID:" + skuId + "没有足够库存");
    }
    public NoStockException() {
        super("没有足够库存");
    }
    public Long getSkuId(){
        return skuId;
    }
    public void setSkuId(Long skuId){
        this.skuId = skuId;
    }
}
