package cn.people.cms.modules.user.service.impl;

import cn.people.cms.base.service.impl.BaseService;
import cn.people.cms.entity.BaseEntity;
import cn.people.cms.modules.cms.model.Site;
import cn.people.cms.modules.sys.model.Category;
import cn.people.cms.modules.user.model.Menu;
import cn.people.cms.modules.user.model.Role;
import cn.people.cms.modules.user.service.IMenuService;
import cn.people.cms.modules.user.service.IRoleService;
import cn.people.cms.util.json.JsonUtil;
import org.apache.commons.lang.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.QueryResult;
import org.nutz.dao.util.Daos;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true,rollbackFor = Exception.class)
public class RoleService extends BaseService<Role> implements IRoleService {


    @Autowired
    private IMenuService menuService;

    @Override
    public List<Role> findRoleByUserId(String userId) {
        return null;
    }


    /**
     * 在角色机构关联表中加入新的记录
     *
     * @param role
     */
//    private void officeRelation(Role role) {
//        if (!Lang.isEmpty(role.getDataScope())) {
//            List<Office> offices = new ArrayList<>();
//            Office office;
//            switch (role.getDataScope()) {
//                case 1:
//                    role.setOffices(officeService.listAll());
//                    dao.insertRelation(role, Role.OFFICES);
//                    break;
//                case 2:
//                    office = officeService.fetch(role.getOfficeId());
//                    if (office == null) {
//                        break;
//                    }
//                    offices.add(office);
//                    role.setOffices(offices);
//                    dao.insertRelation(role,  Role.OFFICES);
//                    break;
//                case 3:
//                    office = officeService.fetch(role.getOfficeId());
//                    offices.add(office);
//                    offices.addAll(dao.query(Office.class, Cnd.where("parent_ids", "LIKE", "%," + role.getOfficeId() + ",%")));
//                    role.setOffices(offices);
//                    dao.insertRelation(role,  Role.OFFICES);
//                    break;
//                case 4:
//                    //如果角色数据范围是个人数据，直接从个人创建的栏目做控制，不在角色机构关联表中插入数据
//                    break;
//            }
//        }
//    }

    /**
     * 根据用户id获取角色信息
     *
     * @param id
     * @return
     */
    @Override
    public Role fetch(Integer id) {
        Role role = super.fetch(id);
        if (!Lang.isEmpty(role)) {
            dao.fetchLinks(role,  Role.MENUS);
        }
        List<Integer> idList = new ArrayList<>();
        if (!Lang.isEmpty(role.getMenus())) {
            for (Menu menu : role.getMenus()) {
                idList.add(menu.getId());
            }
        }
        if (!Lang.isEmpty(role.getFrontSiteCategory())) {
            List list = JsonUtil.parseArray(role.getFrontSiteCategory(), Map.class);
            role.setFrontSC(list);
        }
        if (!Lang.isEmpty(role.getBackSiteCategory())) {
            List list = JsonUtil.parseArray(role.getBackSiteCategory(), Map.class);
            role.setBackSC(list);
        }
        role.setFrontSiteCategory(null);
        role.setBackSiteCategory(null);
        role.setMenuIds(idList);
        role.setMenus(null);
        return role;
    }

    /**
     * 增加角色
     *
     * @param oriRole
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Role save(Role oriRole) {
        if (null != oriRole.getFrontSC()) {
            String frontSC = JsonUtil.toJson(oriRole.getFrontSC());
            oriRole.setFrontSiteCategory(frontSC);
        }
        if (null != oriRole.getBackSC()) {
            String backSC = JsonUtil.toJson(oriRole.getBackSC());
            oriRole.setBackSiteCategory(backSC);
        }
        super.save(oriRole);
        Role role = fetch(oriRole.getName());
        //在关联表中加入新的记录
        if (!Lang.isEmpty(oriRole.getMenuIds())) {
            List<Menu> list = new ArrayList<>();
            for (Integer id : oriRole.getMenuIds()) {
                Menu menu = menuService.fetch(id);
                list.add(menu);
            }
            role.setMenus(list);
            dao.insertRelation(role,  Role.MENUS);
        }
        //前台站点频道
        if (null != oriRole.getFrontSC()) {
            saveSC(oriRole.getFrontSC(), role, 1);
        }
        //后台站点频道
        if (null != oriRole.getBackSC()) {
            saveSC(oriRole.getBackSC(), role, 2);
        }
        return role;
    }

    /**
     * 角色更新
     * @param role
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Role update(Role role) {
        //清除原有角色关联表中的记录
        Role oriRole = fetchLinks(role, Role.FRONT_SITE, Role.FRONT_CATEGORY, Role.BACK_SITE, Role.BACK_CATEGORY, Role.MENUS);
        if(!Lang.isEmpty(oriRole.getMenus())){
            dao.clearLinks(oriRole,Role.MENUS);
        }
        if(!Lang.isEmpty(oriRole.getFrontSiteList())){
            dao.clearLinks(oriRole,Role.FRONT_SITE);
        }
        if(!Lang.isEmpty(oriRole.getFrontCategoryList())){
            dao.clearLinks(oriRole,Role.FRONT_CATEGORY);
        }
        if(!Lang.isEmpty(oriRole.getBackSiteList())){
            dao.clearLinks(oriRole,Role.BACK_SITE);
        }
        if(!Lang.isEmpty(oriRole.getBackCategoryList())){
            dao.clearLinks(oriRole,Role.BACK_CATEGORY);
        }
        if (null != role.getFrontSC()) {
            String frontSC = JsonUtil.toJson(role.getFrontSC());
            role.setFrontSiteCategory(frontSC);
        }
        if (null != role.getBackSC()) {
            String backSC = JsonUtil.toJson(role.getBackSC());
            role.setBackSiteCategory(backSC);
        }
        Daos.ext(dao, FieldFilter.create(Role.class, "^name|remark|frontSiteCategory|backSiteCategory$")).update(role);
        //在关联表中加入新的记录
        if (!Lang.isEmpty(role.getMenuIds())) {
            List<Menu> list = new ArrayList<>();
            for (Integer id : role.getMenuIds()) {
                Menu menu = menuService.fetch(id);
                list.add(menu);
            }
            role.setMenus(list);
            dao.insertRelation(role,  Role.MENUS);
        }
        //前台站点频道
        if (null != role.getFrontSC()) {
            saveSC(role.getFrontSC(), role, 1);
        }
        //后台站点频道
        if (null != role.getBackSC()) {
            saveSC(role.getBackSC(), role, 2);
        }
        return fetch(role.getName());
    }

    /**
     * 保存站点频道关联表
     * @param list
     * @param role
     * @param type
     */
    private void saveSC(List<Map<String, String>> list, Role role, Integer type) {
        List<Site> allSites = new ArrayList<>();
        List<Category> allCategories = new ArrayList<>();
        if (null != list) {
            Iterator<Map<String, String>> iterator = list.iterator();
            while (iterator.hasNext()) {
                Map<String, String> map = iterator.next();
                String siteId = map.get("siteId");
                Site site = dao.fetch(Site.class, Integer.valueOf(siteId));
                if (null != site) {
                    allSites.add(site);
                }
                String[] categoryIds = map.get("categoryIds").split(",");
                for (int j = 0; j < categoryIds.length; j++) {
                    if(StringUtils.isBlank(categoryIds[j])){
                        continue;
                    }
                    Category category = dao.fetch(Category.class, Integer.valueOf(categoryIds[j]));
                    if (null != category) {
                        allCategories.add(category);
                    }
                }
            }
        }
        if (type == 1) {
            //前台关联
            role.setFrontSiteList(allSites);
            dao.insertRelation(role,  Role.FRONT_SITE);
            role.setFrontCategoryList(allCategories);
            dao.insertRelation(role,  Role.FRONT_CATEGORY);
        } else if (type == 2) {
            //后台关联
            role.setBackSiteList(allSites);
            dao.insertRelation(role,  Role.BACK_SITE);
            role.setBackCategoryList(allCategories);
            dao.insertRelation(role,  Role.BACK_CATEGORY);
        }
    }

    /**
     * 伪删除角色
     *
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int vDelete(Integer id) {
        return super.vDelete(id);
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public int updateIgnoreNull(Role role) {
//        //清除原有的角色机构关联表中的记录
////        Role oriRole = dao.fetchLinks(fetch(role.getId()),  Role.OFFICES);
////        if (!Lang.isEmpty(oriRole.getOffices())) {
////            dao.clearLinks(oriRole,  Role.OFFICES);
////        }
//
//        //清除原有的角色菜单关联表中的记录
//        Role oriRole = dao.fetchLinks(fetch(role.getId()),  Role.MENUS);
//        if (!Lang.isEmpty(oriRole.getMenus())) {
//            dao.clearLinks(oriRole,  Role.MENUS);
//        }
//
//        //在角色机构关联表中加入新的记录
////        officeRelation(role);
//
//        //在角色菜单关联表中加入新的记录
//        if (!Lang.isEmpty(role.getMenuIds())) {
//            List<Menu> list = new ArrayList<>();
//            for (Integer integer : role.getMenuIds()) {
//                Menu menu = menuService.fetch(integer);
//                list.add(menu);
//            }
//            role.setMenus(list);
//            dao.insertRelation(role,  Role.MENUS);
//        }
//
//        int flag = super.updateIgnoreNull(role);
//        return flag;
//    }

    @Override
    public List<Role> listAll() {
        List<Role> allRoles = query(null, Cnd.where(BaseEntity.FIELD_STATUS, "<", BaseEntity.STATUS_DELETE));
        return allRoles;
    }

    /**
     * 获取角色列表并分页
     *
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @Override
    public QueryResult listPage(Integer pageNumber, Integer pageSize) {
        return listPage(pageNumber, pageSize, Cnd.where(BaseEntity.FIELD_STATUS, "<", BaseEntity.STATUS_DELETE), "^id|name|remark$");
    }

    @Override
    public Set<Integer> getCategoryIds(List<Role> roles, Integer type) {
        Set<Integer> categoryIds = new HashSet();
        roles.forEach(role -> {
            if (type == 1) { //1代表前台，2代表后台
                //dao.fetchLinks(role, Role.FRONT_SITE);
                fetchLinks(role, Role.FRONT_SITE, Role.FRONT_CATEGORY);
                if(!Lang.isEmpty(role.getFrontCategoryList())){
                    Set<Integer> temp = role.getFrontCategoryList().stream().filter(category -> category.getDelFlag()!=null && Category.STATUS_ONLINE==category.getDelFlag())
                            .map(category -> category.getId()).collect(Collectors.toSet());
                    categoryIds.addAll(temp);
                }
            } else {
                //dao.fetchLinks(role, Role.BACK_SITE);
                fetchLinks(role, Role.FRONT_SITE, Role.BACK_CATEGORY);
                if(!Lang.isEmpty(role.getBackCategoryList())){
                    Set<Integer> temp = role.getBackCategoryList().stream().filter(category -> category.getDelFlag()!=null && Category.STATUS_ONLINE==category.getDelFlag())
                            .map(category -> category.getId()).collect(Collectors.toSet());
                    categoryIds.addAll(temp);
                }
            }
        });
        return categoryIds;
    }

    @Override
    public Set<Integer> getSiteIds(List<Role> roles, Integer type) {
        Set<Integer> siteIds = new HashSet();
        roles.forEach(role -> {  //1代表前台，2代表后台
            if (type == 1) {
                dao.fetchLinks(role, Role.FRONT_SITE);
                if(!Lang.isEmpty(role.getFrontSiteList())){
                    Set<Integer> temp = role.getFrontSiteList().stream().filter(site -> site.getDelFlag()!=null && Site.STATUS_ONLINE==site.getDelFlag())
                            .map(site -> site.getId()).collect(Collectors.toSet());
                    siteIds.addAll(temp);
                }
            } else {
                dao.fetchLinks(role, Role.BACK_SITE);
                if(!Lang.isEmpty(role.getBackSiteList())){
                    Set<Integer> temp = role.getBackSiteList().stream().filter(site -> site.getDelFlag()!=null && Site.STATUS_ONLINE==site.getDelFlag())
                            .map(site -> site.getId()).collect(Collectors.toSet());
                    siteIds.addAll(temp);
                }
            }
        });
        return siteIds;
    }

    /**
     * 获取当前用户的角色列表
     *
     * @return
     */
    @Override
    public List<Role> getCurrentUserRole() {
//        User user = UserUtil.getUser();
//        if (user != null) {
//            return user.getRoleList();
//        }
        return null;
    }

    /**
     * @param role
     * @param params
     * @return
     */
    private Role fetchLinks(Role role,String... params){
        if(params!=null && params.length>0){
            for(String param:params){
                dao.fetchLinks(role, param);
            }
        }
        return role;
    }
}
