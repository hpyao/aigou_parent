package cn.itsource.aigou.service;

import cn.itsource.aigou.index.ProductDoc;
import cn.itsource.aigou.util.PageList;

import java.util.List;
import java.util.Map;

public interface IProductDocService
{
    /**
     * 添加文档
     * @param productDoc
     */
    void add(ProductDoc productDoc);


    /**
     * 删除文档
     * @param id
     */
    void del(Long id);


    /**
     * 获取文档
     * @param id
     * @return
     */
    ProductDoc get(Long id);

    /**
     * 批量添加文档
     * @param productDocs
     */
    void batchAdd(List<ProductDoc> productDocs);

    /**
     * 批量删除
     * @param ids
     */
    void batchDel(List<Long> ids);

    /**
     * 查询
     * @param params
     * @return
     */
    PageList<Map<String,Object>> search(Map<String, Object> params);
}
