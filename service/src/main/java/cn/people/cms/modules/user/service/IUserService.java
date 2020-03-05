package cn.people.cms.modules.user.service;

import cn.people.cms.base.service.IBaseService;
import cn.people.cms.modules.user.model.Menu;
import cn.people.cms.modules.user.model.Role;
import cn.people.cms.modules.user.model.User;
import org.nutz.dao.QueryResult;

import java.util.List;
import java.util.Set;

/**
* 用户Service
* @author cuiyukun
*/
public interface IUserService extends IBaseService<User> {

        QueryResult listPage(Integer pageNumber, Integer pageSize, String username, String name);

        void saveRoleRelation(User user);

        List<Menu> getUserMenus(User user);

        int ban(Integer id, Integer delFlag);

        Set<Integer> getMenuIds(User user);

        List<Role> getRoles(User user);

        List<User>findAll();

        List<User> findAllByOfficeId(Integer officeId);

        Set<Integer> getCategoryIds(User user, Integer type);

        Set<Integer> getSiteIds(User user, Integer type);


}