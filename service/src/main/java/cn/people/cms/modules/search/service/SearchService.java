package cn.people.cms.modules.search.service;

import cn.people.cms.modules.cms.model.Article;
import cn.people.cms.modules.cms.service.IArticleService;
import cn.people.cms.util.http.OKHttpUtil;
import cn.people.cms.util.time.DateFormatUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@Service
public class SearchService implements ISearchService{


    @Resource
    private IArticleService articleService;


    @Value("${search.url}")
    private String url;

    @Value("${search.indexs}")
    private String indexs;

    @Value("${search.types}")
    private String types;


    @Value("${theone.project.accessDomain}")
    private String accessDomain;

    @Override
    public void buildIndex(Integer articleId,Article article) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id",article.getId());
        jsonObject.put("title",article.getTitle());
        jsonObject.put("content",article.getArticleData().getContent());
        jsonObject.put("authors",article.getAuthors());
        jsonObject.put("listTitle",article.getListTitle());
        jsonObject.put("intro",article.getDescription());
        jsonObject.put("publishDate", DateFormatUtil.formatDate(DateFormatUtil.PATTERN_DEFAULT_ON_SECOND,article.getPublishDate()));
        jsonObject.put("url",accessDomain+"/"+article.getUrl());
        try {
            OKHttpUtil.httpPut(url+"/api/index/"+indexs+"/"+types+"/"+article.getId(), jsonObject.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void updateIndex(Integer articleId,Article article) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id",article.getId());
        jsonObject.put("title",article.getTitle());
        jsonObject.put("content",article.getArticleData().getContent());
        jsonObject.put("authors",article.getAuthors());
        jsonObject.put("listTitle",article.getListTitle());
        jsonObject.put("intro",article.getDescription());
        jsonObject.put("publishDate", DateFormatUtil.formatDate(DateFormatUtil.PATTERN_DEFAULT_ON_SECOND,article.getPublishDate()));
        jsonObject.put("url",accessDomain+article.getUrl());
        try {
            OKHttpUtil.httpPut(url+"/api/update/"+indexs+"/"+types+"/"+article.getId(), jsonObject.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteIndex(Integer articleId) {
        try {
            OKHttpUtil.httpGet(url+"/api/delete/"+indexs+"/"+types+"/"+articleId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
