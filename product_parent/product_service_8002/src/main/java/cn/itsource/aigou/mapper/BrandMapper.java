package cn.itsource.aigou.mapper;

import cn.itsource.aigou.domain.Brand;
import cn.itsource.aigou.query.BrandQuery;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;

import java.util.List;

/**
 * <p>
 * 品牌信息 Mapper 接口
 * </p>
 *
 * @author yhptest
 * @since 2019-01-13
 */
public interface BrandMapper extends BaseMapper<Brand> {

    /**
     * 查询分页数据
     * @param page
     * @param query
     * @return
     */
    List<Brand> selectPageList(Page<Brand> page, BrandQuery query);
}
