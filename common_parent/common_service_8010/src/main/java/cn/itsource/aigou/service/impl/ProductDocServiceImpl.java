package cn.itsource.aigou.service.impl;
import com.google.common.collect.Lists;

import cn.itsource.aigou.index.ProductDoc;
import cn.itsource.aigou.repository.ProductDocRepository;
import cn.itsource.aigou.service.IProductDocService;
import cn.itsource.aigou.util.PageList;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import sun.applet.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductDocServiceImpl implements IProductDocService{
    @Autowired
    private ProductDocRepository repository;

    @Override
    public void add(ProductDoc productDoc) {
        repository.save(productDoc);
    }

    @Override
    public void del(Long id) {
        repository.deleteById(id);
    }

    @Override
    public ProductDoc get(Long id) {
        return  repository.findById(id).get();
    }

    @Override
    public void batchAdd(List<ProductDoc> productDocs) {

        repository.saveAll(productDocs);
    }

    @Override
    public void batchDel(List<Long> ids) {

        for (Long id : ids) {
            repository.deleteById(id);
        }
    }

    /**
     *  /**
     * GET test/employee/_search
     {
     "query": {
     "bool": {
     "must": [
     {
     "  match_all": {

     }
     }
     ],
     "filter": {
     "range": {
     "age": {
     "gte": 10,
     "lte": 80
     }
     }
     }
     }
     },
     "sort": [
     {
     "age": {
     "order": "desc"
     }
     }
     ],
     "from": 0,
     "size": 2,
     "_source": ["name","age"]
     }

     * @param params
     * @return
     */
    @Override
    public PageList<Map<String,Object>> search(Map<String, Object> params) {

       // keyword productyType brandId priceMin priceMax sortField sortType page rows
        String keyword = (String) params.get("keyword"); //查询
        String sortField = (String) params.get("sortField"); //排序
        String sortType = (String) params.get("sortType");//排序

        Long productType = params.get("productType") !=null?Long.valueOf(params.get("productType").toString()):null;//过滤
        Long brandId = params.get("brandId") !=null?Long.valueOf(params.get("brandId").toString()):null;//过滤
        Long priceMin = params.get("priceMin") !=null?Long.valueOf(params.get("priceMin").toString())*100:null;//过滤
        Long priceMax = params.get("priceMax") !=null?Long.valueOf(params.get("priceMax").toString())*100:null;//过滤
        Long page = params.get("page") !=null?Long.valueOf(params.get("page").toString()):null; //分页
        Long rows = params.get("rows") !=null?Long.valueOf(params.get("rows").toString()):null;//分页

        //构建器
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        //设置查询条件=查询+过滤
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(keyword)){
            boolQuery.must(QueryBuilders.matchQuery("all", keyword));
        }
        List<QueryBuilder> filter = boolQuery.filter();
        if (productType != null){ //类型
            System.out.println(productType+"jjjjjjjjjjjjjjjjjjj");
            filter.add(QueryBuilders.termQuery("prouductTypeId", productType));
        }
        if (brandId != null){ //品牌
            filter.add(QueryBuilders.termQuery("brandId", brandId));
        }
        //最大价格 最小价格
        //minPrice <= priceMax && maxPrice>=priceMin
        if(priceMax!=null){
            filter.add(QueryBuilders.rangeQuery("minPrice").lte(priceMax));
        }
        if(priceMin!=null){
            filter.add(QueryBuilders.rangeQuery("maxPrice").gte(priceMax));
        }

        builder.withQuery(boolQuery);
        //排序
        SortOrder defaultSortOrder = SortOrder.DESC;
        if (StringUtils.isNotBlank(sortField)){//销量 新品 价格 人气 评论
            //如果传入的不是降序改为升序
            if (StringUtils.isNotBlank(sortType) && sortType.equals(SortOrder.DESC)){
                defaultSortOrder = SortOrder.ASC;
            }
            //销量
            if (sortField.equals("xl")){
                builder.withSort(SortBuilders.fieldSort("saleCount").order(defaultSortOrder));
            }
            // 新品
            if (sortField.equals("xp")){
                builder.withSort(SortBuilders.fieldSort("onSaleTime").order(defaultSortOrder));
            }
            // 人气
            if (sortField.equals("rq")){
                builder.withSort(SortBuilders.fieldSort("viewCount").order(defaultSortOrder));
            }
            // 评论
            if (sortField.equals("pl")){
                builder.withSort(SortBuilders.fieldSort("commentCount").order(defaultSortOrder));
            }
            // 价格  索引库有两个字段 最大,最小
            //如果用户按照升序就像买便宜的,就用最小价格,如果用户按照降序想买贵的,用最大价格
            if (sortField.equals("jg")){
                if (SortOrder.ASC.equals(defaultSortOrder)){
                    builder.withSort(SortBuilders.fieldSort("minPrice").order(defaultSortOrder));
                }else{
                    builder.withSort(SortBuilders.fieldSort("maxPrice").order(defaultSortOrder));
                }
            }
        }
        //分页
        Long pageTmp = page-1; //从0开始
        builder.withPageable(PageRequest.of(pageTmp.intValue(), rows.intValue()));
        //截取字段 @TODO
        //封装数据
        Page<ProductDoc> productDocs = repository.search(builder.build());
        List<Map<String,Object>> datas = productDocs2ListMap(productDocs.getContent());
        return new PageList<>(productDocs.getTotalElements(),datas);
    }

    public static void main(String[] args) {
        System.out.println(Long.valueOf(null));
    }

    /**
     * 数据转换
     * @param content
     * @return
     */
    private List<Map<String,Object>> productDocs2ListMap(List<ProductDoc> content) {
         List<Map<String,Object>> result = new ArrayList<>();
        for (ProductDoc productDoc : content)
        {
            result.add(productDoc2Map(productDoc));
        }
        return result;
    }


    private Map<String,Object> productDoc2Map(ProductDoc productDoc) {
        Map<String,Object> result = new HashMap<>();
         result.put("id", productDoc.getId());
         result.put("name", productDoc.getName());
         result.put("productTypeId", productDoc.getProuductTypeId());
         result.put("brandId", productDoc.getBrandId());
         result.put("minPrice", productDoc.getMinPrice());
         result.put("maxPrice", productDoc.getMaxPrice());

         result.put("saleCount", productDoc.getSaleCount());
         result.put("onSaleTime", productDoc.getOnSaleTime());
         result.put("commentCount", productDoc.getCommentCount());
         result.put("viewCount", productDoc.getViewCount());
         result.put("images", productDoc.getImages());
        return result;
    }
}
