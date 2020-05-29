package com.changgou.search.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/***
 * 目标  主要是通过spring data elasticsearch的注解来建立以下
 *  1 需要创建 索引
 *  2.创建类型
 *  3.创建文档的唯一标识
 *  4.建立映射
 *
 *
 *  @Document(indexName = "skuinfo",type = "docs")
 *
 *       document 注解标识为该POJO 要映射到es中的文档JSON
 *
 *       indexName 指定索引的名称   type 指定索引中的类型名称
 *
 *   @Id  用于标识文档的唯一标识
 *
 *
 *   @Field(type = FieldType.Text, analyzer = "ik_smart",index = true,store = false,searchAnalyzer = "ik_smart")
 *
 *        @feid 用于标识映射到es中的字段
 *
 *        type 指定 该数据类型是什么
 *
 *        index: 指定是否索引   默认是true  要索引
 *
 *        store :指定是否存储  默认是false  不存储
 *
 *        analyzer: 指定如果要分词 ，使用的创建倒排索引表的时候的那个分词器
 *
 *        searchAnalyzer :指定是使用的分词器 搜索的时候的分词器 ，一般两个属性配置的是同一个分词器。 这个可以不配置。
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 * @author ljh
 * @packagename com.changgou.search.pojo
 * @version 1.0
 * @date 2020/5/10
 */
@Document(indexName = "skuinfo",type = "docs")
public class SkuInfo implements Serializable {



    //商品id，同时也是商品编号  文档的唯一标识一定要有 同时可以为主键也可以为文档的唯一标识
    @Id
    private Long id;

    //SKU名称
    @Field(type = FieldType.Text, analyzer = "ik_smart",index = true,store = false,searchAnalyzer = "ik_smart")
    private String name;

    //商品价格，单位为：元
    @Field(type = FieldType.Double)
    private Long price;

    //库存数量
    private Integer num;

    //商品图片
    private String image;

    //商品状态，1-正常，2-下架，3-删除
    private String status;

    //创建时间
    private Date createTime;

    //更新时间
    private Date updateTime;

    //是否默认
    private String isDefault;

    //SPUID
    private Long spuId;

    //类目ID
    private Long categoryId;

    //类目名称  不分词的
    // Keyword 就是不分词
    @Field(type = FieldType.Keyword)
    private String categoryName;




    //品牌名称
    @Field(type = FieldType.Keyword)
    private String brandName;

    //规格
    private String spec;
    //private String 网络制式;//网络制式字段

    //private String 显示屏幕尺寸;//网络制式字段

    //已经上线了然后赶紧下线 改源码 上线

    //规格参数
    private Map<String,Object> specMap;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public Long getSpuId() {
        return spuId;
    }

    public void setSpuId(Long spuId) {
        this.spuId = spuId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public Map<String, Object> getSpecMap() {
        return specMap;
    }

    public void setSpecMap(Map<String, Object> specMap) {
        this.specMap = specMap;
    }
}
