package cn.people.cms.modules.cms.service;

import cn.people.cms.modules.cms.model.Article;
import cn.people.cms.modules.cms.model.HistoryData;
import org.nutz.dao.QueryResult;

/**
 * Created by lml on 2018/3/26.
 */
public interface IHistoryDataService {

    QueryResult listPage(Integer articleId);
    void saveRecord(Article article);
    HistoryData fetch(Integer id);
    void deleteHistory(Integer id);
}
