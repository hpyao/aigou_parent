package cn.itsource.aigou.controller;

import cn.itsource.aigou.client.ProductDocClient;
import cn.itsource.aigou.index.ProductDoc;
import cn.itsource.aigou.service.IProductDocService;
import cn.itsource.aigou.util.AjaxResult;
import cn.itsource.aigou.util.PageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/productDoc")
public class ProductDocController implements ProductDocClient{

    @Autowired
    private IProductDocService productDocService;
    //crud
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public AjaxResult save(ProductDoc productDoc){

        try {
            productDocService.add(productDoc);
            return AjaxResult.me();
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.me().setSuccess(false).setMessage("保存失败!"+e.getMessage());
        }

    }

    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE)
    public  AjaxResult del(@PathVariable("id") Long id){
        try {
            productDocService.del(id);
            return AjaxResult.me();
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.me().setSuccess(false).setMessage("删除失败!"+e.getMessage());
        }
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public  ProductDoc get(@PathVariable("id") Long id){
        return productDocService.get(id);
    }
    //批量操作
    @RequestMapping(value = "/batchSave",method = RequestMethod.POST)
    public  AjaxResult batchSave(@RequestBody List<ProductDoc> productDocs){
        try {
            productDocService.batchAdd(productDocs);
            return AjaxResult.me();
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.me().setSuccess(false).setMessage("保存失败!"+e.getMessage());
        }
    }
    @RequestMapping(value = "/batchDel",method = RequestMethod.DELETE)
    public AjaxResult batchDel(@RequestBody List<Long> ids){
        try {
            productDocService.batchDel(ids);
            return AjaxResult.me();
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.me().setSuccess(false).setMessage("删除失败!"+e.getMessage());
        }
    }
    //分页搜索
    @RequestMapping(value = "/search",method = RequestMethod.GET)
    public PageList<ProductDoc> search(Map<String,Object> params){
        return productDocService.search(params);
    }
}
