package cn.itsource.aigou.service.impl;

import cn.itsource.aigou.domain.Product;
import cn.itsource.aigou.domain.ProductExt;
import cn.itsource.aigou.domain.Sku;
import cn.itsource.aigou.domain.Specification;
import cn.itsource.aigou.mapper.ProductExtMapper;
import cn.itsource.aigou.mapper.ProductMapper;
import cn.itsource.aigou.mapper.SkuMapper;
import cn.itsource.aigou.query.ProductQuery;
import cn.itsource.aigou.service.IProductService;
import cn.itsource.aigou.util.PageList;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.internal.util.unsafe.SpscUnboundedArrayQueue;

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
    private SkuMapper skuMapper;

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
                    sku.setPrice(Integer.valueOf(skuData.get(key).toString()));
                }
                else  if ("stock".equals(key)){
                    sku.setStock(Integer.valueOf(skuData.get(key).toString()));
                }
                else if ("state".equals(key)){
                    Integer state = (Integer) skuData.get(key);
                    sku.setState(state==1?true:false);
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
        return true;
    }
}
