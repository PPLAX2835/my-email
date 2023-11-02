package xyz.pplax.mymail.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.pplax.mymail.mapper.UserMapper;
import xyz.pplax.mymail.model.entity.User;
import xyz.pplax.mymail.utils.TokenUtils;

@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    public int deleteByPrimaryKey(Long uid) {
        return userMapper.deleteByPrimaryKey(uid);
    }

    public int insert(User record) {
        return userMapper.insert(record);
    }

    public int insertSelective(User record) {
        return userMapper.insertSelective(record);
    }

    public User selectByPrimaryKey(Long uid) {
        return userMapper.selectByPrimaryKey(uid);
    }

    public User selectByUsernameAndPassword(String username, String password) {
        return userMapper.selectByUsernameAndPassword(username, password);
    }

    public User selectByToken(String token) {
        String username = null;
        String password = null;
        // 解析token
        try {
            Claims claims = TokenUtils.parseToken(token);
            username = claims.getSubject();
            password = (String) claims.get("password");
        } catch (ExpiredJwtException e) {
            // token过期下线账号
            Claims claims = e.getClaims();
            username = claims.getSubject();
            password = (String) claims.get("password");
            User user = userMapper.selectByUsernameAndPassword(username, password);
            if (user != null) {
                return null;
            }
        }
        return userMapper.selectByUsernameAndPassword(username, password);
    }

    public int updateByPrimaryKeySelective(User record) {
        return userMapper.updateByPrimaryKeySelective(record);
    }

    public int updateByPrimaryKey(User record) {
        return userMapper.updateByPrimaryKey(record);
    }


}
