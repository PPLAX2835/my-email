package xyz.pplax.mymail.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.pplax.mymail.model.resp.ResponseCode;
import xyz.pplax.mymail.model.resp.ResponseResult;

@RestController
@RequestMapping("/api/error")
public class ErrorController {

    @RequestMapping("/parameter")
    public String parameterError() {
        return JSON.toJSONString(ResponseResult.error(ResponseCode.ERROR));
    }

    @RequestMapping("/expired")
    public String expired() {
        return JSON.toJSONString(ResponseResult.error(ResponseCode.TOKEN_EXPIRED));
    }


}
