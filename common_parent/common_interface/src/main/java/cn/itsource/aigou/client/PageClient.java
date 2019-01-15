package cn.itsource.aigou.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(value = "AIGOU-COMMON",fallbackFactory = PageClientFallbackFactory.class )//服务提供者的名称
public interface PageClient {

    /**
     * 根据特定模板传入特定数据,生成静态页面到特定位置
     * @param model
     * @param tmeplatePath
     * @param staticPagePath
     * Map<String,Object>
     *      model ==数据
     *      tmeplatePath==xxx
     *      staticPagePath = xxx
     */
    @RequestMapping(value = "/page",method = RequestMethod.POST)
    void genStaticPage(@RequestBody Map<String,Object> params);
}
