package cn.itsource.aigou.controller;

import cn.itsource.aigou.domain.Sku;
import cn.itsource.aigou.domain.Specification;
import cn.itsource.aigou.mapper.SkuMapper;
import cn.itsource.aigou.service.IProductService;
import cn.itsource.aigou.domain.Product;
import cn.itsource.aigou.query.ProductQuery;
import cn.itsource.aigou.util.AjaxResult;
import cn.itsource.aigou.util.PageList;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.plugins.Page;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    public IProductService productService;

    /**
    * 保存和修改公用的
    * @param product  传递的实体
    * @return Ajaxresult转换结果
    */
    @RequestMapping(value="/save",method= RequestMethod.POST)
    public AjaxResult save(@RequestBody Product product){
        try {
                if(product.getId()!=null){
                        productService.updateById(product);
                    }else{
                        productService.insert(product);
                    }
                return AjaxResult.me();
            } catch (Exception e) {
                e.printStackTrace();
                return AjaxResult.me().setMessage("保存对象失败！"+e.getMessage());
            }
        }
    @RequestMapping(value="/onSale",method= RequestMethod.POST)
    public AjaxResult onSale(@RequestBody Map<String,Object> params){
        try {
            String ids = (String) params.get("ids");//1,2,3
            System.out.println(ids);
            Integer onSale = Integer.valueOf(params.get("onSale").toString());
            System.out.println(onSale);
            productService.onSale(ids,onSale);
            return AjaxResult.me();
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.me().setSuccess(false).setMessage("上下架失败！"+e.getMessage());
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
            productService.deleteById(id);
            return AjaxResult.me();
        } catch (Exception e) {
        e.printStackTrace();
            return AjaxResult.me().setMessage("删除对象失败！"+e.getMessage());
        }
    }

    //获取用户
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public Product get(@PathVariable("id")Long id)
    {
        return productService.selectById(id);
    }


    /**
    * 查看所有的员工信息
    * @return
    */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public List<Product> list(){

        return productService.selectList(null);
    }


    /**
    * 分页查询数据
    *
    * @param query 查询对象
    * @return PageList 分页对象
    */
    @RequestMapping(value = "/json",method = RequestMethod.POST)
    public PageList<Product> json(@RequestBody ProductQuery query)
    {
            return productService.selectPageList(query);
    }



    /**
     * 保存和修改公用的
     * @param params  传递的实体
     * @return Ajaxresult转换结果
     */
    @RequestMapping(value="/addViewProperties",method= RequestMethod.POST)
    public AjaxResult save(@RequestBody Map<String,Object> params){
        try {
            Integer tmp = (Integer) params.get("productId"); //Integer
            Long productId = Long.parseLong(tmp.toString());
            List<Specification> specifications = (List<Specification>) params.get("specifications");
            productService.addViewProperties(productId,specifications);
            return AjaxResult.me();
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.me().setSuccess(false).setMessage("保存显示属性失败！"+e.getMessage());
        }
    }
    /**
     * 保存和修改公用的
     * @param params  传递的实体
     * @return Ajaxresult转换结果
     */
    @RequestMapping(value="/addSkus",method= RequestMethod.POST)
    public AjaxResult addSkus(@RequestBody Map<String,Object> params){
        try {
            Integer tmp = (Integer) params.get("productId"); //Integer
            Long productId = Long.parseLong(tmp.toString());
            //[{id:1,name:xxx,skuValus[]},id:1,name:xxx,skuValus[]}]
            List<Map<String,Object>> skuProperties = (List<Map<String,Object>>) params.get("skuProperties");
            //skuDatas [{'身高':18,"price":18},{'身高':18,"price":18}]
            List<Map<String,Object>> skuDatas = (List<Map<String,Object>>) params.get("skuDatas");
            //sku{id:xx,price:xxx,skuValues:[{id,key,value},{}],indexs:1_2_3}
            productService.addSkus(productId,skuProperties,skuDatas);
            return AjaxResult.me();
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.me().setSuccess(false).setMessage("保存sku属性失败！"+e.getMessage());
        }
    }


    @RequestMapping(value = "/skus/{productId}",method = RequestMethod.GET)
    public List<Map<String,Object>> querySkus(@PathVariable("productId") Long productId){
        List<Sku> skus = productService.querySkus(productId);
        List<Map<String,Object>> result = new ArrayList<>();
        //把skuValues要进行转换
        for (Sku sku : skus) {
            result.add(sku2map(sku));
        }
        return result;
    }

    private Map<String,Object> sku2map(Sku sku) {
        Map<String,Object> result = new LinkedHashMap<>();
//        result.put("id",sku.getId() );
//        result.put("productId",sku.getProductId() );
//        result.put("indexs",sku.getIndexs() );
        //[{"id":37,"key":"三维","value":"d"},{"id":36,"key":"身高","value":"c"},{"id":35,"key":"肤色","value":"a"}]
        String skuValues = sku.getSkuValues();
        List<Map> maps = JSONArray.parseArray(skuValues, Map.class);
        //{"id":37,"key":"三维","value":"d"}
        for (Map map : maps) {
            String key = (String) map.get("key");
            String value = (String) map.get("value");
            result.put(key, value);
        }
        result.put("price",sku.getPrice() );
        result.put("stock",sku.getStock() );
        result.put("state",sku.getState() );
        return result;
    }

}
