package cn.itsource.aigou.service;

import cn.itsource.aigou.domain.Brand;
import cn.itsource.aigou.query.BrandQuery;
import cn.itsource.aigou.util.PageList;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 * 品牌信息 服务类
 * </p>
 *
 * @author yhptest
 * @since 2019-01-13
 */
public interface IBrandService extends IService<Brand> {

    /**
     * 分页查询,关联对象
     * @param query
     * @return
     */
    PageList<Brand> selectPageList(BrandQuery query);
}
