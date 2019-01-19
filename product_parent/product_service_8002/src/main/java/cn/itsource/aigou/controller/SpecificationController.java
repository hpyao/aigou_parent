package cn.itsource.aigou.controller;

import cn.itsource.aigou.domain.Product;
import cn.itsource.aigou.service.IProductService;
import cn.itsource.aigou.service.ISpecificationService;
import cn.itsource.aigou.domain.Specification;
import cn.itsource.aigou.query.SpecificationQuery;
import cn.itsource.aigou.util.AjaxResult;
import cn.itsource.aigou.util.PageList;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/specification")
public class SpecificationController {
    @Autowired
    public ISpecificationService specificationService;

    @Autowired
    private IProductService productService;

    /**
    * 保存和修改公用的
    * @param specification  传递的实体
    * @return Ajaxresult转换结果
    */
    @RequestMapping(value="/save",method= RequestMethod.POST)
    public AjaxResult save(@RequestBody Specification specification){
        try {
            if(specification.getId()!=null){
                specificationService.updateById(specification);
            }else{
                specificationService.insert(specification);
            }
            return AjaxResult.me();
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.me().setMessage("保存对象失败！"+e.getMessage());
        }
    }

    /**
    * 删除对象信息
    * @param id
    * @return
    */
    @RequestMapping(value="/{id}",method=RequestMethod.DELETE)
    public AjaxResult delete(@PathVariable("id") Long id){
        try {
            specificationService.deleteById(id);
            return AjaxResult.me();
        } catch (Exception e) {
        e.printStackTrace();
            return AjaxResult.me().setMessage("删除对象失败！"+e.getMessage());
        }
    }

    //获取用户
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public Specification get(@PathVariable("id")Long id)
    {
        return specificationService.selectById(id);
    }


    /**
    * 查看所有的员工信息
    * @return
    */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public List<Specification> list(){

        return specificationService.selectList(null);
    }


    /**
    * 分页查询数据
    *
    * @param query 查询对象
    * @return PageList 分页对象
    */
    @RequestMapping(value = "/json",method = RequestMethod.POST)
    public PageList<Specification> json(@RequestBody SpecificationQuery query)
    {
        Page<Specification> page = new Page<Specification>(query.getPage(),query.getRows());
            page = specificationService.selectPage(page);
            return new PageList<Specification>(page.getTotal(),page.getRecords());
    }


    /**
     * 根据类型id查询所有的显示属性
     *    查询条件: 类型id, isSku
     */
    // /specification/productType/{id}
    @RequestMapping(value = "/product/{productId}",method = RequestMethod.GET)
    public List<Specification> queryViewProperties(@PathVariable("productId") Long productId){
        Product product = productService.selectById(productId);
        String viewProperties = product.getViewProperties();
        //商品已经设置显示属性,直接从product表中获取(里面就有属性,又有数据)
        if (StringUtils.isNotBlank(viewProperties)){
            return JSONArray.parseArray(viewProperties, Specification.class);
        }else{
            EntityWrapper<Specification> wrapper = new EntityWrapper<>();
            wrapper.eq("product_type_id", product.getProductTypeId());
            wrapper.eq("is_sku", 0);
            return  specificationService.selectList(wrapper);
        }

    }


    /**
     * 根据类型id查询所有的显示属性
     *    查询条件: 类型id, isSku
     */
    // /specification/productType/{id}
    @RequestMapping(value = "/product/skusProperties/{productId}",method = RequestMethod.GET)
    public List<Specification> querySkusProperties(@PathVariable("productId") Long productId){

        //获取商品,尝试从里面获取sku_template
        Product product = productService.selectById(productId);
        String skuTemplate = product.getSkuTemplate();
        if (StringUtils.isNotBlank(skuTemplate)){
            return JSONArray.parseArray(skuTemplate, Specification.class);
        }
        //如果有直接转换返回,否则从属性表中查询
        //参数 类型ID 是否是sku
        EntityWrapper<Specification> w = new EntityWrapper<>();
        w.eq("product_type_id", product.getProductTypeId());
        w.eq("is_sku", 1);
       return  specificationService.selectList(w);
    }
}
