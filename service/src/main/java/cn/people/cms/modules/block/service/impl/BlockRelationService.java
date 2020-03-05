package cn.people.cms.modules.block.service.impl;

import cn.people.cms.base.service.impl.BaseService;
import cn.people.cms.modules.block.model.ArticleItem;
import cn.people.cms.modules.block.model.BlockRelation;
import cn.people.cms.modules.block.model.MenuItem;
import cn.people.cms.modules.block.model.VO.BlockRelationVO;
import cn.people.cms.modules.block.service.IBlockRelationService;
import cn.people.cms.modules.cms.model.Article;
import cn.people.cms.modules.cms.model.ArticleData;
import cn.people.cms.modules.cms.model.front.ArticleMediaVO;
import cn.people.cms.modules.cms.model.type.ArticleType;
import cn.people.cms.modules.sys.model.Category;
import cn.people.cms.modules.sys.service.ICategoryService;
import cn.people.cms.modules.templates.service.ITemplateService;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lml on 2018/4/11.
 */
@Service
@Transactional(readOnly = true,rollbackFor = Exception.class)
public class BlockRelationService  extends BaseService<BlockRelation> implements IBlockRelationService {

    @Value("${theone.freemarker.templates.folder}")
    private String storagePrefix;

    @Lazy
    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private ITemplateService templateService;

    /**
     * 区块关系 (频道、区块的编号决定了唯一的区块关系 前端传人)文章列表
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMenu(BlockRelationVO relationVO) {
        List<Integer>ids = relationVO.getIds();
        //处理区块导航关系
        if(ids!=null && ids.size()>0){
            ids.forEach(id->{
                if (id == null){
                    return;
                }
                BlockRelation relation = new BlockRelation(relationVO);
                Category category = dao.fetch(Category.class,id);
                if(category == null){
                    return;
                }
                MenuItem menuItem = new MenuItem();
                menuItem.setCategoryId(id);
                menuItem.setUrl(category.getUrl());
                menuItem.setTitle(category.getName());
                menuItem.setUrl(category.getUrl());
                menuItem.setOriName(category.getName());
                relation.setMenu(menuItem);
                dao.insertWith(relation,BlockRelation.MENU);
                relation.init();
                dao.updateIgnoreNull(relation);
            });
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveArticle(BlockRelationVO relationVO) {
        List<Article> items = relationVO.getItems();
        if(items!=null && items.size()>0){
            items.forEach(article -> {
                ArticleItem item = new ArticleItem(article);
                BlockRelation relation = new BlockRelation(relationVO);
                relation.setItem(item);
                dao.insertWith(relation,BlockRelation.ARTICLE);
                relation.init();
                dao.updateIgnoreNull(relation);
            });
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveInput(BlockRelationVO relationVO) {
        BlockRelation relation = new BlockRelation(relationVO);
        relation.setInput(relationVO.getInput());
        dao.insertWith(relation,BlockRelation.INPUT);
        relation.init();
        dao.updateIgnoreNull(relation);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveImage(BlockRelationVO relationVO) {
        BlockRelation relation = new BlockRelation(relationVO);
        relation.setImage(relationVO.getImage());
        dao.insertWith(relation,BlockRelation.IMAGE);
        relation.init();
        dao.updateIgnoreNull(relation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRelation(BlockRelationVO relationVO) {
        if(relationVO.getImage() !=null){
            dao.updateIgnoreNull(relationVO.getImage());
        }
        if(relationVO.getItem()!=null){
            dao.updateIgnoreNull(relationVO.getItem());
        }
        if(relationVO.getInput()!=null){
            dao.updateIgnoreNull(relationVO.getInput());
        }
        if(relationVO.getMenu()!=null){
            dao.updateIgnoreNull(relationVO.getMenu());
        }
    }

    @Override
    public QueryResult info(Integer blockId,Integer categoryId,Integer pageNo,Integer pageSize){
        QueryResult result = listPage(pageNo,pageSize,Cnd.where("block_id","=",blockId)
                .and("source_id","=",categoryId).asc(BlockRelation.WEIGHT));
        if(result ==null){
            return result;
        }
        List<BlockRelation>relations = (List<BlockRelation>)result.getList();
        if(!Lang.isEmpty(relations)){
            relations.forEach(relation -> setInfo(relation));
        }
        return result;
    }

    @Override
    public void setInfo(BlockRelation relation) {
        if (relation.getInputId() != null) {
            dao.fetchLinks(relation, BlockRelation.INPUT);
        }
        if (relation.getMenuId() != null) {
            setMenuInfo(relation);
        }
        if (relation.getItemId() != null) {
            setArticleInfo(relation);
        }
        if (relation.getImageId() != null) {
            dao.fetchLinks(relation, BlockRelation.IMAGE);
        }
    }

    private void  setMenuInfo(BlockRelation relation){
        dao.fetchLinks(relation, BlockRelation.MENU);
        MenuItem menuItem = relation.getMenu();
        if (menuItem == null) {
            return;
        }
        Category sourceCategory = dao.fetch(Category.class, menuItem.getCategoryId());
        if (sourceCategory == null) {
            return;
        }
        menuItem.setUrl(templateService.getUrl(sourceCategory.getUrl()));
        List children =  categoryService.queryByParentId(sourceCategory.getId(),null);
        Boolean isHasChildren = false;
        if(!Lang.isEmpty(children)){
            isHasChildren = true;
        }
        if (isHasChildren) {
            List<Category> list = categoryService.getTree(menuItem.getCategoryId()); //暂时写死，全权限，用户admin
            menuItem.setChildren(getMenuItems(list));
        }
        if (menuItem.getIsAutoImport() != null && menuItem.getIsAutoImport() == true) {
            Integer count = menuItem.getCount() == null ? Category.DEFAULT_PAGE_SIZE : menuItem.getCount();
            List<Article> items = dao.query(Article.class, Cnd.where("del_flag", "=", Article.STATUS_ONLINE).and(Article.Constant.CATEGORY_ID, "=", menuItem.getCategoryId()).limit(1, count)
                    .desc(Article.Constant.WEIGHT).desc(Article.Constant.PUBLISH_DATE));
            items.forEach(article -> {
                article.setUrl(templateService.getUrl(article.getUrl()));
            });
            menuItem.setItems(items);
        }

    }

    /**
     * 根据频道树获取menuItem树
     * @param list
     * @return
     */
    private List<MenuItem> getMenuItems(List<Category> list) {
        List<MenuItem> menuItems = new ArrayList<>();
        list.forEach(category -> {
            MenuItem menuItem = new MenuItem();
            menuItem.setCategoryId(category.getId());
            menuItem.setUrl(templateService.getUrl(category.getUrl()));
            menuItem.setTitle(category.getName());
            if (null != category.getChildren() && category.getChildren().size() > 0) {
                menuItem.setChildren(getMenuItems(category.getChildren()));
            }
            menuItems.add(menuItem);
        });
        return menuItems;
    }

    private void setArticleInfo(BlockRelation relation){
        dao.fetchLinks(relation, BlockRelation.ARTICLE);
        ArticleItem articleItem = relation.getItem();
        if (ArticleType.AUDIO.value().equals(articleItem.getType()) || ArticleType.VIDEO.value().equals(articleItem.getType())) {
            ArticleData data = dao.fetch(ArticleData.class, articleItem.getArticleId());
            if (StringUtils.isNotBlank(data.getImages())) {
                articleItem.setImageJson(JSONArray.parseArray(data.getImages(), ArticleMediaVO.class));
            }
            if (StringUtils.isNotBlank(data.getAudios())) {
                articleItem.setAudioJson(JSONArray.parseArray(data.getAudios(), ArticleMediaVO.class));
            }
            if (StringUtils.isNotBlank(data.getVideos())) {
                articleItem.setVideoJson(JSONArray.parseArray(data.getVideos(), ArticleMediaVO.class));
            }
        }
        Article article = dao.fetch(Article.class,articleItem.getArticleId());
        if(article == null){
            return;
        }
        articleItem.setUrl(templateService.getUrl(article.getUrl()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(Integer id){
        BlockRelation relation = fetch(id);
        if(BlockRelation.STATUS_OFFLINE == relation.getDelFlag()){
            relation.setDelFlag(BlockRelation.STATUS_ONLINE);
        }else if(BlockRelation.STATUS_ONLINE == relation.getDelFlag()){
            relation.setDelFlag(BlockRelation.STATUS_OFFLINE);
        }
        dao.updateIgnoreNull(relation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(Integer id,Integer delFlag){
        BlockRelation relation = fetch(id);
        relation.setDelFlag(delFlag);
        dao.updateIgnoreNull(relation);
    }
}
