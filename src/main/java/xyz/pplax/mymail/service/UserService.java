package xyz.pplax.mymail.service;

import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.pplax.mymail.mapper.UserMapper;
import xyz.pplax.mymail.model.constants.RedisKeyConstants;
import xyz.pplax.mymail.model.entity.User;
import xyz.pplax.mymail.utils.RedisOperator;
import xyz.pplax.mymail.utils.TokenUtils;

@Service
public class UserService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    private RedisOperator redisOperator;

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

            // 从缓存中移除
            redisOperator.del(RedisKeyConstants.USER_INFO_PREFIX + username);
        }

        // 先从缓存中查询，如果查询不到就从数据库中查
        String userJsonStr = redisOperator.get(RedisKeyConstants.USER_INFO_PREFIX + username);
        if (userJsonStr != null) {
            return JSON.parseObject(userJsonStr, User.class);
        } else {
            User user = userMapper.selectByUsernameAndPassword(username, password);
            // 存到缓存中
            if (user != null) {
                redisOperator.set(RedisKeyConstants.USER_INFO_PREFIX + user.getUsername(), JSON.toJSONString(user));
            }
            return user;
        }
    }

    public int updateByPrimaryKeySelective(User record) {
        return userMapper.updateByPrimaryKeySelective(record);
    }

    public int updateByPrimaryKey(User record) {
        return userMapper.updateByPrimaryKey(record);
    }


}
