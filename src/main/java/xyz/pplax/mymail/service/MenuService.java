package xyz.pplax.mymail.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.pplax.mymail.mapper.MenuMapper;
import xyz.pplax.mymail.model.entity.Menu;

import java.util.List;

@Service
public class MenuService {

    @Autowired
    MenuMapper menuMapper;

    public int deleteByPrimaryKey(Integer menuId) {
        return menuMapper.deleteByPrimaryKey(menuId);
    }

    public int insert(Menu record) {
        return menuMapper.insert(record);
    }

    public int insertSelective(Menu record) {
        return insertSelective(record);
    }

    public Menu selectByPrimaryKey(Integer menuId) {
        return menuMapper.selectByPrimaryKey(menuId);
    }

    public List<Menu> selectParentMenuList() {
        return menuMapper.selectParentMenuList();
    }

    public List<Menu> selectListByPrimaryKey(Integer menuId) {
        return menuMapper.selectListByPrimaryKey(menuId);
    }

    public List<Menu> selectListSelective(Menu record) {
        return menuMapper.selectListSelective(record);
    }

    public int updateByPrimaryKeySelective(Menu record) {
        return menuMapper.updateByPrimaryKeySelective(record);
    }

    public int updateByPrimaryKey(Menu record) {
        return menuMapper.updateByPrimaryKey(record);
    }
}
