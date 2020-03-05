package cn.people.cms.modules.user.service.impl;

import cn.people.cms.base.service.impl.BaseService;
import cn.people.cms.entity.BaseEntity;
import cn.people.cms.modules.cms.model.Site;
import cn.people.cms.modules.sys.model.Category;
import cn.people.cms.modules.user.model.Menu;
import cn.people.cms.modules.user.model.Role;
import cn.people.cms.modules.user.model.User;
import cn.people.cms.modules.user.service.IMenuService;
import cn.people.cms.modules.user.service.IRoleService;
import cn.people.cms.modules.user.service.IUserService;
import cn.people.cms.util.base.UserUtil;
import cn.people.domain.IUser;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.dao.sql.Criteria;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@Transactional(readOnly = true,rollbackFor = Exception.class)
public class UserService extends BaseService<User> implements IUserService {


    @Autowired
    private IRoleService roleService;

    @Autowired
    private IMenuService menuService;

    /**
     * username  管理员
     * 根据username获取用户信息
     *
     * @param username
     * @return
     */
    @Override
    public User fetch(String username) {
        User user = dao.fetch(User.class, username);
        return user;
    }

    /**
     * 根据用户id获取用户信息
     * 将用户具有的权限id放入user
     * @param id
     * @return
     */
    @Override
    public User fetch(Integer id){
        User user = super.fetch(id);
        if (!Lang.isEmpty(user)) {
            fetchLinks(user, User.ROLE_LIST);
        }
        List<Integer> roleIds = new ArrayList<>();
        if (!Lang.isEmpty(user.getRoleList())){
            for (Role role : user.getRoleList()) {
                roleIds.add(role.getId());
            }
        }
        user.setRoleIds(roleIds);
        user.setRoleList(null);
        user.setCreateAt(null);
        user.setCreateBy(null);
        user.setUpdateAt(null);
        user.setUpdateBy(null);
        return user;
    }

    /**
     * 在角色关联表中加入记录
     * @param user
     */
    @Override
    public void saveRoleRelation(User user) {
        //清除原有的用户角色关联表中的记录
        User oriUser = fetchLinks(user, User.ROLE_LIST);
        if (!Lang.isEmpty(oriUser.getRoleList())) {
            dao.clearLinks(oriUser, User.ROLE_LIST);
        }
        List<Role> list = new ArrayList<>();
        for (Integer id : user.getRoleIds()) {
            Role role = roleService.fetch(id);
            list.add(role);
        }
        user.setRoleList(list);
        dao.insertRelation(user, User.ROLE_LIST);
    }

    /**
     * @param user
     * @param params
     * @return
     */
    private User fetchLinks(User user,String... params){
        if(params!=null && params.length>0){
            for(String param:params){
                dao.fetchLinks(user, param);
            }
        }
        return user;
    }

    /**
     * 获取用户列表并分页
     */
    @Override
    public QueryResult listPage(Integer pageNumber, Integer pageSize, String username, String name) {
        IUser user = UserUtil.getUser();
        Criteria cri = Cnd.NEW().getCri();
        SqlExpressionGroup cnd = cri.where();
        if (!user.isAdmin()) {
            cnd = cnd.and("username", "!=", "super");
        }
        cnd = StringUtils.isNoneBlank(username) ? cnd.and("username", "like", "%" + username + "%") : cnd;
        cnd = StringUtils.isNoneBlank(name) ? cnd.and("name", "like", "%" + name + "%") : cnd;
        cnd = cnd.and(BaseEntity.FIELD_STATUS,"<", BaseEntity.STATUS_DELETE);
        QueryResult queryResult = listPage(pageNumber, pageSize, cri, "^id|username|name|remark$");
        return queryResult;
    }

    @Override
    public Set<Integer> getMenuIds(User user){
        Set<Integer> menuIds = new HashSet<>();
//        if(user.isSuper()){
//            List<Menu>list = dao.query(Menu.class,Cnd.where(BaseEntity.FIELD_STATUS,"<", Menu.STATUS_DELETE).and("system_id", "=", system.getId()));
//            list.forEach(menu -> menuIds.add(menu.getId()));
//            return menuIds;
//        }
        List<Role> roles = getRoles(user);
        if(null == roles || roles.size() < 1){
            return menuIds;
        }
        roles.stream().
                map(role -> dao.fetchLinks(role, Role.MENUS, Cnd.where("del_flag", "<", 3))).
                filter(role -> (null != role.getMenus() && role.getMenus().size() > 0)).
                map(role -> role.getMenus()).forEach(menus -> menus.stream().forEach(menu -> menuService.parentIds(menu.getId(), menuIds).invoke()));
        return menuIds;
    }

    @Override
    public Set<Integer> getSiteIds(User user, Integer type){
        Set<Integer> siteIds = new HashSet();
        if(user.isSuper(user.getId())){
            List<Site>sites = dao.query(Site.class,Cnd.where("del_flag","=",Site.STATUS_ONLINE));
            if(!Lang.isEmpty(sites)){
                for (Site site : sites) {
                    siteIds.add(site.getId());
                }
                return siteIds;
            }
        }
        siteIds = roleService.getSiteIds(getRoles(user), type);
        return siteIds;
    }

    @Override
    public Set<Integer> getCategoryIds(User user, Integer type){
        Set<Integer> cids = new HashSet();
        if(user.isSuper(user.getId())){
            List<Category> categories = dao.query(Category.class,Cnd.where("del_flag","=",Site.STATUS_ONLINE));
            if(!Lang.isEmpty(categories)){
                for (Category category : categories) {
                    cids.add(category.getId());
                }
                return cids;
            }
        }
        cids = roleService.getCategoryIds(getRoles(user), type);
        return cids;
    }

    @Override
    public List<Role> getRoles(User user){
        if(null == user){
            return null;
        }
        user = dao.fetchLinks(user, User.ROLE_LIST);
        return user.getRoleList();
    }

    @Override
    public List<Menu> getUserMenus(User user) {
        List<Role> roles = getRoles(user);
        List<Menu> menus = new ArrayList();
        if(!Lang.isEmpty(roles)){
            for (Role role : roles) {
                role = dao.fetchLinks(role, Role.MENUS);
                menus.addAll(role.getMenus());
            }
        }
        return menus;
    }

    @Override
    public int ban(Integer id, Integer delFlag) {
        return 0;
    }

    @Override
    public List<User>findAll(){
        return dao.query(User.class,Cnd.where(User.FIELD_STATUS,"<", User.STATUS_DELETE));
    }

    @Override
    public List<User> findAllByOfficeId(Integer officeId) {
        return null;
    }


}
