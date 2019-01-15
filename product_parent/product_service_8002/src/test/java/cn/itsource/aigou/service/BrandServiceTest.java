package cn.itsource.aigou.service;

import cn.itsource.aigou.ProductService_8002;
import cn.itsource.aigou.domain.Brand;
import cn.itsource.aigou.query.BrandQuery;
import cn.itsource.aigou.util.PageList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProductService_8002.class )
public class BrandServiceTest {

    @Autowired
    private IBrandService brandService;

     @Test
      public void test() throws Exception{
         BrandQuery query = new BrandQuery();
         query.setKeyword("七");
         PageList<Brand> pagelist = brandService.selectPageList(query);
         System.out.println(pagelist.getTotal());
         List<Brand> rows = pagelist.getRows();
         for (Brand row : rows) {
             System.out.println(row);
             System.out.println(row.getProductType());

         }


     }
}
