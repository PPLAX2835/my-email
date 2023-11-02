package xyz.pplax.mymail.controller;


import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.pplax.mymail.model.entity.Email;
import xyz.pplax.mymail.model.entity.User;
import xyz.pplax.mymail.model.resp.ResponseResult;
import xyz.pplax.mymail.service.EmailService;
import xyz.pplax.mymail.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    UserService userService;
    @Autowired
    EmailService emailService;

    /**
     * 获得用户保存的邮箱
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/emails")
    public String getEmails(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("token");
        User user = userService.selectByToken(token);

        Email record = new Email();
        record.setUid(user.getUid());
        List<Email> emails = emailService.selectListSelective(record);

        System.out.println(JSON.toJSONString(emails));

        return JSON.toJSONString(ResponseResult.success(emails));
    }

}
