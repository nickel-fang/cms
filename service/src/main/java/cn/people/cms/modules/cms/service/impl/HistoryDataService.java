package cn.people.cms.modules.cms.service.impl;

import cn.people.cms.base.dao.BaseDao;
import cn.people.cms.modules.cms.model.Article;
import cn.people.cms.modules.cms.model.HistoryData;
import cn.people.cms.modules.cms.service.IHistoryDataService;
import cn.people.cms.modules.sys.model.Category;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by lml on 2018/3/26.
 */
@Service
@Transactional(rollbackFor = Exception.class,readOnly = true)
public class HistoryDataService  implements IHistoryDataService {

    @Autowired
    private BaseDao dao;

    @Async
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRecord(Article article){
        //稿源库过滤
        if(article.getDelFlag()!=null && article.getDelFlag()== Article.STATUS_SOURCE){
            return;
        }
        HistoryData data = new HistoryData(article);
        /*IUser user = UserUtil.getUser();
        if(null !=user){
            data.setUpdateBy(user.getId());
            data.setUpdateUser(user.getUsername());
        }*/
        dao.insert(data);
    }

    @Override
    public QueryResult listPage(Integer articleId){
        Pager pager = dao.createPager(Article.DEFAULT_PAGE_NO, Article.DEFAULT_PAGE_SIZE);
        Cnd cnd = Cnd.where("article_id","=",articleId);
        List<HistoryData> list = dao.query(HistoryData.class, cnd);
        pager.setRecordCount(dao.count(HistoryData.class, cnd));
        return new QueryResult(list, pager);
    }

    @Override
    public HistoryData fetch(Integer id){
        HistoryData data = dao.fetch(HistoryData.class,id);
        if(data.getCategoryId()!=null){
            Category category = dao.fetch(Category.class,data.getCategoryId());
            if(category!=null){
                data.setCategoryName(category.getName());
            }
        }
        return data;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteHistory(Integer id){
        List<HistoryData> list = dao.query(HistoryData.class,Cnd.where("article_id","=",id));
        if(null!=list && list.size()>0){
            list.forEach(historyData -> dao.delete(HistoryData.class,historyData.getId()));
        }
    }
}
