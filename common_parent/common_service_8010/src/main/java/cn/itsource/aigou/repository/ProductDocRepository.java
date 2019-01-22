package cn.itsource.aigou.repository;

import cn.itsource.aigou.index.ProductDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDocRepository extends ElasticsearchRepository<ProductDoc,Long> {
}
