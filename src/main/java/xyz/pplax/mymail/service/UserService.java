package xyz.pplax.mymail.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.pplax.mymail.mapper.UserMapper;
import xyz.pplax.mymail.model.entity.User;

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

    public int updateByPrimaryKeySelective(User record) {
        return userMapper.updateByPrimaryKeySelective(record);
    }

    public int updateByPrimaryKey(User record) {
        return userMapper.updateByPrimaryKey(record);
    }


}
