package xyz.pplax.mymail.mapper;

import org.apache.ibatis.annotations.Mapper;
import xyz.pplax.mymail.model.entity.Email;

import java.util.List;

@Mapper
public interface EmailMapper {
    int deleteByPrimaryKey(Long emailId);

    int insert(Email record);

    int insertSelective(Email record);

    Email selectByPrimaryKey(Long emailId);

    List<Email> selectSelective(Email record);

    int updateByPrimaryKeySelective(Email record);

    int updateByPrimaryKey(Email record);
}