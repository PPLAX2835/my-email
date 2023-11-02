package xyz.pplax.mymail;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;
import xyz.pplax.mymail.mapper.EmailMapper;
import xyz.pplax.mymail.mapper.UserMapper;
import xyz.pplax.mymail.model.entity.Email;
import xyz.pplax.mymail.model.entity.Menu;
import xyz.pplax.mymail.model.entity.User;
import xyz.pplax.mymail.model.resp.ResponseResult;
import xyz.pplax.mymail.service.MailService;
import xyz.pplax.mymail.service.MenuService;
import xyz.pplax.mymail.service.UserService;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@SpringBootTest
class MymailApplicationTests {

    @Autowired
    MailService mailService;

    @Test
    void sentEmailTest() {

        String recipientEmail = "lax1458667357@163.com";
        String subject = "这是一条标题";
        String content = "这是一段内容";
        MultipartFile attachment = null;

        // 查询用户对应邮箱
        Email email = new Email();
        email.setEmailAddress("1458667357@qq.com");
        email.setUid(7312L);
        List<Email> emails = emailMapper.selectListSelective(email);
        if (emails.size() == 0) {
            throw new RuntimeException("找不到邮箱，可能是参数错误");
        }
        Email userEmail = emails.get(0);

        try {
            if (attachment != null) {
                byte[] attachmentBytes = attachment.getBytes();
                mailService.sendMailMessage(userEmail, recipientEmail, subject, content, attachmentBytes, attachment.getOriginalFilename());
            } else {
                mailService.sendMailMessage(userEmail, recipientEmail, subject, content, null, null);
            }
            System.out.println(JSON.toJSONString(ResponseResult.success()));
        } catch (IOException e) {
            System.out.println(JSON.toJSONString(ResponseResult.error(e.getMessage())));
        }
    }


    @Autowired
    UserMapper userMapper;
    @Autowired
    EmailMapper emailMapper;
    @Test
    public void MapperTest() {
        User user = new User();
        user.setUid(1L);
        user.setUsername("PPLAX");
        user.setPassword("123456");
        user.setCreateTime(new Date());
        userMapper.insert(user);

        Email email = new Email();
        email.setEmailAddress("1458667357@qq.com");
        email.setEmailPassword("dvbviwakqsrvbadb");
        email.setHost("smtp.qq.com");
        email.setUid(1L);
        email.setPort(587);
        email.setCreateTime(new Date());

        emailMapper.insert(email);
    }


    @Autowired
    UserService userService;
    @Test
    public void loginTest() {
        User user = userService.selectByUsernameAndPassword("PPLAX", "123456");
        System.out.println(JSON.toJSONString(user));
    }


    @Autowired
    MenuService menuService;
    @Test
    public void menuTest() {
        List<Menu> menus = menuService.selectParentMenuList();

        List<Menu> menus1 = menuService.selectListByPrimaryKey(1);

        Menu menu = new Menu();
        menu.setParentMenuId(1);
        List<Menu> menus2 = menuService.selectListSelective(menu);

        System.out.println(JSON.toJSONString(menus));
        System.out.println(JSON.toJSONString(menus1));
        System.out.println(menus2);
    }

}
