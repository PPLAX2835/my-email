package xyz.pplax.mymail.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.pplax.mymail.model.entity.User;
import xyz.pplax.mymail.model.resp.ResponseResult;
import xyz.pplax.mymail.service.UserService;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

    /**
     * 获得当前用户信息
     * @return
     */
    @GetMapping("/self")
    public String getSelf(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("token");
        User user = userService.selectByToken(token);
        user.setPassword(null);
        return JSON.toJSONString(ResponseResult.success(user));
    }



}
