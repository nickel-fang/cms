package cn.people.cms.modules.cms.service.impl;

import cn.people.cms.base.dao.BaseDao;
import cn.people.cms.base.service.impl.BaseService;
import cn.people.cms.modules.cms.model.Site;
import cn.people.cms.modules.cms.model.front.SiteVO;
import cn.people.cms.modules.cms.service.ISiteService;
import cn.people.cms.modules.sys.service.impl.CategoryService;
import cn.people.cms.modules.user.model.User;
import cn.people.cms.modules.user.service.impl.UserService;
import cn.people.cms.util.base.ShiroUtils;
import cn.people.cms.util.base.UserUtil;
import cn.people.cms.util.mapper.BeanMapper;
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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by lml on 2016/12/26.
 */
@Transactional(readOnly = true)
@Service
public class SiteService extends BaseService<Site> implements ISiteService {
    @Autowired
    private BaseDao dao;
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;

    @Override
    @Transactional
    public Object save(Site site) {
        boolean isInit = site.getId() == null;
        super.save(site);
        //如果是初始化则建一个同名的频道
        if(isInit){
            categoryService.initRootCategory(site);
        }
        return site;
    }

    @Override
    public Site fetch(Integer id) {
        return dao.fetchLinks(dao.fetch(Site.class,id),Site.TEMPLATE);
    }

    @Override
    public List all(Boolean role) {
        IUser user = ShiroUtils.getUser();
        List<Site> sites = dao.query(Site.class,Cnd.where("del_flag","=",Site.STATUS_ONLINE));
        if (null != role) {
            if(user.getId() == 3 && role){ //超级管理员或者安全保密管理员在角色管理中可以看到全部站点
                return sites;
            }
        }
        if(user.isAdmin(user.getId())){ //超级管理员或者安全保密管理员在角色管理中可以看到全部站点
            return sites;
        }
        User entity = BeanMapper.map(user, User.class);
        Set<Integer> accessIds = userService.getSiteIds(entity, 2);  //1代表前台，2代表后台  目前先全部返回后台的
        if(Lang.isEmpty(accessIds)){
            return null;
        }
        sites = sites.stream().filter(site->accessIds.contains(site.getId())).collect(Collectors.toList());
        return sites;
    }

    @Override
    public QueryResult findByVO(SiteVO siteVO) {
        IUser user = UserUtil.getUser();
        User entity = BeanMapper.map(user, User.class);
        Set<Integer> accessIds = userService.getSiteIds(entity,2);
        if(Lang.isEmpty(accessIds)){
            return null;
        }
        String[] includes= accessIds.stream().map(a->a.toString()).collect(Collectors.toSet()).toArray(new String[accessIds.size()]);
        Criteria criteria = Cnd.NEW().getCri();
        SqlExpressionGroup cnd = criteria.where().andIn("id", includes);
        cnd.and("del_flag","<",Site.STATUS_DELETE);
        if(StringUtils.isNotBlank(siteVO.getName())){
            cnd.and("name","like","%"+siteVO.getName()+"%");
        }
        return listPage(siteVO.getPageNumber(),siteVO.getPageSize(),criteria);
    }
}
