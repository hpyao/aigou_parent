package cn.itsource.aigou.service.impl;

import cn.itsource.aigou.client.PageClient;
import cn.itsource.aigou.client.RedisClient;
import cn.itsource.aigou.domain.ProductType;
import cn.itsource.aigou.mapper.ProductTypeMapper;
import cn.itsource.aigou.service.IProductTypeService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import feign.Retryer;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品目录 服务实现类
 * </p>
 *
 * @author yhptest
 * @since 2019-01-13
 */
@Service
public class ProductTypeServiceImpl extends ServiceImpl<ProductTypeMapper, ProductType> implements IProductTypeService {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private PageClient pageClient;
    @Autowired
    private ProductTypeMapper productTypeMapper;
    @Override
    public List<ProductType> treeData()
    {
        String productTypeInRedis = redisClient.get("productType_in_redis");
        if (StringUtils.isNotBlank(productTypeInRedis))
        {
            System.out.println("cache....");
            return JSONArray.parseArray(productTypeInRedis, ProductType.class);
        }else{

            System.out.println("db....jjjjjjjjjjjjjjjjjj");
            // 1 递归方案效率低,要发多次sql
            //return getTreeDataRecursion(0L);
            // 2 循环方案,只需发一条sql
            List<ProductType> treeDataByDb = getTreeDataLoop(0L);
            redisClient.set("productType_in_redis", JSONArray.toJSONString(treeDataByDb, SerializerFeature.WriteMapNullValue));
            return treeDataByDb;
        }


    }


    //增删改已经不是传统的了,要做同步redis-清空缓存,下次查询时,自动查询数据库
    @Override
    public boolean insert(ProductType entity) {

        //方案1:
        //redisClient.set("productType_in_redis", "");
        //方案2:为了配置页面静态,这种方案还要好一点
        super.insert(entity); //先做增删改,再做同步
        synchronizedOpr();
        return true;
    }

    @Override
    public boolean deleteById(Serializable id) {
        //redisClient.set("productType_in_redis", "");
        super.deleteById(id);
        synchronizedOpr();
        return true;
    }

    @Override
    public boolean updateById(ProductType entity) {
        //redisClient.set("productType_in_redis", "");
        super.updateById(entity);
        synchronizedOpr();
        return true;
    }


    //增删改让商品类型发生改变,都要重新生成静态页面还要更新缓存
    private void synchronizedOpr(){
        //更新缓存
        List<ProductType> allProductType = getTreeDataLoop(0L);
        redisClient.set("productType_in_redis", JSONArray.toJSONString(allProductType));
        //是否每次都要从数据库查询---是的,就可以优化原来代码,同步修改缓存. 就不用查询的时候从数据库获取
        //先静态化类型
        Map<String,Object> productTypeParams = new HashMap<>();
        Object model = null;
        productTypeParams.put("model",allProductType );
        productTypeParams.put("tmeplatePath","E:\\openSource\\IdeaProjects\\aigou_parent\\product_parent\\product_service_8002\\src\\main" +
                "\\resources\\template\\productType\\product.type.vm" );
        productTypeParams.put("staticPagePath","E:\\openSource\\IdeaProjects\\aigou_parent\\product_parent" +
                "\\product_service_8002\\src\\main\\resources\\template\\productType\\product.type.vm.html" );
        pageClient.genStaticPage(productTypeParams);
        //在静态化主页
        Map<String,Object> IndexParams = new HashMap<>();
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("staticRoot", "E:\\openSource\\IdeaProjects\\aigou_parent\\product_parent\\product_service_8002\\src\\main\\resources\\");
        IndexParams.put("model",modelMap );
        IndexParams.put("tmeplatePath","E:\\openSource\\IdeaProjects\\aigou_parent\\product_parent\\product_service_8002\\src\\main\\resources\\template\\home.vm" );
        IndexParams.put("staticPagePath","E:\\openSource\\IdeaProjects\\aigou_web_parent\\aigou_shopping\\home.html" );
        pageClient.genStaticPage(IndexParams);

    }

    private List<ProductType> getTreeDataLoop(long l) {
        //返回数据 一级类型,下面挂了子子孙孙类型
        List<ProductType> result = new ArrayList<>();
        //1 获取所有的类型
        List<ProductType> productTypes = productTypeMapper.selectList(null);
        //2)遍历所有的类型
        Map<Long,ProductType> productTypesDto = new HashMap<>();
        for (ProductType productType : productTypes) {
            productTypesDto.put(productType.getId(), productType);
        }
        for (ProductType productType : productTypes) {
            Long pid = productType.getPid();
            // ①如果没有父亲就是一级类型 放入返回列表中
            if (pid.longValue() == 0){
                result.add(productType);
            }else{
                // ②如果有父亲就是把自己当做一个儿子就ok
                //方案1:遍历比较所有所有里面获取 两层for 10*10
//                for (ProductType productTypeTmp : productTypes) { 1 10 2 10 310 40 10
//                    if (productTypeTmp.getId()==pid){
//                        productTypeTmp.getChildren().add(productType);
//                    }
//                }
                //方案2:通过Map建立id和类型直接关系,以后通过pid直接获取父亲 10+10
                ProductType parent = productTypesDto.get(pid);
                parent.getChildren().add(productType);
            }

        }
        return result;
    }

    /**
     * 递归获取无限极数据
     *    ①自己调用自己
     *    ②要有出口
     * @return
     */
    private List<ProductType> getTreeDataRecursion(Long id) {

        //0
        List<ProductType> children = getAllChildren(id); //1 2
        //出口
        if (children == null || children.size()<1){
            return null;
        }
        for (ProductType productType : children) {
            //1 3 4 自己调用自己
            List<ProductType> children1 = getTreeDataRecursion(productType.getId());
            productType.setChildren(children1);
        }
        return children;

    }

    private List<ProductType> getAllChildren(Long pid){
        Wrapper wrapper = new EntityWrapper<ProductType>();
        wrapper.eq("pid", pid);
        return productTypeMapper.selectList(wrapper);
    }

}
