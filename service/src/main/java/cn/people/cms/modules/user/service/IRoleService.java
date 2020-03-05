package cn.people.cms.modules.user.service;

import cn.people.cms.base.service.IBaseService;
import cn.people.cms.modules.user.model.Role;
import org.nutz.dao.QueryResult;

import java.util.List;
import java.util.Set;

/**
* 用户管理下角色管理Service
* @author cuiyukun
*/
public interface IRoleService extends IBaseService<Role> {

    Role update(Role oriRole);

    List<Role> findRoleByUserId(String userId);

    List<Role> getCurrentUserRole();

    List<Role> listAll();

    QueryResult listPage(Integer pageNumber, Integer pageSize);

    Set<Integer> getCategoryIds(List<Role> roles, Integer type);

    Set<Integer> getSiteIds(List<Role> roles, Integer type);

}