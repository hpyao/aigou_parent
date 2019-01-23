package cn.itsource.aigou.service;

import cn.itsource.aigou.domain.Brand;
import cn.itsource.aigou.domain.ProductType;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 商品目录 服务类
 * </p>
 *
 * @author yhptest
 * @since 2019-01-13
 */
public interface IProductTypeService extends IService<ProductType> {

    /**
     * 获取无限极数据
     * @return
     */
    List<ProductType> treeData();

    /**
     * 获取面包屑
     * @param productTypeId
     * @return
     */
    List<Map<String,Object>> getCrumbs(Long productTypeId);

    List<Brand> getBrands(Long productTypeId);

    Set<String> getLetters(Long productTypeId);
}
