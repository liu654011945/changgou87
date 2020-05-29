package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.SkuEsMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SkuService;
import entity.Result;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.search.service.impl
 * @version 1.0
 * @date 2020/5/10
 */
@Service
public class SkuServiceImpl implements SkuService {


    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private SkuEsMapper skuEsMapper;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;


    @Override
    public void importSku() {
        //1.调用商品微服务的feign 查询出符合条件的sku的数据
        //1.1 定义一个feign
        //1.2 添加feign的依赖
        //1.3 声明启用feign
        //1.4 调用
        Result<List<Sku>> byStatus = skuFeign.findByStatus("1");//获取正常的商品的列表
        List<Sku> data = byStatus.getData();//sku列表

        //2.调用spring data elasticsearch 的API 将数据导入到ES中
        //2.1 创建repository(dao)
        //2.2.调用方法保存数据到es中
        //将List<Sku> 转换给 List<SkuInfo>
        List<SkuInfo> skuInfoList = JSON.parseArray(JSON.toJSONString(data), SkuInfo.class);

        for (SkuInfo skuInfo : skuInfoList) {
            String spec = skuInfo.getSpec();//{"电视音响效果":"立体声","电视屏幕尺寸":"20英寸","尺码":"170"}
            Map<String, Object> specMap = JSON.parseObject(spec, Map.class);
            skuInfo.setSpecMap(specMap);//有值了
        }


        skuEsMapper.saveAll(skuInfoList);
    }

    //执行实现关键字搜索的功能
    @Override
    public Map search(Map<String, String> searchMap) {


        //0 从页面中获取要搜索的内容关键字
        String keywords = searchMap.get("keywords");

        if (StringUtils.isEmpty(keywords)) {//如果为空
            keywords = "华为";
        }

        //1.创建一个查询对象的 构建对象
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();


        //1.1 设置分组查询的条件   商品分类的分组   添加聚合函数
        // // terms 指定的就是聚合函数的类型 ： 分组查询
        // 设置别名 skuCategorygroup  用于设置分组查询的别名
        //设置分组的字段 categoryName
        // 设置展示的数据长度 5000
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuCategorygroup").field("categoryName").size(5000));

        //1.2 设置分组查询   品牌的分组
        // 设置分组的字段 brandName
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuBrandgroup").field("brandName").size(5000));


        //  1.3 设置分组查询   规格的分组 select spec from tb_sku where name like  '%立体声%' GROUP BY spec;
        // spec.keyword  ?????黑人疑问号

        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuSpecgroup").field("spec.keyword").size(5000));




        //1.4 设置高亮 设置高亮的字段 设置高亮的前缀和后缀

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<em style='color:red'>").postTags("</em>");
        nativeSearchQueryBuilder.withHighlightFields(new HighlightBuilder.Field("name")).withHighlightBuilder(highlightBuilder);


        //2.设置查询的条件  匹配查询 从哪个索引 哪一个类型中查询 不指定就查询所有的索引和所有的类型
        nativeSearchQueryBuilder.withQuery(QueryBuilders.matchQuery("name", keywords));//匹配查询 特点：先分词再查询


        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();


        //2.1 多条件组合过滤查询  商品分类过滤
        String category = searchMap.get("category");
        if (!StringUtils.isEmpty(category)) {
            //must必须要满足  must  must_not  should    filter(就是一定要满足)
            boolQueryBuilder.filter(QueryBuilders.termQuery("categoryName", category));//必须满足 词条查询
        }
        //2.2 多条件组合过滤查询  商品品牌过滤
        String brand = searchMap.get("brand");
        if (!StringUtils.isEmpty(brand)) {
            // 必须要满足
            boolQueryBuilder.filter(QueryBuilders.termQuery("brandName", brand));//必须满足 词条查询
        }

        //2.3 多条件组合过滤查询  规格的过滤查询

        for (String key : searchMap.keySet()) {
            if (key.startsWith("spec_")) {//就是规格相关的
                // specMap.规格名.keyword:规格选项值
                boolQueryBuilder.filter(QueryBuilders.termQuery("specMap." + key.substring(5) + ".keyword", searchMap.get(key)));//必须满足 词条查询
            }
        }

        //2.4 多条件组合过滤查询  价格的范围查询  500-1000   3000-*
        String price = searchMap.get("price");
        if (!StringUtils.isEmpty(price)) {
            String[] split = price.split("-");
            //3000快以上 就是大于=
            if (split[1].equals("*")) {
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(split[0]));//必须满足 范围查询
            } else {
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(split[0]).lte(split[1]));//必须满足 范围查询
            }
        }
        nativeSearchQueryBuilder.withFilter(boolQueryBuilder);//过滤查询


        //2.5 设置排序
        String sortField = searchMap.get("sortField");//price
        String sortRule = searchMap.get("sortRule");//DESC/ ASC

        if(!StringUtils.isEmpty(sortField) && !StringUtils.isEmpty(sortRule)) {
            ////设置排序 order by price desc
            nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(sortField).order(SortOrder.valueOf(sortRule)));
        }

        //2.6 分页

        //参数1 标识当前页码  0 表示第一页
        //参数2 标识每页显示行 默认写成 40
        String pageNumStr = searchMap.get("pageNum");
        //默认是1
        Integer pageNum=1;
        Integer pageSize=40;

        if(!StringUtils.isEmpty(pageNumStr)){
            pageNum = Integer.parseInt(pageNumStr);
        }
        Pageable pageale = PageRequest.of(pageNum-1,pageSize);



        nativeSearchQueryBuilder.withPageable(pageale);//用于设置分页的条件



        //3.构建查询对象
        SearchQuery query = nativeSearchQueryBuilder.build();
        //4.执行查询动作
        // SearchResultMapper 如果使用了自定义的SearchResultMapper 那么就不会走默认的映射，
        AggregatedPage<SkuInfo> skuInfos = elasticsearchTemplate.queryForPage(query, SkuInfo.class,new SearchResultMapperImpl());



        //获取高亮的数据

        //5.获取结果
        List<SkuInfo> content = skuInfos.getContent();//获取当前页的所有的记录
        long totalElements = skuInfos.getTotalElements();//获取总记录数
        int totalPages = skuInfos.getTotalPages();//获取总页数


        // ctr +alt +M
        //5.1 获取分组的结果集  分类的
        List<String> categoryList = getStringsTermsList(skuInfos, "skuCategorygroup");

        //5.2 获取分组的结果集  品牌的
        List<String> brandList = getStringsTermsList(skuInfos, "skuBrandgroup");

        //5.3 获取分组的结果集  规格的列表数据   [一顿操作] 出现了一个map

        StringTerms skuSpecgroupTerms = (StringTerms) skuInfos.getAggregation("skuSpecgroup");

        Map<String, Set<String>> specMap = getStringSetMap(skuSpecgroupTerms);


        //6.设置结果封装对象返回
        Map<String, Object> resultMap = new HashMap<String, Object>();

        resultMap.put("rows", content);//当前页的结果集
        resultMap.put("total", totalElements);//总记录数
        resultMap.put("totalPages", totalPages);//总页数
        resultMap.put("categoryList", categoryList);//设置分类的列表数据
        resultMap.put("brandList", brandList);//设置商品品牌的列表数据
        resultMap.put("specMap", specMap);//设置规格列表的数据返回给前端
        resultMap.put("pageNum", pageNum);
        resultMap.put("pageSize", pageSize);
        return resultMap;
    }

    //一顿操作的方法 ???
    private Map<String, Set<String>> getStringSetMap(StringTerms stringTermsSpec) {


        // Map<String, Set<String>>        {    "电视音响效果" :["立体声","环绕"],"电视屏幕尺寸":["20","30"]       }
        Map<String, Set<String>> specMap = new HashMap<String, Set<String>>();

        if (stringTermsSpec != null) {
            List<StringTerms.Bucket> buckets = stringTermsSpec.getBuckets();
            Set<String> values = new HashSet<String>();

            for (StringTerms.Bucket bucket : buckets) {
                //{"电视音响效果":"立体声","电视屏幕尺寸":"20英寸","尺码":"165"}
                // {"电视音响效果":"立体声","电视屏幕尺寸":"20英寸","尺码":"170"}
                String keyAsString = bucket.getKeyAsString();
                Map<String, String> map = JSON.parseObject(keyAsString, Map.class);
                for (Map.Entry<String, String> stringStringEntry : map.entrySet()) {
                    String key = stringStringEntry.getKey();//  电视音响效果
                    String value = stringStringEntry.getValue(); // 立体声
                    //先从map中获取Key对应的值也就是旧的set集合
                    values = specMap.get(key);
                    if (values == null) {
                        values = new HashSet<String>();
                    }
                    values.add(value);
                    specMap.put(key, values);

                }
            }
        }
        return specMap;
    }

    private List<String> getStringsTermsList(AggregatedPage<SkuInfo> skuInfos, String group) {
        StringTerms groupterms = (StringTerms) skuInfos.getAggregation(group);

        List<String> list = new ArrayList<String>();
        if (groupterms != null) {
            for (StringTerms.Bucket bucket : groupterms.getBuckets()) {
                String keyAsString = bucket.getKeyAsString();//就是分类的名称
                list.add(keyAsString);
            }
        }
        return list;
    }
}















































































































































































































