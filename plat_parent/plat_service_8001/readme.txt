1 config server 可以集群
   discovery:
          enabled: true #从eureka上面找配置服务
          service-id: aigou-config-server #指定服务名
2 支持环境切换
     profile: ${spring.profiles.active} #环境
     java -jar -Dspring.profiles.active=test xxx.jar
3 run dashboard
   必须打包插件