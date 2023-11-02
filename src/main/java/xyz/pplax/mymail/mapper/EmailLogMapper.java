package xyz.pplax.mymail.mapper;

import org.apache.ibatis.annotations.Mapper;
import xyz.pplax.mymail.model.entity.EmailLog;

@Mapper
public interface EmailLogMapper {
    int deleteByPrimaryKey(Long logId);

    int insert(EmailLog record);

    int insertSelective(EmailLog record);

    EmailLog selectByPrimaryKey(Long logId);

    int updateByPrimaryKeySelective(EmailLog record);

    int updateByPrimaryKey(EmailLog record);
}