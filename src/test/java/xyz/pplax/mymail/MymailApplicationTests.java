package xyz.pplax.mymail;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;
import xyz.pplax.mymail.mapper.EmailMapper;
import xyz.pplax.mymail.mapper.UserMapper;
import xyz.pplax.mymail.model.constants.EmailConstants;
import xyz.pplax.mymail.model.entity.Email;
import xyz.pplax.mymail.model.entity.Menu;
import xyz.pplax.mymail.model.entity.User;
import xyz.pplax.mymail.model.mail.MailMessage;
import xyz.pplax.mymail.model.resp.ResponseResult;
import xyz.pplax.mymail.service.MailService;
import xyz.pplax.mymail.service.MenuService;
import xyz.pplax.mymail.service.UserService;
import xyz.pplax.mymail.utils.ReciveOneMail;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;
import java.io.IOException;
import java.util.*;

@SpringBootTest
class MymailApplicationTests {

    @Autowired
    MailService mailService;
    @Value("${pplax.file.savepath}")
    private String fileSavePath;

//        ("dvbviwakqsrvbadb")

}
