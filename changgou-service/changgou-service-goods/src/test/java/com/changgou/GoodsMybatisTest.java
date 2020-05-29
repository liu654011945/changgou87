package com.changgou;

import com.changgou.goods.dao.BrandMapper;
import com.changgou.goods.pojo.Brand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou
 * @version 1.0
 * @date 2020/5/5
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class GoodsMybatisTest {

    @Autowired
    private BrandMapper brandMapper;

    //查询所有的品牌的类别
    @Test
    public void findAll(){

        List<Brand> brands = brandMapper.selectAll();
        for (Brand brand : brands) {
            System.out.println(brand.getName());
        }
    }
}
