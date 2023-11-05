package xyz.pplax.mymail.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.pplax.mymail.component.handler.CommonBlockHandler;

@RestController
@RequestMapping("/api/test")
@Api(value = "认证相关接口", tags = "AuthController", description = "用户登录、登出等相关接口")
public class TestController {

    @RequestMapping("/test1")
    @SentinelResource(value = "my-mail-api-resource", blockHandlerClass = CommonBlockHandler.class, blockHandler = "handle")
    public String test1() {
        return "test1";
    }


}
