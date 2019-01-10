package cn.itsource.aigou;

import org.assertj.core.internal.bytebuddy.asm.Advice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
@EnableConfigServer
public class ConfigServerApplication_8848 {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication_8848.class);
    }
}
