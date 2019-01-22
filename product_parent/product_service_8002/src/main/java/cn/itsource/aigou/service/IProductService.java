package cn.itsource.aigou.service;

import cn.itsource.aigou.domain.Product;
import cn.itsource.aigou.domain.Sku;
import cn.itsource.aigou.domain.Specification;
import cn.itsource.aigou.query.ProductQuery;
import cn.itsource.aigou.util.PageList;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品 服务类
 * </p>
 *
 * @author yhptest
 * @since 2019-01-18
 */
public interface IProductService extends IService<Product> {

    /**
     * 跨表高级分页
     * @param query
     * @return
     */
    PageList<Product> selectPageList(ProductQuery query);

    /**
     * 添加显示属性
     * @param productId
     * @param specifications
     */
    void addViewProperties(Long productId, List<Specification> specifications);

    /**
     * 添加sku
     * @param productId
     * @param skuProperties
     * @param skuDatas
     */
    void addSkus(Long productId, List<Map<String,Object>> skuProperties, List<Map<String,Object>> skuDatas);

    /**
     * 通过商品id查询sku
     * @param productId
     * @return
     */
    List<Sku> querySkus(Long productId);

    /**
     * 上下架处理
     * @param ids
     * @param onSale
     */
    void onSale(String ids, Integer onSale);
}
