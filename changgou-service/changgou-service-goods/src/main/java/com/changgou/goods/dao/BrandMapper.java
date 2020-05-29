package com.changgou.goods.dao;

import com.changgou.goods.pojo.Brand;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/****
 * @Author:admin
 * @Description:Brand的Dao
 * @Date 2019/6/14 0:12
 *****/
public interface BrandMapper extends Mapper<Brand> {

    /**
     * 根据商品分类的id查询品牌列表
     *
     * @param id
     * @return
     */
    @Select(value = "select tbb.* from tb_category_brand tcb,tb_brand tbb where tcb.category_id= #{id}  and tcb.brand_id=tbb.id")
    List<Brand> findBrandByCategory(Integer id);

    List<Brand> selectById();

}
