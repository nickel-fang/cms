package cn.people.cms.modules.cms.service;

import cn.people.cms.base.service.IBaseService;
import cn.people.cms.modules.cms.model.Article;
import cn.people.cms.modules.cms.model.front.ArticleVO;
import org.nutz.dao.QueryResult;

import java.util.List;

/**
 * Created by lml on 2016/12/22.
 */
public interface IArticleService extends IBaseService<Article> {

     Article getArticleDetails(Integer id);
     Article changeOnlineStatus(Integer id);
     void addMetaData(Integer articleId, String fieldCode, String fieldValue);
     QueryResult findSearchPage(ArticleVO articleVO);
     QueryResult listPage(Integer pageNumber, Integer pageSize, String startTime, String endTime, String type);
     List<Article> getAll();
     QueryResult findReferPage(ArticleVO article);
    void batchSort(List<Article> list);
     Object draft(Article article);
    Object audit(Article article);
    Object source(Article article);
    QueryResult simpleListPage(Integer categoryId);
    String dynaticUpdate(Article article,String type);
    String asyncUpdateDetail(Article article,String type);
    String asyncUpdateDetail(Article article);
    void manuallyUpdate(Integer categoryId);
    String flushArticleAndCategory(Article article,String type);
    List<Article> updateReferenceArticles(Article article);
    void distributeArticles(Article article,List<Integer> categoryIds);
    Article getArticleDetails(Article article);
    QueryResult sourceList(ArticleVO article);
    void import2Draft(List<Article> sources, Integer categoryId);
    Object publishCid(Integer id);
    int deleteBatchSource(Integer id,Integer del_flag);
    String syncToBBS(Article article,String action);

    void batchPublish(String articleIds);
}
