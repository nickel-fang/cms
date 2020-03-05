package cn.people.cms.modules.cms.service;

import cn.people.cms.base.service.IBaseService;
import cn.people.cms.modules.cms.model.Site;
import cn.people.cms.modules.cms.model.front.SiteVO;
import org.nutz.dao.QueryResult;

import java.util.List;

/**
 * Created by lml on 2016/12/26.
 */
public interface ISiteService extends IBaseService<Site> {
    QueryResult findByVO(SiteVO siteVO);
    List all(Boolean role);
}
