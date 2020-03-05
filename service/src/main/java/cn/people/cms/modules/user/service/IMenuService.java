package cn.people.cms.modules.user.service;

import cn.people.cms.base.service.ITreeService;
import cn.people.cms.modules.user.model.Menu;

import java.util.List;

/**
* 用户管理下菜单管理Service
* @author cuiyukun
*/
public interface IMenuService extends ITreeService<Menu> {

    List<Menu> getCurrentUserMenu(Integer userId, Boolean filterView, Boolean isAll);

    @Override
    int updateIgnoreNull(Menu menu);

    @Override
    void batchUpdate(List<Menu> list);

    void batchSort(List<Menu> list);

    Menu queryCode(Menu menu);

    List<Menu> getMenuTree(Integer userId, Boolean isAll);

    List<Menu> getMenuTree(Boolean filterView, Integer userId, Boolean isAll);
}