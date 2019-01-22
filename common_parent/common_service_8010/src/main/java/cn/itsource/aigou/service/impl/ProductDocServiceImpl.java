package cn.itsource.aigou.service.impl;

import cn.itsource.aigou.index.ProductDoc;
import cn.itsource.aigou.repository.ProductDocRepository;
import cn.itsource.aigou.service.IProductDocService;
import cn.itsource.aigou.util.PageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProductDocServiceImpl implements IProductDocService{
    @Autowired
    private ProductDocRepository repository;

    @Override
    public void add(ProductDoc productDoc) {
        repository.save(productDoc);
    }

    @Override
    public void del(Long id) {
        repository.deleteById(id);
    }

    @Override
    public ProductDoc get(Long id) {
        return  repository.findById(id).get();
    }

    @Override
    public void batchAdd(List<ProductDoc> productDocs) {

        repository.saveAll(productDocs);
    }

    @Override
    public void batchDel(List<Long> ids) {

        for (Long id : ids) {
            repository.deleteById(id);
        }
    }

    @Override
    public PageList<ProductDoc> search(Map<String, Object> params) {
        return null;
    }
}
