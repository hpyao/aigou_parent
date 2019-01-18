package cn.itsource.aigou.controller;

import cn.itsource.aigou.service.ITypeTemplateService;
import cn.itsource.aigou.domain.TypeTemplate;
import cn.itsource.aigou.query.TypeTemplateQuery;
import cn.itsource.aigou.util.AjaxResult;
import cn.itsource.aigou.util.PageList;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {
    @Autowired
    public ITypeTemplateService typeTemplateService;

    /**
    * 保存和修改公用的
    * @param typeTemplate  传递的实体
    * @return Ajaxresult转换结果
    */
    @RequestMapping(value="/save",method= RequestMethod.POST)
    public AjaxResult save(@RequestBody TypeTemplate typeTemplate){
        try {
            if(typeTemplate.getId()!=null){
                typeTemplateService.updateById(typeTemplate);
            }else{
                typeTemplateService.insert(typeTemplate);
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
            typeTemplateService.deleteById(id);
            return AjaxResult.me();
        } catch (Exception e) {
        e.printStackTrace();
            return AjaxResult.me().setMessage("删除对象失败！"+e.getMessage());
        }
    }

    //获取用户
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public TypeTemplate get(@PathVariable("id")Long id)
    {
        return typeTemplateService.selectById(id);
    }


    /**
    * 查看所有的员工信息
    * @return
    */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public List<TypeTemplate> list(){

        return typeTemplateService.selectList(null);
    }


    /**
    * 分页查询数据
    *
    * @param query 查询对象
    * @return PageList 分页对象
    */
    @RequestMapping(value = "/json",method = RequestMethod.POST)
    public PageList<TypeTemplate> json(@RequestBody TypeTemplateQuery query)
    {
        Page<TypeTemplate> page = new Page<TypeTemplate>(query.getPage(),query.getRows());
            page = typeTemplateService.selectPage(page);
            return new PageList<TypeTemplate>(page.getTotal(),page.getRecords());
    }
}
