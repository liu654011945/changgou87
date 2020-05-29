package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.search.pojo.SkuInfo;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.search.service.impl
 * @version 1.0
 * @date 2020/5/12
 */
public class SearchResultMapperImpl implements SearchResultMapper {
    //   自定义数据映射 目的就是为了【手动】要获取高亮的数据返回给页面
    @Override
    public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
        //目的就是为了获取[高亮]的数据


        //1.设置获取content(搜索的当前的记录集合)
        List<T> content = new ArrayList<T>();//该content 是没有高亮的
        //2.设置获取分页信息

        //3.设置获取总记录数
        SearchHits hits = response.getHits();

        if (hits == null || hits.getTotalHits() <= 0) {
            return new AggregatedPageImpl<T>(content);//空
        }
        long totalHits = hits.getTotalHits();

        //获取高亮的数据
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();//一行记录 JSON 就是商品POJO
            SkuInfo skuInfo = JSON.parseObject(sourceAsString, SkuInfo.class);//没有高亮的数据

            //需要获取高亮的值，将值替换掉原来的name的值 再返回
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            StringBuffer sb = new StringBuffer();
            if(highlightFields!=null
                    && highlightFields.size()>0
                    && highlightFields.get("name")!=null
                    && highlightFields.get("name").getFragments()!=null) {

                HighlightField highlightField = highlightFields.get("name");
                Text[] fragments = highlightField.getFragments();//高亮碎片

                for (Text fragment : fragments) {
                    String string = fragment.string();//高亮过 的数据
                    sb.append(string);
                }
            }

            //如果sb的值长度>0 才有必要替换
            if(sb.toString().length()>0) {
                skuInfo.setName(sb.toString());//将高亮数据替换到没有高亮的数据
            }
            content.add((T)skuInfo);
        }


        //4.设置聚合函数集 分组的结果.....
        Aggregations aggregations = response.getAggregations();
        //5.设置scrollid
        String scrollId = response.getScrollId();

        return new AggregatedPageImpl<T>(content, pageable, totalHits, aggregations, scrollId);
    }
}
