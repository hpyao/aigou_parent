package cn.itsource.aigou.service;

import cn.itsource.aigou.ProductService_8002;
import cn.itsource.aigou.domain.Brand;
import cn.itsource.aigou.domain.Product;
import cn.itsource.aigou.query.BrandQuery;
import cn.itsource.aigou.query.ProductQuery;
import cn.itsource.aigou.util.PageList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProductService_8002.class )
public class ProductServiceTest {

    @Autowired
    private IProductService productService;

     @Test
      public void test() throws Exception{

         PageList<Product> page = productService.selectPageList(new ProductQuery());
         System.out.println(page.getTotal());
         for (Product product : page.getRows()) {
             System.out.println(product);
             System.out.println(product.getProductType());
             System.out.println(product.getBrand());
             System.out.println(product.getProductExt());
         }

     }
}
