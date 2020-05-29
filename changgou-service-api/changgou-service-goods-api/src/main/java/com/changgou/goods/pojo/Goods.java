package com.changgou.goods.pojo;

import java.io.Serializable;
import java.util.List;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.goods.pojo
 * @version 1.0
 * @date 2020/5/8
 */
public class Goods implements Serializable {
    private Spu spu;//spu数据
    private List<Sku> skuList;//sku列表的数据

    public Spu getSpu() {
        return spu;
    }

    public void setSpu(Spu spu) {
        this.spu = spu;
    }

    public List<Sku> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<Sku> skuList) {
        this.skuList = skuList;
    }
}
