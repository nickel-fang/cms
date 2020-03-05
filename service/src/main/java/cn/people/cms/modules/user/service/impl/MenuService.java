package cn.people.cms.modules.user.service.impl;

import cn.people.cms.base.service.impl.TreeService;
import cn.people.cms.modules.user.model.Menu;
import cn.people.cms.modules.user.model.User;
import cn.people.cms.modules.user.service.IMenuService;
import cn.people.cms.modules.user.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true,rollbackFor = Exception.class)
public class MenuService extends TreeService<Menu> implements IMenuService {

    @Autowired
    private Dao dao;

    @Autowired
    private IUserService userService;

    @Override
    public Menu queryCode(Menu menu) {
        List<Menu>result;
        if(StringUtils.isNotBlank(menu.getPermission())){
            result = dao.query(tClass, getDelFlag(null).
                    and(Cnd.exps("code", "=", menu.getCode().trim()).or("permission","=",menu.getPermission().trim())));
        }else {
            result = dao.query(tClass, getDelFlag(null).and("code", "=", menu.getCode().trim()));
        }
        return result.size() > 0 ? result.get(0) : null;
    }

    /**
     * 增加一条菜单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object save(Menu menu) {
        return super.save(menu);
    }


    /**
     * 更新一条菜单信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Menu menu, String fieldName) {
        return super.update(menu, fieldName);
    }

    @Override
    public List<Menu> getMenuTree(Integer userId, Boolean isAll) {
        return getCurrentUserMenu(userId,null, isAll);
    }

    @Override
    public List<Menu> getMenuTree(Boolean filterView, Integer userId, Boolean isAll) {
        return getCurrentUserMenu(userId, filterView, isAll);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateIgnoreNull(Menu menu) {
        return super.updateIgnoreNull(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdate(List<Menu> list) {
        super.batchUpdate(list);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSort(List<Menu> list) {
        if(null != list && list.size() > 0){
            for (Menu m : list){
                Menu menu = fetch(m.getId());
                menu.setSort(m.getSort());
                updateIgnoreNull(menu);
            }
        }
    }

    /**
     * 返回菜单树
     */
    @Override
    public List<Menu> getCurrentUserMenu(Integer userId, Boolean filterView, Boolean isAll) {
        User user = userService.fetch(userId);
        Cnd cnd = Cnd.where("parent_id", "=", 0).and("del_flag", "=", 0);

        cnd.desc("sort");
        List<Menu> root = dao.query(Menu.class, cnd);
        if(user.isSuper() || (null != isAll && isAll)){
            treeList(root, filterView);
            return root;
        }
        Set<Integer> menuIds = userService.getMenuIds(user);
        if(Lang.isEmpty(menuIds)){
            return null;
        }
        List<Menu> list = queryByParentId(filterView, 0);
        list = setChild(list,menuIds,filterView);
        return list;
    }

    private Menu recursive(Menu menu, Boolean filterView, Set<Integer> menuIds) {
        List<Menu> menus = queryByParentId(filterView, menu.getId());
        menus = setChild(menus,menuIds,filterView);
        menu.setChild(menus);
        return menu;
    }

    private List<Menu> setChild(List<Menu> list, Set<Integer> menuIds, Boolean filterView){
        if (null == menuIds || menuIds.size() < 1) {
            return null;
        }
        if (null == list || list.size() < 1) {
            return null;
        }
        Iterator<Menu> iterator = list.iterator();
        while (iterator.hasNext()){
            Menu menu = iterator.next();
            if(menu!=null){
                if (!menuIds.contains(menu.getId())) {
                    iterator.remove();
                    if(Lang.isEmpty(list)){
                        return null;
                    }
                } else {
                    recursive(menu, filterView,menuIds);
                }
            }
        }
        return list;
    }
}
