package xyz.pplax.mymail.controller;

import com.alibaba.fastjson.JSON;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.utils.CaptchaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.pplax.mymail.model.dto.LoginDto;
import xyz.pplax.mymail.model.entity.User;
import xyz.pplax.mymail.model.resp.ResponseResult;
import xyz.pplax.mymail.service.UserService;
import xyz.pplax.mymail.utils.AESUtil;
import xyz.pplax.mymail.utils.RedisOperator;
import xyz.pplax.mymail.utils.TokenUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static xyz.pplax.mymail.utils.AESUtil.desEncrypt;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    UserService userService;

    @Autowired
    private RedisOperator redisOperator;

    /**
     * 登录
     * @param request
     * @param loginDto
     * @return
     * @throws Exception
     */
    @PostMapping("/login")
    public String login(HttpServletRequest request, LoginDto loginDto) throws Exception {
        // 从session中获取验证码
        HttpSession session = request.getSession();
        String Captcha = (String) session.getAttribute("Captcha");

        // 解密
        String iv = AESUtil.IV;                                 // 初始向量iv
        String key = loginDto.getCode();                                      // 密钥
        String text = desEncrypt(loginDto.getPassword(), key, iv).trim();     // 解密后的明文

        // 裁剪code，获得用户输入的验证码
        String code = loginDto.getCode().substring(0, 4);
        // 查库
        User user = userService.selectByUsernameAndPassword(loginDto.getUsername(), text);
        // 判断是否存在
        boolean success = (user != null);
        // 判断验证码
        boolean code_correct = code.equalsIgnoreCase(Captcha);    // 验证码

        String msg = null;
        String token = null;
        boolean flag = false;
        if (success) {
            if (code_correct) {
                // 验证码正确
                token = TokenUtils.generateToken(loginDto.getUsername(), text);
                // 存redis
                redisOperator.set(loginDto.getUsername(), JSON.toJSONString(user));
                flag = true;
            } else {
                // 验证码错误
                success = false;
                msg = "验证码错误！";
            }
        } else {
            msg = "账号或密码错误！";
        }

        if (flag) {
            return JSON.toJSONString(ResponseResult.success(token));
        } else {
            return JSON.toJSONString(ResponseResult.error(msg));
        }
    }


    /**
     * 登出
     * @param token
     * @return
     */
    @DeleteMapping("/logout")
    public String logout(@RequestParam("token") String token) {
        System.out.println(token);
        return "";
    }


    /**
     * 获得验证码图片
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping(path = "/captchaimg")
    public void getCaptchaImg(HttpServletRequest request, HttpServletResponse response, @RequestParam(name = "key", required = false) String key) throws IOException {
        GifCaptcha gifCaptcha = new GifCaptcha(130, 20, 4);
        String code = gifCaptcha.text();
        HttpSession session = request.getSession();
        session.setAttribute("Captcha", code);
        CaptchaUtil.out(gifCaptcha, request, response);
    }

}
