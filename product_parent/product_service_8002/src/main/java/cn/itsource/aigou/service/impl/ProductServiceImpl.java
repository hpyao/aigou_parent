package cn.itsource.aigou.service.impl;
import cn.itsource.aigou.client.PageClient;
import cn.itsource.aigou.controller.SpecificationController;
import cn.itsource.aigou.domain.*;
import cn.itsource.aigou.mapper.*;
import cn.itsource.aigou.service.IProductTypeService;
import cn.itsource.aigou.util.StrUtils;
import com.google.common.collect.Lists;

import cn.itsource.aigou.client.ProductDocClient;
import cn.itsource.aigou.index.ProductDoc;
import cn.itsource.aigou.query.ProductQuery;
import cn.itsource.aigou.service.IProductService;
import cn.itsource.aigou.util.PageList;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.internal.util.unsafe.SpscUnboundedArrayQueue;

import java.io.Serializable;
import java.util.*;

import static javax.swing.UIManager.get;

/**
 * <p>
 * 商品 服务实现类
 * </p>
 *
 * @author yhptest
 * @since 2019-01-18
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductExtMapper productExtMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private ProductTypeMapper productTypeMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private ProductDocClient productDocClient;

    @Override
    public PageList<Product> selectPageList(ProductQuery query) {

        Page<Product> page = new Page<>(query.getPage(),query.getRows());
        List<Product> data =  productMapper.loadPageData(page,query);
        long total = page.getTotal();
        return new PageList<>(total,data);
    }

    @Override
    public void addViewProperties(Long productId, List<Specification> specifications) {
        //fastJson
        String viewProperties = JSONArray.toJSONString(specifications);
        //获取商品
        Product product = productMapper.selectById(productId);
        //设置viewProperties
        product.setViewProperties(viewProperties);
        //修改
        productMapper.updateById(product);
    }

    @Override
    public void addSkus(Long productId, List<Map<String,Object>> skuProperties, List<Map<String,Object>> skuDatas) {
        Product product = productMapper.selectById(productId);
        //skuProperties修改到product
        product.setSkuTemplate(JSONArray.toJSONString(skuProperties));
        productMapper.updateById(product);
        //skuDatas放入sku表
        //1 删除原来的
        EntityWrapper<Sku> wapper = new EntityWrapper<>();
        wapper.eq("productId", productId);
        skuMapper.delete(wapper);

        //2 插入新的
        Map<String,Object> otherProp = new HashMap<>();
        for (Map<String, Object> skuData : skuDatas) {
            Sku sku = new Sku();
            //处理了四个字段
            sku.setProductId(productId);
            for (String key : skuData.keySet()) {
                //price,stock,state是直接传递进来
                if ("price".equals(key)){
                    sku.setPrice(Integer.valueOf(skuData.get(key).toString())*100);
                }
                else  if ("stock".equals(key)){
                    sku.setStock(Integer.valueOf(skuData.get(key).toString()));
                }
                else if ("state".equals(key)){
                    Boolean state = (boolean) skuData.get(key);
                    sku.setState(state);
                }else{
                    //others 升高 三维
                    otherProp.put(key, skuData.get(key));
                }

            }
            //处理其他值
            List<Map<String,Object>> tmps = new ArrayList<>();
            for (String key : otherProp.keySet()) {
                Map<String,Object> map = new HashMap<>();
                String properName = key;
                Long properId = getProId(skuProperties,properName);//TODO
                Object proValue = otherProp.get(key);//TODO
                map.put("id", properId);
                map.put("key", properName);
                map.put("value", proValue);
                tmps.add(map);
            }
            String skuValues = JSONArray.toJSONString(tmps);
            sku.setSkuValues(skuValues);

            //indexDexs 1_2_3
            StringBuilder sb = new StringBuilder();
            for (Map<String, Object> map : tmps) {
                Long id = (Long) map.get("id"); //1 定位是哪个属性
                String value = String.valueOf(map.get("value").toString()) ; //定位是哪个选项
                Integer index = getIndex(skuProperties,id,value);
                sb.append(index).append("_");
            }
            String indexDexs =  sb.toString();
            indexDexs=  indexDexs.substring(0, indexDexs.lastIndexOf("_"));
            sku.setIndexs(indexDexs);
            skuMapper.insert(sku);
        }
    }

    @Override
    public List<Sku> querySkus(Long productId) {
        Wrapper<Sku> w = new EntityWrapper<>();
        w.eq("productId", productId);
        return   skuMapper.selectList(w);
    }

    @Override
    public void onSale(String ids, Integer onSale) {
        List<Long> idsLong = StrUtils.splitStr2LongArr(ids);
        if (1==onSale.intValue()){
            //上架
            //数据库状态和上架时间要修改 id 时间
            Map<String,Object> params = new HashMap<>();
            params.put("ids", idsLong);
            params.put("timeStamp", new Date().getTime());
            productMapper.onSale(params);
            //添加esku
            // productDocClient.batchDel(Arrays.asList(idsLong));
            List<ProductDoc> productDocs = product2productDocs(idsLong);
            productDocClient.batchSave(productDocs);
            //生成静态页面
            staticDetailPage(productDocs);
        }else{
            //下架
            //数据库状态和下架时间要修改
            Map<String,Object> params = new HashMap<>();
            params.put("ids", idsLong);
            params.put("timeStamp", new Date().getTime());
            productMapper.offSale(params);
            //删除esku
            productDocClient.batchDel(idsLong);

        }

    }

    @Autowired
    private PageClient pageClient;
    @Autowired
    private IProductTypeService productTypeService;
    private void staticDetailPage(List<ProductDoc> productDocs) {
        for (ProductDoc productDoc : productDocs) {
            //在静态化主页
            staticDetaiPage(productDoc);
        }

    }

    private void staticDetaiPage(ProductDoc productDoc) {
        Map<String,Object> IndexParams = new HashMap<>();
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("staticRoot", "E:\\openSource\\IdeaProjects\\aigou_parent\\product_parent\\product_service_8002\\src\\main\\resources\\");
        //面包屑
        Long prouductTypeId = productDoc.getProuductTypeId();
        List<Map<String, Object>> crumbs = productTypeService
                .getCrumbs(prouductTypeId);
        modelMap.put("crumbs",crumbs );
        //商品
        modelMap.put("product",productDoc );
        //规格参数
        modelMap.put("viewProperties", JSONArray
                .parseArray(productDoc.getViewProperties(), Specification.class));
        //详情
        ProductExt productExt = productExtMapper.selectList(new EntityWrapper<ProductExt>()
                .eq("productId", productDoc.getId())).get(0);
        modelMap.put("productExt",productExt);

        //sku选项
        modelMap.put("skuOptions", JSONArray
                .parseArray(productDoc.getSkuProperties(), Specification.class));
        modelMap.put("skuOptionStrs", productDoc.getSkuProperties());

        //skuString返回,用于缓存到界面.
        List<Sku> skus = skuMapper.selectList(new EntityWrapper<Sku>()
                .eq("productId", productDoc.getId()));
        skus.forEach(sku->sku.setSkuValues(null)); //把skuValues都设置为null,前台处理不了.
        modelMap.put("skus", JSONArray.toJSONString(skus));

        IndexParams.put("model",modelMap );
        IndexParams.put("tmeplatePath","E:\\openSource\\IdeaProjects\\aigou_parent\\product_parent\\product_service_8002\\src\\main\\resources\\template\\detail\\product-detail.vm" );
        IndexParams.put("staticPagePath","E:\\openSource\\IdeaProjects\\aigou_web_parent\\aigou_shopping\\pages\\"+productDoc.getId()+".html" );
        pageClient.genStaticPage(IndexParams);
    }

    //获取某个属性选项索引值

    /**
     *  从所有的属性(里面包含选项)拿到某个属性某个选项索引值.
     * @param skuProperties 所有的属性(里面包含选项)
     * @param proId 要获取哪个属性
     * @param value 哪个选项
     * @return
     */
    private Integer getIndex(List<Map<String, Object>> skuProperties, Long proId, String value) {

        for (Map<String, Object> skuProperty : skuProperties) {
            Long proIdTmp = Long.valueOf(skuProperty.get("id").toString());
            if (proIdTmp.longValue() == proId.longValue()){
                //找到属性的选项
                List<String> skuValues = (List<String>) skuProperty.get("skuValues");
                int index = 0;
                for (String skuValue : skuValues) {
                    if(skuValue.equals(value)){
                        return index;
                    }
                    index++;
                }
            }

        }
        return null;
    }

    private Long getProId(List<Map<String,Object>> skuProperties, String properName) {
        for (Map<String,Object> skuProperty : skuProperties) {
            Long spId = Long.valueOf(skuProperty.get("id").toString()) ;
            String spName = (String) skuProperty.get("name");
            if (properName.equals(spName)){
                return spId;
            }
        }
        return null;
    }

    @Override
    public boolean insert(Product entity) {
        //添加本表信息以外,还要存放关联表
        entity.setCreateTime(new Date().getTime());
        productMapper.insert(entity);

        System.out.println(entity.getId());
        if (entity.getProductExt() != null) {
            entity.getProductExt().setProductId(entity.getId());
            productExtMapper.insert(entity.getProductExt());
        }
        return true;
    }
    @Override
    public boolean updateById(Product entity) {
        //添加本表信息以外,还要存放关联表
        entity.setUpdateTime(new Date().getTime());
        productMapper.updateById(entity);
        //通过productId查询productExt
        Wrapper<ProductExt> wrapper = new EntityWrapper<ProductExt>()
                .eq("productId", entity.getId());
        ProductExt productExt = productExtMapper.selectList(wrapper).get(0);
        //把前台传递进来值设置给数据库查询出来值,并且把它修改进去
        ProductExt tmp = entity.getProductExt();
        if ( tmp!= null) {
            productExt.setDescription(tmp.getDescription());
            productExt.setRichContent(tmp.getRichContent());
            productExtMapper.updateById(productExt);
        }
        // 如果是上架状态,要同步修改es库
        if (entity.getState()==1){
            ProductDoc productDoc = product2productDoc(entity);
            productDocClient.save(productDoc);
        }

        return true;
    }

    @Override
    public boolean deleteById(Serializable id) {
        super.deleteById(id);
        //如果是上架状态,要同步删除es库
        Product product = productMapper.selectById(id);
        if (product.getState()==1){
            productDocClient.del(Long.valueOf(id.toString()));
        }
        return true;
    }

    /**
     * 转换多个
     * @param ids
     * @return
     */
    private List<ProductDoc> product2productDocs(List<Long> ids) {
        List<ProductDoc> productDocs = new ArrayList<>();
        for (Long id : ids) {
            Product product = productMapper.selectById(id);
            ProductDoc productDoc = product2productDoc(product);
            productDocs.add(productDoc);
        }
        return productDocs;
    }

    /**
     * 转换一个
     * @param product
     * @return
     */
    private ProductDoc product2productDoc(Product product) {

        //选中 alt+enter
        ProductDoc productDoc = new ProductDoc();
        productDoc.setId(product.getId());
        productDoc.setName(product.getName());
        productDoc.setProuductTypeId(product.getProductTypeId());
        productDoc.setBrandId(product.getBrandId());
        //从某个商品sku中获取最大或最小
        List<Sku> skus = skuMapper.selectList(new EntityWrapper<Sku>()
                .eq("productId", product.getId()));

        Integer minPrice  = skus.get(0).getPrice();
        Integer maxPrice  = skus.get(0).getPrice();
        for (Sku sku : skus) {
            if (sku.getPrice()<minPrice) minPrice=sku.getPrice();
            if (sku.getPrice()>maxPrice) maxPrice = sku.getPrice();
        }
        productDoc.setMinPrice(minPrice);
        productDoc.setMaxPrice(maxPrice);
        productDoc.setSaleCount(product.getSaleCount());
        productDoc.setOnSaleTime(product.getOnSaleTime().intValue());
        productDoc.setCommentCount(product.getCommentCount());
        productDoc.setViewCount(product.getViewCount());
        String medias = product.getMedias();
        if (StringUtils.isNotBlank(medias)) {
            productDoc.setImages(Arrays
                    .asList(medias.split(",")));
        }
        Brand brand = brandMapper.selectById(product.getBrandId());
        ProductType productType = productTypeMapper.selectById(product.getProductTypeId());
        //投机-有空格就会分词
        String all = product.getName()+" "
                +product.getSubName()+" "+brand.getName()+" "+productType.getName();

        productDoc.setAll(all);
        productDoc.setViewProperties(product.getViewProperties());
        productDoc.setSkuProperties(product.getSkuTemplate());
        //设置值
        return productDoc;
    }
}
