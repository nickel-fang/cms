package cn.people.cms.modules.cms.front;

import cn.people.cms.base.api.Result;
import cn.people.cms.modules.cms.model.Article;
import cn.people.cms.modules.cms.model.front.ArticleVO;
import cn.people.cms.modules.cms.service.IArticleService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by lml on 2018/5/18.
 */
@Api(description = "前端接口模块")
@RestController
@RequestMapping("/auth/front")
@Slf4j
public class FrontApiController {

    @Autowired
    private IArticleService articleService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result list(ArticleVO article) {
        article.setDelFlag(Article.STATUS_ONLINE);
        return Result.success(articleService.findSearchPage(article));
    }
}
