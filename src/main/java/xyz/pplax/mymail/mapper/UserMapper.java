package xyz.pplax.mymail.mapper;

import org.apache.ibatis.annotations.Mapper;
import xyz.pplax.mymail.model.entity.User;

@Mapper
public interface UserMapper {
    int deleteByPrimaryKey(Long uid);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long uid);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}