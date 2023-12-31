package xyz.pplax.mymail.controller;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.pplax.mymail.model.entity.Menu;
import xyz.pplax.mymail.model.resp.ResponseResult;
import xyz.pplax.mymail.service.MenuService;

import java.util.List;

/**
 * 菜单controller
 */
@RestController
@RequestMapping("/api")
@Api(value = "菜单接口", tags = "MenuController", description = "获得菜单")
public class MenuController {

    @Autowired
    MenuService menuService;

    @GetMapping("/menus")
    public String getMenus() {
        List<Menu> parentMenuList = menuService.selectParentMenuList();

        for (Menu menu : parentMenuList) {
            Menu record = new Menu();
            record.setParentMenuId(menu.getMenuId());

            List<Menu> menus = menuService.selectListSelective(record);
            menu.setChileMenus(menus);
        }

        return JSON.toJSONString(ResponseResult.success(parentMenuList));
    }


}
