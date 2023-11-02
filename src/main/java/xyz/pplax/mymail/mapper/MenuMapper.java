package xyz.pplax.mymail.mapper;

import org.apache.ibatis.annotations.Mapper;
import xyz.pplax.mymail.model.entity.Menu;

import java.util.List;

@Mapper
public interface MenuMapper {
    int deleteByPrimaryKey(Integer menuId);

    int insert(Menu record);

    int insertSelective(Menu record);

    Menu selectByPrimaryKey(Integer menuId);

    List<Menu> selectParentMenuList();

    List<Menu> selectListByPrimaryKey(Integer menuId);

    List<Menu> selectListSelective(Menu record);

    int updateByPrimaryKeySelective(Menu record);

    int updateByPrimaryKey(Menu record);
}