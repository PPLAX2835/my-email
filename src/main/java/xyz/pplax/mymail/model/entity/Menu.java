package xyz.pplax.mymail.model.entity;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * menu
 * @author 
 */
@Data
public class Menu implements Serializable {
    /**
     * 主键
     */
    private Integer menuId;

    /**
     * 图标
     */
    private String icon;

    /**
     * 选项名
     */
    private String name;

    /**
     * 跳转地址
     */
    private String url;

    /**
     * 父级菜单id
     */
    private Integer parentMenuId;

    /**
     * 子菜单列表
     */
    private List<Menu> chileMenus;

    private static final long serialVersionUID = 1L;
}