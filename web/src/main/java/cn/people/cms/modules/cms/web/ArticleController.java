package cn.people.cms.modules.cms.web;

import cn.people.cms.base.api.Result;
import cn.people.cms.modules.cms.model.Article;
import cn.people.cms.modules.cms.model.front.ArticleParamVO;
import cn.people.cms.modules.cms.model.front.ArticleVO;
import cn.people.cms.modules.cms.service.IArticleService;
import cn.people.cms.modules.cms.service.IHistoryDataService;
import cn.people.cms.modules.sys.model.Category;
import cn.people.cms.modules.sys.service.ICategoryService;
import cn.people.cms.modules.templates.service.ITemplateService;
import cn.people.cms.modules.util.HttpUtils;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文章管理
 * @author by lml on 2016/12/10.
 */
@Api(description = "文章管理(cms模块)")
@RestController
@RequestMapping("/api/cms/article")
@Slf4j
public class ArticleController {

    @Autowired
    private IArticleService articleService;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private IHistoryDataService historyDataService;
    @Autowired
    private ITemplateService templateService;

    @Value("${theone.phpPerformance.url}")
    private String phpPerformanceUrl;

    /**
     * 保存草稿，文章状态分两种，草稿和待审核，由前端传值更新状态
     */
    @RequestMapping(value = "/draft",method = RequestMethod.POST)
    @RequiresPermissions("cms:articles:edit")
    public Result draft(@RequestBody Article article) {
        try {
            Article out = (Article)articleService.draft(article) ;
            if(!Lang.isEmpty(out) ){
                return Result.success(out);
            }
            return Result.error("参数有误，保存失败");
        }catch (Exception ex){
            log.error(ex.getMessage(),ex);
            return Result.error("保存草稿失败");
        }
    }

    /**
     * 审核文章，文章状态分两种，审核通过和未通过，由前端传值更新状态
     * @param article
     * @return
     */
    @RequestMapping(value = "/audit",method = RequestMethod.POST)
    @RequiresPermissions("cms:articles:audit")
    public Result audit(@RequestBody Article article) {
        try {
            if(null !=  articleService.audit(article)){

                try {
                    Runnable runnable = () -> {
                        Article article1 = articleService.fetch(article.getId());
                        threadInsertArticle(article1);
                    };
                    new Thread(runnable).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return Result.success();
            }
            return Result.error("参数有误，保存失败");
        }catch (Exception ex){
            log.error(ex.getMessage(),ex);
            return Result.error("审核失败");
        }
    }

    private void threadInsertArticle(Article insert) {
        String url = templateService.getUrl(insert.getUrl());
        Map<String,Object> map=new HashMap<>();
        map.put("id",insert.getId());
        map.put("title",insert.getTitle());
        map.put("url",url);
        map.put("type",insert.getType());
        map.put("channelId",insert.getCategoryId());

        Category category = categoryService.fetch(insert.getCategoryId());
        if (category != null) {
            map.put("siteId",category.getSiteId());
        }
        map.put("commentCount",insert.getComments());
        map.put("likeCount",insert.getLikes());
        map.put("shareCount",insert.getHits());
        map.put("creatorId",insert.getCreateBy());
        map.put("zrbj",insert.getResponsibleUser());
        map.put("author",insert.getAuthors());
        map.put("createtime",insert.getCreateAt());
        String jsonString = JSON.toJSONString(map);
        HttpUtils.doPost(phpPerformanceUrl+"/articles",jsonString);
    }

   @RequestMapping(value = "/source",method = RequestMethod.POST)
   @RequiresPermissions("cms:articles:edit")
    public Result source(@RequestBody Article article) {
        try {
            Article out = (Article)articleService.source(article) ;
            if(!Lang.isEmpty(out)){
                return Result.success(out);
            }
            return Result.error("参数有误，保存失败");
        }catch (Exception ex){
            log.error(ex.getMessage(),ex);
            return Result.error("保存草稿失败");
        }
    }

    @RequestMapping(value = "/source", method = RequestMethod.GET)
    @RequiresPermissions("cms:articles:view")
    public Result sourceList(ArticleVO article) {
        return Result.success(articleService.sourceList(article));
    }

    /**
     * 查询完整列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @RequiresPermissions("cms:articles:view")
    public Result list(ArticleVO article) {
        return Result.success(articleService.findSearchPage(article));
    }


    @GetMapping(value = "/history/{id}")
    @RequiresPermissions("cms:articles:view")
    public Result history(@PathVariable Integer id){
        try {
            return Result.success(historyDataService.listPage(id));
        }catch (Exception ex){
            log.error(ex.getMessage(),ex);
            return Result.error("保存草稿失败");
        }
    }

    /**
     * 保存文章
     */
    @RequestMapping(method = RequestMethod.POST)
    @RequiresPermissions("cms:articles:audit")
    public Result save(@RequestBody Article article) {
        try {
            //上线之后的修改，url不更新
            article.setUrl(null);
            if (!Lang.isEmpty(articleService.save(article))) {
                //update 效能
                try {
                    try {
                        Runnable runnable = () -> {
                            threadUpdateArticle(article);
                        };
                        new Thread(runnable).start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //=====================================================================================================================
                List<Article> list = articleService.updateReferenceArticles(article);
                if(!Lang.isEmpty(list)){
                    list.forEach(des ->  {
                        try {
                        articleService.flushArticleAndCategory(des,null);
                    }catch (Exception ex){
                        log.error(ex.getMessage());
                    }});
                }
                return Result.success();
            }
            return Result.error("存储失败");
        }catch (Exception ex){
            log.error(ex.getMessage(),ex);
            return Result.error("保存文章失败");
        }
    }

    /**
     * 文章详情（带扩展字段）
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @RequiresPermissions("cms:articles:view")
    public Result view(@PathVariable Integer id) {
        if (id == null) {
            return Result.error("传入参数异常");
        }
        Article article = articleService.getArticleDetails(id);
        if (null == article) {
            return Result.error("获得的文章为空");
        }
        return Result.success(article);
    }

    /**
     * 上下线
     */
    @RequestMapping(value = "/onOff/{id}", method = RequestMethod.GET)
    @RequiresPermissions("cms:articles:audit")
    public Result OnOff(@PathVariable Integer id) {
        try {
            Article article = articleService.changeOnlineStatus(id);
            if(null != article){
                categoryService.dynamicUpdateById(categoryService.getSimpleCategory(article.getCategoryId()),null);
                return Result.success();
            }else {
                return Result.error("参数有误，上/下线失败");
            }
        }catch (Exception ex){
            log.error(ex.getMessage(),ex);
            return Result.error("上/下线失败");
        }
    }

    /**
     * 删除文章
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @RequiresPermissions("cms:articles:edit,cms:articles:audit")
    public Result delete(@PathVariable Integer id) {
        if(articleService.vDelete(id)>0){
            try {
                Runnable runnable = () -> {
                    HttpUtils.doDelete(phpPerformanceUrl+"/articles/"+id);
                };
                new Thread(runnable).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Result.success();
        }
        return Result.error("删除失败");
    }

    /**
     * 批量切换上下线
     */
    @RequestMapping(value = "/batchOnOff", method = RequestMethod.POST)
    @RequiresPermissions("cms:articles:audit")
    public Result batchOnOff(@RequestParam String articleIds) {
        List<String> ids = null;
        if (StringUtils.isNotEmpty(articleIds)) {
            ids = Arrays.asList(articleIds.split(","));
        }
        if (null != ids && ids.size() > 0) {
            for(int i=0;i<ids.size();i++){
                Integer id = Integer.valueOf(ids.get(i));
                Article article = articleService.changeOnlineStatus(id);
                if(i == ids.size()-1){
                    categoryService.dynamicUpdateById(categoryService.getSimpleCategory(article.getCategoryId()),null);
                }
            }
        }
        return Result.success();
    }


    @RequestMapping(value = "/stick", method = RequestMethod.PATCH)
    @RequiresPermissions("cms:articles:edit,cms:articles:audit")
    public Result stick(@RequestBody Article article){
        if(Lang.isEmpty(article) || article.getId()==null){
            return Result.error(-1,"更新的文章不存在");
        }
        articleService.updateIgnoreNull(article);
        return Result.success();
    }

    @RequestMapping(value = "/preview/{id}", method = RequestMethod.GET)
    @RequiresPermissions("cms:articles:edit,cms:articles:audit")
    public Result preview(@PathVariable Integer id){
        try {
            Article article = articleService.getArticleDetails(id);
            String url =articleService.flushArticleAndCategory(article,"preview");
            return Result.success(templateService.getUrl(url));
        }catch (Exception ex){
            log.error(ex.getMessage(),ex);
            return Result.error("预览失败");
        }
    }


    @RequestMapping(value = "/batchSort", method = RequestMethod.POST)
    @RequiresPermissions("cms:articles:edit,cms:articles:audit")
    public Result batchSort(@RequestBody List<Article> list) {
        if(list == null || list.size() ==0){
            return Result.success();
        }
        articleService.batchSort(list);
        return Result.success();
    }

    @RequestMapping(value = "/batchDelete", method = RequestMethod.POST)
    @RequiresPermissions("cms:articles:edit,cms:articles:audit")
    public Result batchDelete(@RequestBody List<Integer> ids) {
        if(!Lang.isEmpty(ids)){
            if(null!=ids && ids.size()>0){
                ids.forEach(id->articleService.delete(id));
            }
            return Result.success("数据为空");
        }
        return Result.success();
    }

    @RequestMapping(value = "/batchDeleteSource", method = RequestMethod.POST)
    @RequiresPermissions("cms:articles:edit,cms:articles:audit")
    public Result batchDeleteSource(@RequestBody List<Integer> ids) {
        if(!Lang.isEmpty(ids)){
            if(null!=ids && ids.size()>0){
                ids.forEach(id->articleService.deleteBatchSource(id,Article.STATUS_SOURCE));
            }
            return Result.success("数据为空");
        }
        return Result.success();
    }


    /**
     * 批量提交审核
     * @param articleIds
     * @return
     */
    @RequestMapping(value = "/batchAuditPublish")
    @RequiresPermissions("cms:articles:edit,cms:articles:audit")
    public Result batchAuditPublish(@RequestParam String articleIds){
        if(StringUtils.isEmpty(articleIds)){
            return Result.error("参数有误");
        }
        articleService.batchPublish(articleIds);
        return Result.success();
    }


    @RequestMapping(value = "/publish", method = RequestMethod.POST)
    @RequiresPermissions("cms:articles:edit,cms:articles:audit")
    public Result batchPublish(@RequestBody ArticleParamVO article){
        if(null == article.getArticle()){
            return Result.error("参数有误");
        }
        articleService.distributeArticles(article.getArticle(),article.getList());
        return Result.success();
    }

    /**
     * 从稿源库选择文章
     * @param list
     * @return
     */
    @RequestMapping(value = "/source/import", method = RequestMethod.POST)
    @RequiresPermissions("cms:articles:edit,cms:articles:audit")
    public Result batchPublish(@RequestBody List<Article> list,@RequestParam Integer categoryId){
        if(!Lang.isEmpty(categoryId)){
            articleService.import2Draft(list,categoryId);
            return Result.success();
        }
        return Result.success("编号为空");
    }

    @RequestMapping(value = "/publish/cids", method = RequestMethod.GET)
    @RequiresPermissions("cms:articles:edit,cms:articles:audit")
    public Result publishCid(@RequestParam Integer id){
        return Result.success(articleService.publishCid(id));
    }

    private void threadUpdateArticle(Article article) {
        Map<String,Object> map=new HashMap<>();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(article.getTitle())){
            map.put("title",article.getTitle());
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(article.getLink())){
            map.put("url",article.getLink());
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(article.getType())){
            map.put("type",article.getType());
        }
        if (article.getCategoryId()!=null && article.getCategoryId()!=0){
            map.put("channelId",article.getCategoryId());

            Category category = categoryService.fetch(article.getCategoryId());
            if (category != null)
                map.put("siteId",category.getSiteId());
        }
        if (article.getComments()!=null && article.getComments()!=0){
            map.put("commentCount",article.getComments());
        }
        if (article.getLikes()!=null && article.getLikes()!=0){
            map.put("likeCount",article.getLikes());
        }
        if (article.getHits()!=null && article.getHits()!=0){
            map.put("shareCount",article.getHits());
        }
        if (article.getCreateBy()!=null && article.getCreateBy()!=0){
            map.put("creatorId",article.getCreateBy());
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(article.getResponsibleUser())){
            map.put("zrbj",article.getResponsibleUser());
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(article.getAuthors())){
            map.put("author",article.getAuthors());
        }
        String jsonString = JSON.toJSONString(map);
        HttpUtils.doPut(phpPerformanceUrl+"/articles/"+article.getId(),jsonString);
    }

}
