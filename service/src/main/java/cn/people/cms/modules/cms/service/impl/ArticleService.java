package cn.people.cms.modules.cms.service.impl;

import cn.people.cms.base.dao.BaseDao;
import cn.people.cms.base.service.impl.BaseService;
import cn.people.cms.entity.BaseEntity;
import cn.people.cms.modules.cms.model.Article;
import cn.people.cms.modules.cms.model.ArticleData;
import cn.people.cms.modules.cms.model.ArticleMeta;
import cn.people.cms.modules.cms.model.front.ArticleMediaVO;
import cn.people.cms.modules.cms.model.front.ArticleVO;
import cn.people.cms.modules.cms.model.front.MediaResourceVO;
import cn.people.cms.modules.cms.model.type.ArticleType;
import cn.people.cms.modules.cms.service.IArticleDataService;
import cn.people.cms.modules.cms.service.IArticleService;
import cn.people.cms.modules.cms.service.IHistoryDataService;
import cn.people.cms.modules.file.model.MediaInfo;
import cn.people.cms.modules.file.service.IMediaInfoService;
import cn.people.cms.modules.search.service.ISearchService;
import cn.people.cms.modules.sys.model.Category;
import cn.people.cms.modules.sys.service.ICategoryService;
import cn.people.cms.modules.templates.model.Template;
import cn.people.cms.modules.templates.service.ITemplateService;
import cn.people.cms.modules.user.model.User;
import cn.people.cms.modules.user.service.IUserService;
import cn.people.cms.modules.util.HttpUtils;
import cn.people.cms.util.base.ShiroUtils;
import cn.people.cms.util.http.OKHttpUtil;
import cn.people.cms.util.mapper.BeanMapper;
import cn.people.cms.util.time.ClockUtil;
import cn.people.domain.IUser;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Criteria;
import org.nutz.dao.sql.OrderBy;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by lml on 2016/12/10.
 */
@Slf4j
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class ArticleService extends BaseService<Article> implements IArticleService {

    @Autowired
    private BaseDao dao;
    @Lazy
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private IArticleDataService articleDataService;
    @Autowired
    private IMediaInfoService mediaInfoService;
    @Autowired
    private ITemplateService templateService;
    @Autowired
    private IUserService userService;

    @Value("${theone.project.rootId}")
    private Integer rootId;

    @Value("${theone.forums.url}")
    private String forumsUrl;

    @Autowired
    private ISearchService searchService;


    @Lazy
    @Autowired
    private IHistoryDataService historyDataService;
    @Value("${theone.freemarker.switch}")
    private Boolean templateSwitch;

    @Value("${theone.phpPerformance.url}")
    private String phpPerformanceUrl;

    /**
     * 文章列表页面搜索查询
     */
    @Override
    public QueryResult findSearchPage(ArticleVO articleVO) {
        QueryResult result = getSearchResult(articleVO);
        if (result == null) {
            return null;
        }
        List<Article> list = (List<Article>) result.getList();
        if (!Lang.isEmpty(list)) {
            list.stream().forEach(article -> {
                Category category = dao.fetch(Category.class, article.getCategoryId());
                if (category != null) {
                    article.setCategoryName(category.getName());
                }
                article.setUrl(templateService.getUrl(article.getUrl()));
                if (ArticleType.AUDIO.value().equals(article.getType()) || ArticleType.VIDEO.value().equals(article.getType())) {
                    ArticleData data = dao.fetch(ArticleData.class, article.getArticleId());
                    if (StringUtils.isNotBlank(data.getImages())) {
                        article.setImageJson(JSONArray.parseArray(data.getImages(), ArticleMediaVO.class));
                    }
                    if (StringUtils.isNotBlank(data.getAudios())) {
                        article.setAudioJson(JSONArray.parseArray(data.getAudios(), ArticleMediaVO.class));
                    }
                    if (StringUtils.isNotBlank(data.getVideos())) {
                        article.setVideoJson(JSONArray.parseArray(data.getVideos(), ArticleMediaVO.class));
                    }
                }
            });
        }
        return result;
    }

    /**
     * 拼接搜索条件
     *
     * @param article
     * @return
     */
    private QueryResult getSearchResult(ArticleVO article) {
        String[] categoryIds = null;
        if (article.getCategoryId() != null && article.getCategoryId() > 0) {
            Integer categoryId = article.getCategoryId() == null ? 0 : article.getCategoryId();
            categoryIds = categoryIds(categoryId, article.getApiType());
        }
        if (article.getSiteId() != null && article.getSiteId() > 0 && (article.getCategoryId() == null || article.getCategoryId() == 0)) {
            List<String> categoryList = new ArrayList<String>();
            List<Category> list = categoryService.getTree(rootId, article.getSiteId(), null);
            for (Category category : list) {
                addCategoryId(categoryList, category);
            }
            categoryIds = categoryList.toArray(new String[categoryList.size()]);
        }
        Criteria criteria = Cnd.NEW().getCri();
        if (categoryIds == null || categoryIds.length == 0) {
            return null;
        }
        SqlExpressionGroup cnd;
        cnd = criteria.where().andIn(Article.Constant.CATEGORY_ID, categoryIds);
        //add by adili 20180607 （文章列表增加“全部”状态，排列按照“全部”、“草稿”、“待审核”、“待修改”、“已上线”、“已下线”的顺序排列）
        log.info("DelFlag:{}", article.getDelFlag());
        if ( article.getDelFlag() != null && 99 == Integer.valueOf(article.getDelFlag())) {
            cnd = criteria.where().andIn(BaseEntity.FIELD_STATUS, getAllStatus());
        } else {
            cnd = article.getDelFlag() == null ? cnd.and(BaseEntity.FIELD_STATUS, "=", BaseEntity.STATUS_ONLINE) : cnd.and(BaseEntity.FIELD_STATUS, " = ", article.getDelFlag());
        }
        cnd = article.getBeginTime() != null ? cnd.and(Article.Constant.CREATE_AT, ">=", article.getBeginTime()) : cnd;
        cnd = article.getEndTime() != null ? cnd.and(Article.Constant.CREATE_AT, "<=", article.getEndTime()) : cnd;
        cnd = StringUtils.isNotBlank(article.getTitle()) ? cnd.and(Article.Constant.TITLE, "like", "%" + article.getTitle().trim() + "%") : cnd;
        cnd = StringUtils.isNotBlank(article.getOperateUser()) ? cnd.and(Cnd.exps(Article.Constant.CREATE_USER, "like", article.getOperateUser().trim() + "%")
                .or(Article.Constant.AUDIT_USER, "like", article.getOperateUser().trim() + "%")) : cnd;
        OrderBy orderBy = criteria.getOrderBy();
        if (StringUtils.isNotBlank(article.getDesc())) {
            orderBy.desc(article.getDesc());
        }
        if (StringUtils.isNotBlank(article.getAsc())) {
            orderBy.asc(article.getAsc());
        }
        //增加默认排序
        orderBy.desc(Article.Constant.STICK).desc(Article.Constant.WEIGHT).desc(Article.Constant.PUBLISH_DATE).desc(Article.Constant.ID);
        return listPage(article.getPageNumber(), article.getPageSize(), criteria);
    }

    //获取全部文章状态
    private long[] getAllStatus() {
        long[] arr = new long[5];
        arr[0] = Article.STATUS_DRAFT;
        arr[1] = Article.STATUS_AUDIT;
        arr[2] = Article.STATUS_NO_AUDIT;
        arr[3] = Article.STATUS_ONLINE;
        arr[4] = Article.STATUS_OFFLINE;
        return arr;
    }

    private void addCategoryId(List<String> categoryList, Category category) {
        categoryList.add(String.valueOf(category.getId()));
        if (category.getChildren() != null) {
            for (Category category1 : category.getChildren()) {
                addCategoryId(categoryList, category1);
            }
        }
    }

    /**
     * 分页搜索文章在规定时间内的统计量
     *
     * @return
     */
    @Override
    public QueryResult listPage(Integer pageNumber, Integer pageSize, String startTime, String endTime, String type) {
        Sql sql = Sqls.create("select title, sum($type) as $type from cms_article where publish_date >= @startTime && publish_date <= @endTime group by title order by sum($type) desc");
        sql.vars().set("type", type);
        sql.params().set("startTime", startTime);
        sql.params().set("endTime", endTime);
        return super.listPage(pageNumber, pageSize, sql);
    }

    //TODO:临时处理,后面通过权限过滤
    private String[] categoryIds(Integer categoryId, Integer type) {
        List<Category> categories = new ArrayList<>();
        String[] categoryIds;
        IUser user = ShiroUtils.getUser();
        Set set;
        if (type == null) {
            User entity = BeanMapper.map(user, User.class);
            set = userService.getCategoryIds(entity, 2);
            if (Lang.isEmpty(set)) {
                return null;
            }
            List child = categoryService.getAllChildrenList(set, categories, categoryId);
            categories = child == null ? new ArrayList() : child;
        } else if (type == 0) {
            // set = categoryService.getFrontCategoryIds(user);
            List child = categoryService.getAllChildrenList(categories, categoryId);
            categories = child == null ? new ArrayList() : child;
        }
        if (categoryId != 0) {
            Category category = dao.fetch(Category.class, categoryId);
            if (category != null) {
                categories.add(category);//categories包含当前的栏目id及栏目下所有的子栏目
            }
        }
        categoryIds = categories.stream().map(category -> category.getId().toString()).collect(Collectors.toSet()).toArray(new String[categories.size()]);
        return categoryIds;
    }

    @Override
    public QueryResult findReferPage(ArticleVO articleVO) {
        Criteria criteria = Cnd.NEW().getCri();
        SqlExpressionGroup cnd = criteria.where();
        if (null == articleVO.getDelFlag()) {
            cnd.and(Article.FIELD_STATUS, "=", Article.STATUS_ONLINE);
        } else {
            cnd.and(Article.FIELD_STATUS, "=", articleVO.getDelFlag());
        }
        if (articleVO.getCategoryId() != null) {
            cnd.andIn(Article.Constant.CATEGORY_ID, categoryIds(articleVO.getCategoryId(), articleVO.getApiType()));
        }
        cnd = StringUtils.isNotBlank(articleVO.getTitle()) ? cnd.and(Article.Constant.TITLE, "like", "%" + "%" + articleVO.getTitle().trim() + "%") : cnd;
        criteria.getOrderBy().desc(Article.Constant.STICK).desc(Article.Constant.WEIGHT).desc(Article.Constant.PUBLISH_DATE).desc(Article.Constant.ID);
        QueryResult result = listPage(articleVO.getPageNumber(), articleVO.getPageSize(), criteria);
        List<Article> list = (List<Article>) result.getList();
        if (!Lang.isEmpty(list)) {
            list.forEach(article -> {
                Category category = dao.fetch(Category.class, article.getCategoryId());
                if (category != null) {
                    article.setCategoryName(category.getName());
                }
            });
        }
        return result;
    }

    @Override
    public Article getArticleDetails(Integer id) {
        //普通字段 关联调查和扩展字段
        Article article = fetch(id);
        article.setUrl(templateService.getUrl(article.getUrl()));
        article.setArticleData(articleDataService.fetch(id));
        if (article.getCategoryId() != null) {
            Category category = dao.fetch(Category.class, article.getCategoryId());
            if (category != null) {
                article.setCategoryName(category.getName());
            }
        }
        return article;
    }

    @Override
    public Article getArticleDetails(Article article) {
        if (article == null || article.getId() == null) {
            return null;
        }
        article.setUrl(templateService.getUrl(article.getUrl()));
        article.setArticleData(articleDataService.fetch(article.getId()));
        if (article.getCategoryId() != null) {
            Category category = dao.fetch(Category.class, article.getCategoryId());
            if (category != null) {
                article.setCategoryName(category.getName());
            }
        }
        return article;
    }

    @Override
    public QueryResult sourceList(ArticleVO article) {
        Cnd cnd = Cnd.where(Article.FIELD_STATUS, "=", Article.STATUS_SOURCE);
        if (!StringUtils.isBlank(article.getSource())) {
            cnd.and("source", "=", article.getSource());
        }
        if (!StringUtils.isBlank(article.getTitle())) {
            cnd.and("title", "like", "%" + article.getTitle() + "%");
        }

        if (article.getImportType() != null) {
            cnd.and("import_type", "=", article.getImportType());
        }

        if (article.getBeginTime() != null) {
            cnd.and(Article.Constant.CREATE_AT, ">=", article.getBeginTime());
        }
        if (article.getEndTime() != null) {
            cnd.and(Article.Constant.CREATE_AT, "<=", article.getEndTime());
        }
        return listPage(article.getPageNumber(), article.getPageSize(), cnd);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchSort(List<Article> list) {
        if (Lang.isEmpty(list)) {
            return;
        }
        list.forEach(article -> {
            dao.updateIgnoreNull(article);
            Article entity = dao.fetch(Article.class, article.getId());
            if (entity == null || entity.getCategoryId() == null) {
                return;
            }
            categoryService.dynamicUpdateById(categoryService.getSimpleCategory(entity.getCategoryId()), null);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object draft(Article article) {
        create(article);
        if (null == article.getDelFlag()) {
            article.setDelFlag(Article.STATUS_DRAFT);
        }
        save(article);
        updateReferenceArticles(article);
        return article;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object source(Article article) {
        create(article);
        article.setDelFlag(Article.STATUS_SOURCE);
        return save(article);
    }

    private void create(Article article) {
        if (article.getId() == null) {
            Integer userId = article.currentUid();
            article.setCreateBy(userId);
            article.setCreateAt(ClockUtil.currentDate());
            User user = userService.fetch(userId);
            article.setCreateUser(user.getUsername());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object audit(Article article) {
        Integer userId = article.currentUid();
        article.setAuditBy(userId);
        article.setAuditAt(ClockUtil.currentDate());
        User user = userService.fetch(userId);
        article.setAuditUser(user.getUsername());
        save(article);
        List<Article> list = updateReferenceArticles(article);
        list.forEach(des -> {
            try {
                flushArticleAndCategory(des, null);
                searchService.buildIndex(article.getId(), article);
                syncToBBS(article, "save");
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        });
        return article;
    }

    private void updateUser(Article article) {
        //更新操作用具
        IUser user = ShiroUtils.getUser();
        if (null != user) {
            article.setUpdateBy(user.getId());
            article.setUpdateUser(user.getUsername());
            article.setUpdateAt(ClockUtil.currentDate());
        }
    }

    /**
     * 保存（文章 详情 扩展字段）
     *
     * @param article
     */
    @Override
    @Transactional
    public Object save(Article article) {
        //添加引用的时候 articleId为引用文章的id
        ArticleData articleData = article.getArticleData();
        if(null == articleData){
            return null;
        }
        updateUser(article);
        //处理文章详情
        handlerData(article);
        // /设置默认标题
        if (StringUtils.isBlank(article.getListTitle())) {
            article.setListTitle(article.getTitle());
        }
        if(null != article.getDelFlag() &&  article.getDelFlag().equals(Article.STATUS_ONLINE )  && null==article.getPublishDate()){
            //上线则设置发布时间
            article.setPublishDate(ClockUtil.currentDate());
        }
        if (Lang.isEmpty(article.getId())) {
            article.init();
            dao.insert(article);
            article.setArticleId(article.getId());
            //插入文章历史库
            historyDataService.saveRecord(article);
            //更新默认权重
            if(article.getWeight()==null){
                article.setWeight(article.getId() );
            }
            dao.updateIgnoreNull(article);
            if (null == articleData.getId()) {
                articleData.setId(article.getId());
                dao.insert(articleData);
            }
            if (article.getDelFlag()==Article.STATUS_ONLINE) { //审核通过后
                searchService.buildIndex(article.getId(),article);
                syncToBBS(article,"save");
            }
            //插入扩展字段
            if (!Lang.isEmpty(article.getMetas())) {
                dao.insertLinks(article,Article.Constant.MATAS);
            }

        } else {
            Article article1 = update(article);
            if (article.getDelFlag().equals(Article.STATUS_ONLINE )) {
                searchService.updateIndex(article.getId(),article);
                syncToBBS(article,"edit");
            }
            //更新文章
            return article1;
        }
        return article;
    }


    private void handlerData(Article article) {
        ArticleData articleData = article.getArticleData();
        //文章内容处理
        if (!Lang.isEmpty(articleData.getContent())) {
            articleData.setContent(StringEscapeUtils.unescapeHtml4(articleData.getContent()));
        }
        //文章图片或音频视频处理
        saveMediaInfo(articleData, article.getMediaIds(), article.getAudioUrl(), article.getAudioCover(), article.getVideoUrl(), article.getVideoCover());
    }

    @Transactional
    public Article update(Article article) {
        //插入文章历史库
        historyDataService.saveRecord(article);
        //插入扩展字段
        dao.deleteLinks(dao.fetchLinks(fetch(article.getId()), Article.Constant.MATAS), Article.Constant.MATAS);
        dao.insertLinks(article, Article.Constant.MATAS);
        ArticleData articleData = article.getArticleData();
        if (!Lang.isEmpty(articleData) && !Lang.isEmpty(articleData.getId())) {
            dao.update(articleData);
        } else if (!Lang.isEmpty(articleData)) {
            articleData.setId(article.getId());
            dao.insert(articleData);
        }
        dao.updateIgnoreNull(article);
        return article;
    }

    /**
     * 反转文章上线状态
     *
     * @param id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Article changeOnlineStatus(Integer id) {
        Article article = dao.fetch(tClass, id);
        if (article == null || null == article.getDelFlag()) {
            return null;
        }
        if (Article.STATUS_AUDIT == article.getDelFlag() || Article.STATUS_OFFLINE == article.getDelFlag()) {
            article.setDelFlag(Article.STATUS_ONLINE);
            //每一次上线都更新发布时间
            article.setPublishDate(ClockUtil.currentDate());
            article.setArticleData(dao.fetch(ArticleData.class, id));
            //引用文章更新
            List<Article> list = updateReferenceArticles(article);
            if (!Lang.isEmpty(list)) {
                list.forEach(des -> {
                    try {
                        flushArticleAndCategory(des, null);
                        searchService.buildIndex(article.getId(), article);
                        syncToBBS(article, "onOffLine");
                    } catch (Exception ex) {
                        log.error(ex.getMessage());
                    }
                });
            }
            //================================上线新增文章到效能
            /*try {
                Runnable runnable = () -> {
                    threadInsertArticle(article);
                };
                new Thread(runnable).start();
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        } else if (Article.STATUS_ONLINE == article.getDelFlag()) {
            article.setDelFlag(Article.STATUS_OFFLINE);
            article.setUrl(null);
            //============================================下线调效能
            try {
                Runnable runnable = () -> {
                    HttpUtils.doDelete(phpPerformanceUrl + "/articles/" + id);
                };
                new Thread(runnable).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            templateService.offLine(Template.ARTICLE_TEMPLATE_TYPE, id);
            searchService.deleteIndex(article.getId());
            syncToBBS(article, "onOffLine");
        }
        dao.updateIgnoreNull(article);
        return article;
    }

    //根据上线，下线，编辑等操作调用论坛接口
    public String syncToBBS(Article article1, String action) {
        Article myArticle = BeanMapper.map(article1, Article.class);
        log.info("当前的文章信息 : {}", myArticle);
        //判断是否允许同步
        if (null == myArticle.getAllowPost() || !"1".equals(myArticle.getAllowPost())) {
            log.info("该文章不需要同步到BBS:{}", myArticle);
            return null;
        }
        //判断当前频道与BBS论坛是否绑定
        //根据文章id 获取 category （频道）的信息，再用
        Category myCategory = categoryService.fetch(myArticle.getCategoryId());
        //文章所在 频道与BSS 未进行绑定，则不同步
        if (null == myCategory) {
            log.info("未查到频道信息：{}", myCategory);
            return null;
        }
        //BBS 已绑定的话，存储BBS 对应的板块ID
        if (null == myCategory.getBbsId() || "".equals(myCategory.getBbsId())) {
            log.info("文章所在频道与BBS没有进行绑定:{}", JSONObject.toJSONString(myCategory));
            return null;
        }
        String repStr = null;
        myArticle.setCategoryId(Integer.valueOf(myCategory.getBbsId())); //暂存储BBS 板块ID
        log.info("对应的论坛板块ID：{}",myArticle.getCategoryId());
        //下线  -> 论坛 删除接口
        if ("onOffLine".equals(action)) {
            //cms 操作下线  bbs 调用删除接口
            if (Article.STATUS_OFFLINE == myArticle.getDelFlag()) {
                try {
                    //修改接口
                    repStr = OKHttpUtil.httpGet(forumsUrl + "/api.php?mod=post&action=del&articleId=" + myArticle.getId());
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            // cms 进行上线操作， bbs 进行修改
            if (Article.STATUS_ONLINE == myArticle.getDelFlag()) {
                try {
                    repStr = OKHttpUtil.httpPost(forumsUrl + "/api.php?mod=post&action=edit", JSONObject.toJSONString(myArticle));
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        // 在线状态的 修改
        if (Article.STATUS_ONLINE == myArticle.getDelFlag()) {
            //审核通过后进行保存，调用BBS
            if ("save".equals(action)) {
                try {
                    repStr = OKHttpUtil.httpPost(forumsUrl + "/api.php?mod=post&action=add", JSONObject.toJSONString(myArticle));
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            //编辑接口
            if ("edit".equals(action)) {
                try {
                    repStr = OKHttpUtil.httpPost(forumsUrl + "/api.php?mod=post&action=edit", JSONObject.toJSONString(myArticle));
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        log.info("syncToBBS-->当前操作：" + action + " 论坛返回内容:{}", repStr);
        return repStr;
    }

    @Override
    @Transactional(readOnly = false,rollbackFor = Exception.class)
    public void batchPublish(String articleIds) {
        log.info("into method batchPublish:{}",articleIds);
        for (String articleId : articleIds.split(",")) {
            try {

                Article article = fetch(Integer.parseInt(articleId));
                //“1草稿”状态栏增加“批量提交审核”，“待修改”状态栏增加“批量提交审核”  CMS编辑反馈bug&需求 6-23
                if (article.getDelFlag() ==  Article.STATUS_DRAFT || article.getDelFlag() == Article.STATUS_NO_AUDIT ) {
                    article.setDelFlag(Article.STATUS_AUDIT);
                    dao.updateIgnoreNull(article);
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    /**
     * 可扩展字段保存
     *
     * @param articleId
     * @param fieldCode
     * @param fieldValue
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMetaData(Integer articleId, String fieldCode, String fieldValue) {
        ArticleMeta articleMeta = new ArticleMeta();
        articleMeta.setArticleId(articleId);
        articleMeta.setFieldCode(fieldCode);
        articleMeta.setFieldValue(fieldValue);
        dao.insert(articleMeta);
    }

    /**
     * 为检索区域提供获取所有上线文章的接口（包含文章详情）
     *
     * @return
     */
    @Override
    public List<Article> getAll() {
        List<Article> articles = query(null, getDelFlag(Article.STATUS_ONLINE).and(Article.Constant.IS_REFERENCE, "=", false));
        articles.stream().forEach(article -> {
            dao.fetchLinks(article, "articleData");
        });
        return articles;
    }

    private void saveMediaInfo(ArticleData articleData, List<Integer> mediaIds, String audioUrl, String audioCover, String videoUrl, String videoCover) {
        List<ArticleMediaVO> videos = Lists.newArrayList();
        List<ArticleMediaVO> audios = Lists.newArrayList();
        if (!Lang.isEmpty(mediaIds)) {
            for (Integer mediaId : mediaIds) {
                MediaInfo mediaInfo = mediaInfoService.fetch(mediaId);
                if (Lang.isEmpty(mediaInfo)) {
                    continue;
                }
                if (Lang.isEmpty(mediaInfo.getType())) {
                    continue;
                }
                if (mediaInfo.getType().equals("video")) {
                    ArticleMediaVO mediaVO = new ArticleMediaVO();
                    mediaVO.setId(mediaId);
                    mediaVO.setTitle(mediaInfo.getName());
                    if (StringUtils.isNotBlank(videoCover)) {
                        mediaVO.setImage(videoCover);
                    } else {
                        mediaVO.setImage(mediaInfo.getCover());
                    }
                    mediaVO.setType(mediaInfo.getType());
                    mediaVO.setTimes(mediaInfo.getDuration());
                    List<MediaResourceVO> resources = Lists.newArrayList();
                    MediaResourceVO ld = new MediaResourceVO();
                    ld.setEnctype("ld");
                    ld.setSize(mediaInfo.getSize());
                    ld.setUrl(mediaInfo.getFileUrl());
                    resources.add(ld);
                    mediaVO.setResources(resources);
                    videos.add(mediaVO);
                }
                if (mediaInfo.getType().equals("audio")) {
                    ArticleMediaVO mediaVO = new ArticleMediaVO();
                    mediaVO.setId(mediaId);
                    mediaVO.setTitle(mediaInfo.getName());
                    if (StringUtils.isNotBlank(audioCover)) {
                        mediaVO.setImage(audioCover);
                    }
                    mediaVO.setType(mediaInfo.getType());
                    mediaVO.setTimes(mediaInfo.getDuration());
                    List<MediaResourceVO> resources = Lists.newArrayList();
                    MediaResourceVO mp3Small = new MediaResourceVO();
                    MediaResourceVO mp3Big = new MediaResourceVO();
                    mp3Small.setEnctype("64");
                    mp3Small.setSize(mediaInfo.getMp3SmallSize());
                    mp3Small.setUrl(mediaInfo.getMp3SmallUrl());
                    mp3Big.setEnctype("128");
                    mp3Big.setSize(mediaInfo.getMp3BigSize());
                    mp3Big.setUrl(mediaInfo.getMp3BigUrl());
                    resources.add(mp3Small);
                    resources.add(mp3Big);
                    mediaVO.setResources(resources);
                    audios.add(mediaVO);
                }
            }
        } else if (StringUtils.isNotBlank(audioUrl)) {
            ArticleMediaVO mediaVO = new ArticleMediaVO();
            if (StringUtils.isNotBlank(audioCover)) {
                mediaVO.setImage(audioCover);
            }
            mediaVO.setType("audio");
            List<MediaResourceVO> resources = Lists.newArrayList();
            MediaResourceVO mp3Small = new MediaResourceVO();
            MediaResourceVO mp3Big = new MediaResourceVO();
            mp3Small.setEnctype("64");
            mp3Small.setUrl(audioUrl);
            mp3Big.setEnctype("128");
            mp3Big.setUrl(audioUrl);
            resources.add(mp3Small);
            resources.add(mp3Big);
            mediaVO.setResources(resources);
            audios.add(mediaVO);
        } else if (StringUtils.isNotBlank(videoUrl)) {
            ArticleMediaVO mediaVO = new ArticleMediaVO();
            if (StringUtils.isNotBlank(videoCover)) {
                mediaVO.setImage(videoCover);
            }
            mediaVO.setType("video");
            List<MediaResourceVO> resources = Lists.newArrayList();
            MediaResourceVO ld = new MediaResourceVO();
            MediaResourceVO sd = new MediaResourceVO();
            MediaResourceVO hd = new MediaResourceVO();
            ld.setEnctype("ld");
            ld.setUrl(videoUrl);
            sd.setEnctype("sd");
            sd.setUrl(videoUrl);
            hd.setEnctype("hd");
            hd.setUrl(videoUrl);
            resources.add(ld);
            resources.add(sd);
            resources.add(hd);
            mediaVO.setResources(resources);
            videos.add(mediaVO);
        }
        if (!Lang.isEmpty(videos)) {
            articleData.setVideos(JSONArray.toJSONString(videos));
        }
        if (!Lang.isEmpty(audios)) {
            articleData.setAudios(JSONArray.toJSONString(audios));
        }
        if (!Lang.isEmpty(articleData.getImageJson())) {
            articleData.setImages(JSONArray.toJSONString(articleData.getImageJson()));
        }
    }

    /**
     * 异步生成文章静态html
     *
     * @param article
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String asyncUpdateDetail(Article article, String type) {
        if (templateSwitch) {
            try {
                //更新文章再更新频道页
                if (type == null && Article.STATUS_ONLINE != article.getDelFlag()) {
                    return null;
                }
                return dynaticUpdate(article, type);
            } catch (Exception ex) {
                log.error(article.getId() + "生成静态html失败");
            }
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String asyncUpdateDetail(Article article) {
        return asyncUpdateDetail(article, null);
    }

    /**
     * 手动更新某个栏目下所有的文章详情
     *
     * @param categoryId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void manuallyUpdate(Integer categoryId) {
        if (categoryId == null) {
            return;
        }
        List<Article> articles = dao.query(Article.class, Cnd.where(Article.Constant.CATEGORY_ID, "=", categoryId).and(Article.FIELD_STATUS, "=", Article.STATUS_ONLINE));
        if (!Lang.isEmpty(articles)) {
            articles.forEach(article -> {
                ArticleData articleData = dao.fetch(ArticleData.class, article.getId());
                if (articleData == null) {
                    return;
                }
                article.setArticleData(articleData);
                asyncUpdateDetail(article);
            });
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Article> updateReferenceArticles(Article article) {
        List referenceArticles = new ArrayList();
        if (article.getId() == null || article.getArticleId() == null) {
            return referenceArticles;
        }
        referenceArticles.add(article);
        List<Article> list = dao.query(Article.class, Cnd.where("article_id", "=", article.getArticleId()).and("id", "!=", article.getId()));
        if (Lang.isEmpty(list)) {
            return referenceArticles;
        }
        list.forEach(source -> {
            try {
                Article desArticle = BeanMapper.map(article, Article.class);
                desArticle.setCategoryId(source.getCategoryId());
                desArticle.setDelFlag(article.getDelFlag());
                desArticle.setSourceId(article.getSourceId());
                desArticle.setId(source.getId());
                desArticle.setUrl(source.getUrl());
                ArticleData desData = BeanMapper.map(article.getArticleData(), ArticleData.class);
                desData.setId(source.getId());
                dao.updateIgnoreNull(desArticle);
                dao.updateIgnoreNull(desData);
                desArticle.setArticleData(desData);
                referenceArticles.add(desArticle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return referenceArticles;
    }

    /**
     * 一稿多发
     *
     * @param article
     * @param categoryIds
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void distributeArticles(Article article, List<Integer> categoryIds) {
        if (article.getId() == null || article.getArticleId() == null) {
            return;
        }
        Set oldSet = publishCid(article.getArticleId());
        for (Integer categoryId : categoryIds) {
            if (categoryId.equals(article.getCategoryId())) {
                continue;
            }
            Article desArticle = BeanMapper.map(article, Article.class);
            desArticle.setArticleId(article.getArticleId());
            desArticle.setSourceId(article.getSourceId());
            desArticle.setCategoryId(categoryId);
            if (oldSet != null && oldSet.contains(categoryId)) {
                Article oriArticle = dao.fetch(Article.class, Cnd.where("category_id", "=", categoryId).and("article_id", "=", article.getArticleId()));
                //update
                desArticle.setId(oriArticle.getId());
                desArticle.setUpdateAt(new Date());
                desArticle.setDelFlag(oriArticle.getDelFlag());
                dao.updateIgnoreNull(desArticle);
                ArticleData desData = BeanMapper.map(article.getArticleData(), ArticleData.class);
                desData.setId(desArticle.getId());
                dao.updateIgnoreNull(desData);
            } else {
                //insert
                desArticle.setId(null);
                desArticle.setDelFlag(article.getDelFlag());
                create(desArticle);
                dao.insert(desArticle);
                ArticleData desData = BeanMapper.map(article.getArticleData(), ArticleData.class);
                desData.setId(desArticle.getId());
                dao.insert(desData);
            }
        }
        //删除未选择频道
        if (!Lang.isEmpty(oldSet)) {
            oldSet.removeAll(categoryIds);
            IUser user = ShiroUtils.getUser();
            User user1 = BeanMapper.map(user, User.class);
            Set<Integer> allIds = userService.getCategoryIds(user1, 2);
            oldSet.retainAll(allIds);
            if (!Lang.isEmpty(oldSet)) {
                oldSet.forEach(id -> dao.clear(Article.class, Cnd.where("article_id", "=", article.getArticleId()).and("category_id", "=", id)));
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void import2Draft(List<Article> sources, Integer categoryId) {
        if (Lang.isEmpty(sources)) {
            return;
        }
        sources.forEach(article -> {
            Article desArticle = BeanMapper.map(article, Article.class);
            desArticle.setCategoryId(categoryId);
            desArticle.setSourceId(article.getId());
            desArticle.setDelFlag(Article.STATUS_DRAFT);
            desArticle.setId(null);
            create(desArticle);
            dao.insert(desArticle);
            desArticle.setArticleId(desArticle.getId());
            dao.updateIgnoreNull(desArticle);
            ArticleData desData;
            if (null == article.getArticleData()) {
                ArticleData data = dao.fetch(ArticleData.class, article.getId());
                desData = BeanMapper.map(data, ArticleData.class);
            } else {
                desData = BeanMapper.map(article.getArticleData(), ArticleData.class);
            }

            if (null != desData) {
                desData.setId(desArticle.getId());
                dao.insert(desData);
            }
        });
    }

    @Override
    public Set<Integer> publishCid(Integer id) {
        //查找到数据库现在所有引用的文章,包括原文
        List<Article> articles = dao.query(Article.class, Cnd.where("article_id", "=", id).and("del_flag", "!=", Article.STATUS_DELETE));
        if (!Lang.isEmpty(articles)) {
            return articles.stream().map(a -> a.getCategoryId()).collect(Collectors.toSet());
        }
        return null;
    }


    /**
     * 当type为空的时候，更新在线的模板静态页，此时需要更新文章的url
     *
     * @param article
     * @param type
     * @return
     */
    @Override
    @Transactional
    public String dynaticUpdate(Article article, String type) {
        String idConst = "id";
        String articleConst = "article";
        if (article == null || article.getId() == null || article.getCategoryId() == null) {
            return null;
        }
        Category category = dao.fetchLinks(dao.fetchLinks(dao.fetch(Category.class, article.getCategoryId()),
                Category.PAGE_TEMPLATE), Category.TEMPLATE);

        Map map = new HashMap();
        map.put(idConst, article.getId());
        map.put(articleConst, article);
        String url = categoryService.generateTemplates(category.getTemplateId(), category, type, map);
        if (type == null) {
            article.setUrl(url);
        } else {
            article.setUrl(null);
        }
        dao.updateIgnoreNull(article);
        return url;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Integer id) {
        return dao.update(this.tClass, Chain.make(BaseEntity.FIELD_STATUS, Article.STATUS_DELETE), Cnd.where("id", "=", id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBatchSource(Integer id, Integer del_flag) {
        return dao.update(this.tClass, Chain.make(BaseEntity.FIELD_STATUS, Article.STATUS_DELETE), Cnd.where("id", "=", id).and(BaseEntity.FIELD_STATUS, "=", del_flag));
    }


    @Override
    public QueryResult simpleListPage(Integer categoryId) {
        Category category = dao.fetch(Category.class, categoryId);
        if (category == null) {
            return null;
        }
        Integer pageSize = category.getCardSize() == null ? Article.DEFAULT_PAGE_SIZE : category.getCardSize();
        QueryResult result = listPage(Article.DEFAULT_PAGE_NO, pageSize, Cnd.where("del_flag", "=", Article.STATUS_ONLINE)
                .and("category_id", "=", categoryId).desc(Article.Constant.STICK).desc(Article.Constant.WEIGHT).desc(Article.Constant.PUBLISH_DATE).desc(Article.Constant.ID));
        if (result != null) {
            if (!Lang.isEmpty(result.getList())) {
                List<Article> list = (List<Article>) result.getList();
                list.forEach(article -> {
                    getArticleDetails(article);
                });
            }
        }
        return result;
    }

    @Override
    /**
     * 更新正文静态页和列表静态页
     */
    @Transactional
    public String flushArticleAndCategory(Article article, String type) {
        String url = asyncUpdateDetail(article, type);
        categoryService.dynamicUpdateById(categoryService.getSimpleCategory(article.getCategoryId()), type);
        return url;
    }
}
