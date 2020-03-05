package cn.people.cms.modules.search.web;


import cn.people.cms.base.api.Result;
import cn.people.cms.modules.cms.model.Article;
import cn.people.cms.modules.cms.service.IArticleService;
import cn.people.cms.util.http.OKHttpUtil;
import cn.people.cms.util.time.DateFormatUtil;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Api(description = "检索管理")
@RestController
@RequestMapping("/api/search")
@Slf4j
public class SearchController {


    @Resource
    private IArticleService articleService;


    @Value("${search.url}")
    private String url;

    @Value("${search.indexs}")
    private String indexs;

    @Value("${search.types}")
    private String types;

    @RequestMapping(value = "buildIndex",method = RequestMethod.GET)
    public Result list() {

        List<Article> list = articleService.getAll();
        for (Article article : list) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("title",article.getTitle());
                jsonObject.put("content",article.getArticleData().getContent());
                jsonObject.put("authors",article.getAuthors());
                jsonObject.put("listTitle",article.getListTitle());
                jsonObject.put("intro",article.getDescription());
                jsonObject.put("createTime", DateFormatUtil.formatDate(DateFormatUtil.PATTERN_DEFAULT_ON_SECOND,article.getCreateAt()));
                jsonObject.put("url",article.getUrl());
                OKHttpUtil.httpPut(url+"/api/index/"+indexs+"/"+types+"/"+article.getId(), jsonObject.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Result.success();
    }



}
