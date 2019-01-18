package cn.itsource.aigou.service.impl;

import cn.itsource.aigou.domain.Product;
import cn.itsource.aigou.domain.ProductExt;
import cn.itsource.aigou.mapper.ProductExtMapper;
import cn.itsource.aigou.mapper.ProductMapper;
import cn.itsource.aigou.mapper.ProductTypeMapper;
import cn.itsource.aigou.query.ProductQuery;
import cn.itsource.aigou.service.IProductService;
import cn.itsource.aigou.util.PageList;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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

    @Override
    public PageList<Product> selectPageList(ProductQuery query) {

        Page<Product> page = new Page<>(query.getPage(),query.getRows());
        List<Product> data =  productMapper.loadPageData(page,query);
        long total = page.getTotal();
        return new PageList<>(total,data);
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
