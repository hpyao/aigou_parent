package cn.itsource.aigou;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages = "cn.itsource.aigou") //扫描指定包及其子孙包
public class ProductService_8002 {
    public static void main(String[] args) {
        SpringApplication.run(ProductService_8002.class);
    }
}
