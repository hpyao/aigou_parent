package cn.itsource.aigou;

import cn.itsource.aigou.index.ProductDoc;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CommonApplication_8010.class)
public class ProductDocInitTest {

    @Autowired
    private ElasticsearchTemplate template;

     @Test
      public void testInit() throws Exception{

         template.createIndex(ProductDoc.class);
         template.putMapping(ProductDoc.class);
      }
}
