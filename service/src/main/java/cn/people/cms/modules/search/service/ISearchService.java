package cn.people.cms.modules.search.service;

import cn.people.cms.modules.cms.model.Article;

public interface ISearchService {


    public void buildIndex(Integer articleId,Article article);



    public void updateIndex(Integer articleId,Article article);


    public void deleteIndex(Integer articleId);
}
