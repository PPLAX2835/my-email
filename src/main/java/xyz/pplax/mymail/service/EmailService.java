package xyz.pplax.mymail.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.pplax.mymail.mapper.EmailMapper;
import xyz.pplax.mymail.model.entity.Email;

import java.util.List;

@Service
public class EmailService {

    @Autowired
    EmailMapper emailMapper;

    public int deleteByPrimaryKey(Long emailId) {
        return emailMapper.deleteByPrimaryKey(emailId);
    }

    public int insert(Email record) {
        return emailMapper.insert(record);
    }

    public int insertSelective(Email record) {
        return emailMapper.insertSelective(record);
    }

    public Email selectByPrimaryKey(Long emailId) {
        return emailMapper.selectByPrimaryKey(emailId);
    }

    public List<Email> selectListSelective(Email record) {
        return emailMapper.selectListSelective(record);
    }

    public int updateByPrimaryKeySelective(Email record) {
        return emailMapper.updateByPrimaryKeySelective(record);
    }

    public int updateByPrimaryKey(Email record) {
        return emailMapper.updateByPrimaryKey(record);
    }
}
