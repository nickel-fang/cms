package cn.people.cms.modules.sys.service.impl;

import cn.people.cms.base.dao.BaseDao;
import cn.people.cms.base.service.impl.TreeService;
import cn.people.cms.entity.BaseEntity;
import cn.people.cms.modules.block.model.BlockRelation;
import cn.people.cms.modules.block.service.IBlockRelationService;
import cn.people.cms.modules.cms.model.Site;
import cn.people.cms.modules.cms.service.IArticleService;
import cn.people.cms.modules.sys.model.Category;
import cn.people.cms.modules.sys.service.ICategoryService;
import cn.people.cms.modules.templates.model.Template;
import cn.people.cms.modules.templates.service.ITemplateService;
import cn.people.cms.modules.user.model.User;
import cn.people.cms.modules.user.service.IUserService;
import cn.people.cms.util.base.ShiroUtils;
import cn.people.cms.util.mapper.BeanMapper;
import cn.people.cms.util.mapper.JsonMapper;
import cn.people.cms.util.text.StringUtils;
import cn.people.domain.IUser;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by lml on 2016/12/22.
 */
@Slf4j
@Service
@CacheConfig(cacheNames = "cms")
@Transactional(readOnly = true,rollbackFor = Exception.class)
public class CategoryService extends TreeService<Category> implements ICategoryService {

    @Autowired
    private IUserService userService;
    @Autowired
    private ITemplateService templateService;
    @Autowired
    private IBlockRelationService blockRelationService;
    @Value("${theone.freemarker.switch}")
    private Boolean templateSwitch;
    @Autowired
    private IArticleService articleService;
    @Autowired
    private BaseDao dao;
    private JsonMapper jsonMapper = new JsonMapper();
    private final Integer rootId= 1;


    /**
     * 更新频道的同时，同步生成静态页面
     * @param category
     * @return
     */
    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public Object save(Category category) {
        boolean isInit = category.getId()==null;
        super.save(category);
        if (!isInit){
            updateDescendTemplates(category);
        }
        if(category.getSort() ==null){
            category.setSort(category.getId());
        }
        //更新详情模板
        if(category.getTemplateId() == null){
            Integer parentId = category.getParentId();
            if(parentId!=null){
                Category pCategory = dao.fetch(Category.class,parentId);
                if(pCategory !=null && pCategory.getTemplateId()!=null){
                    category.setTemplateId(pCategory.getTemplateId());
                }
            }
        }
        dao.updateIgnoreNull(category);
        return category;
    }

    /**
     * 更新文章列表和文章详情的静态页面
     * @param category
     */
    //@Async
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateCategoryTemplateAsync(Category category){
        if (!templateSwitch || null == category) {
            return null;
        }
        if(category.getPageTemplateId()!=null){
            return dynamicUpdateById(category,null);
        }
        return null;
    }

    @Override
    public Category fetch(Integer id) {
        Category category  = super.fetch(id);
        //频道模板路径 http 不需要重新获取
        log.info("category.Url:{}",category.getUrl());
        if(StringUtils.isNotBlank(category.getUrl()) && category.getUrl().length() >= 4 ) {
            if (!"http".startsWith(category.getUrl().substring(0,4))) {
                category.setUrl(templateService.getUrl(category.getUrl()));
            }
        }
        if(category == null){
            return null;
        }
        return dao.fetchLinks(dao.fetchLinks(category, Category.TEMPLATE),Category.PAGE_TEMPLATE);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    public int delete(Integer id) {
        Category category = dao.fetch(Category.class,id);
        if(category == null){
            return 0;
        }
        category.setUrl(null);
        category.setDelFlag(Category.STATUS_DELETE);
        templateService.offLine(Template.CATEGORY_TEMPLATE_TYPE,id);
        return dao.updateIgnoreNull(category);
    }

    /**
     * 不根据用户，直接拿该id下所有category树
     * @param id
     * @return
     */
    @Override
    public List<Category> getTree(Integer id) {
        User user = userService.fetch(1);
        if (user == null) {
            return null;
        }
        Set<Integer> categoryIds = userService.getCategoryIds(user, null);
        List<Category> sourceList = queryParentId(id, null);
        if ((Lang.isEmpty(categoryIds)) || Lang.isEmpty(sourceList)) {
            return null;
        }
        Iterator<Category> iterator = sourceList.iterator();
        while (iterator.hasNext()) {
            Category categoryVO = iterator.next();
            boolean isRoot = rootId.equals(categoryVO.getId());
            if (categoryVO == null ||  !categoryIds.contains(categoryVO.getId())) {
                if(!isRoot){
                    iterator.remove();
                    continue;
                }
            }
            List<Category> child = queryParentId(categoryVO.getId(), 0);
            child = setChild(child, categoryIds, 0);
            categoryVO.setChildren(child);
        }
        return sourceList;
    }

    @Override
    public List<Category> getTree(Integer id, Integer siteId, Boolean role) {
        return getTree(id,siteId, BaseEntity.STATUS_ONLINE, role);
    }

    private List queryParentId(Integer parentId,Integer delFlag) {
        if(parentId == 0){
            return dao.query(tClass, getDelFlag(delFlag).and("parent_id", "=", parentId));
        }
        return dao.query(tClass, getDelFlag(delFlag).and("parent_id", "=", parentId).desc("sort"));
    }

    private List<Category> setChild(List<Category> list, Set categoryIds,Integer delFlag) {
        if ((Lang.isEmpty(categoryIds)) || Lang.isEmpty(list)) {
            return null;
        }
        Iterator<Category> iterator = list.iterator();
        while (iterator.hasNext()) {
            Category categoryVO = iterator.next();
            boolean isRoot = rootId.equals(categoryVO.getId());
            if (categoryVO == null ||  !categoryIds.contains(categoryVO.getId())) {
                if(!isRoot){
                    iterator.remove();
                    continue;
                }
            }
            recursive(categoryVO, categoryIds,delFlag);
        }
        return list;
    }

    private Category recursive(Category categoryVO, Set categoryIds,Integer delFlag) {
        List<Category> child = queryParentId(categoryVO.getId(),delFlag);
        child = setChild(child, categoryIds,delFlag);
        categoryVO.setChildren(child);
        return categoryVO;
    }

//    @Override
//    public List<Category> getTree(Integer id, Integer userId,Integer siteId) {
//        return getTree(id, userId, siteId,null);
//    }

    @Override
    public List<Category> getTree(Integer id, Integer siteId,Integer delFlag, Boolean role) {
        IUser user = ShiroUtils.getUser();
        if (user == null) {
            return null;
        }
        Set<Integer> categoryIds = new HashSet<>();
        if (user.isAdmin() || (null != role && user.getId() == 3 && role)) { //超级管理员 或 安全保密管理员在role为true时取全部频道
            List<Category> categories = dao.query(Category.class,Cnd.where("del_flag","=", Site.STATUS_ONLINE));
            if(!Lang.isEmpty(categories)){
                for (Category category : categories) {
                    categoryIds.add(category.getId());
                }
            }
        } else {
            User entity = BeanMapper.map(user, User.class);
            categoryIds = userService.getCategoryIds(entity, 2);  //1代表前台，2代表后台  目前先全部返回后台的
        }
        List<Category> sourceList = queryByParentId(id, siteId,delFlag);
        setChild(sourceList, categoryIds,siteId,delFlag);
        return sourceList;
    }

    private Category recursive(Category categoryVO, Set categoryIds,Integer siteId,Integer delFlag) {
        List<Category> child = queryByParentId(categoryVO.getId(), siteId,delFlag);
        child = setChild(child, categoryIds,siteId,delFlag);
        categoryVO.setChildren(child);
        return categoryVO;
    }

    private List<Category> setChild(List<Category> list, Set categoryIds,Integer siteId,Integer delFlag) {
        if ((Lang.isEmpty(categoryIds)) || Lang.isEmpty(list)) {
            return null;
        }
        Iterator<Category> iterator = list.iterator();
        while (iterator.hasNext()) {
            Category categoryVO = iterator.next();
            boolean isRoot = rootId.equals(categoryVO.getId());
            if (categoryVO == null ||  !categoryIds.contains(categoryVO.getId())) {
                if(!isRoot){
                    iterator.remove();
                    continue;
                }
            }
            recursive(categoryVO, categoryIds,siteId,delFlag);
        }
        return list;
    }

    @Override
    public QueryResult listPage(Integer pageNo, Integer pageSize) {
        return super.listPage(pageNo, pageSize, getDelFlag(null));
    }

    /**
     * 不含关联字段
     *
     * @param list
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    public void batchSort(List<Category> list) {
        super.batchUpdate(list);
    }

    @Override
    public List<Category> getAllChildrenList(List<Category> list, Integer categoryId){
        List<Category> child = queryByParentId(categoryId, null);
        if (Lang.isEmpty(child)) {
            return list;
        }
        Iterator<Category> iterator = child.iterator();
        while (iterator.hasNext()) {
            Category category = iterator.next();
            if (category == null) {
                iterator.remove();
                continue;
            }
            list.add(category);
            list = getAllChildrenList(list, category.getId());
        }
        return list;
    }

    @Override
    public List<Category> getAllChildrenList(Set<Integer> ids, List<Category> list, Integer categoryId) {
        List<Category> child = queryByParentId(categoryId, null);
        if (Lang.isEmpty(child)) {
            return list;
        }
        Iterator<Category> iterator = child.iterator();
        while (iterator.hasNext()) {
            Category category = iterator.next();
            if (category == null) {
                iterator.remove();
                continue;
            }
            if (!ids.contains(category.getId())) {
                iterator.remove();
                continue;
            }
            list.add(category);
            list = getAllChildrenList(ids, list, category.getId());
        }
        return list;
    }

    @Override
    public Category getSimpleCategory(Integer id) {
        if (id == null) {
            return null;
        }
        return dao.fetch(Category.class, id);
    }

    /**
     * 手动更新某个编号下所有的文章
     * @param id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void manuallyUpdate(Integer id,boolean isOwn,String type){
        Category category = dao.fetch(Category.class,id);
        if(isOwn){
            dynamicUpdateById(category,type);
           dao.updateIgnoreNull(category);
        }else {
            List<Category>categories;
            if(id == 0){
                categories = dao.query(tClass, getDelFlag(0));
            }else {
                categories = findByParentIdsLike(","+id+",");
                categories.add(category);
            }
            if(categories == null || categories.size() == 0){
                return;
            }
            categories.forEach(c -> {
                dynamicUpdateById(c, type);
                articleService.manuallyUpdate(c.getId());
                log.info("------category  update finish-------");
            });
            batchUpdate(categories);
        }
    }

    /**
     * 当type为空的时候，更新在线的模板静态页，此时需要更新频道页面的url
     * @param category
     * @param type
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String dynamicUpdateById(Category category,String type){
        if(category == null || Category.STATUS_ONLINE != category.getDelFlag()){
            return null;
        }
        Map map = new HashMap();
        QueryResult result = articleService.simpleListPage(category.getId());
        if(result!=null){
            map.put("result",result);
        }
        String url = generateTemplates(category.getPageTemplateId(),category,type,map);
        if(type == null){
            category.setUrl(url);
        }
        dao.updateIgnoreNull(category);
        return url;
    }

//    @Async
    @Transactional(rollbackFor = Exception.class)
    public void updateDescendTemplates(Category category){
        if(category.getPageTemplateId()==null && category.getTemplateId() == null){
            return;
        }
        List<Category> descendantsIds =dao.query(tClass, getDelFlag(0).and("parent_ids", "like", "%,"+category.getId()+",%"));
        if(Lang.isEmpty(descendantsIds)){
            return;
        }
        descendantsIds.forEach(c->{
            //若子栏目没有定制化模板，则更新所有
            if(category.getTemplateId()!=null && c.getTemplateId() == null)
                c.setTemplateId(category.getTemplateId());
            dao.updateIgnoreNull(c);
        });
    }

    private List queryByParentId(Integer parentId,Integer siteId,Integer delFlag) {
        return dao.query(tClass, getDelFlag(delFlag).and("site_id","=",siteId).and("parent_id", "=", parentId).desc("sort"));
    }
    @Override
    public String generateMapAndTemplates(Integer templateId,Integer categoryId){
        return generateMapAndTemplates(templateId,categoryId,null);
    }

    @Override
    public String generateMapAndTemplates(Integer templateId,Integer categoryId,String  type){
        Map map = new HashMap();
        QueryResult result = articleService.simpleListPage(categoryId);
        if(result!=null){
            map.put("result",result);
        }
        Category category = dao.fetch(Category.class,categoryId);
        return generateTemplates(templateId,category,type,map);
    }


    @Override
    public String  generateTemplates(Integer templateId,Category category){
        return generateTemplates(templateId,category,null,null);
    }

    /**
     * 选择频道之后。生成频道对应的静态页面
     * 首先获取频道关联的模板，查询到模板中所有的区块，获取每一个区块所绑定的区块模板，每一个区块对应生成区块模板的静态
     * 文件，最后装入includes列表中
     * map里面包含三种类型的数据，文章详情（key=article）、文章列表（key=result【QueryResult】）、最终的编码如果是文章详情
     * 还需要再map中塞入文章的id。其他情况都默认获取频道的id即可
     */
    @Override
    public String  generateTemplates(Integer templateId,Category category,String type,Map map){
        if(templateId == null || category == null || category.getId() == null){
            return null;
        }
        Template template = dao.fetch(Template.class,templateId);
        if(template == null){
            return null;
        }
        //获取页面数据【文章列表、文章详情】
        Map pageMap = getPageMap(map,category.getId());
        //获取模板区块数据，freemarker include 区块页面
        List<Map> includes = getBlockIncludes(template,category.getId());
        if(!Lang.isEmpty(includes)){
            pageMap.put("includes",includes);
        }
        String id = String.valueOf(pageMap.get("id"));
        log.info("频道或者详情模板生成"+id+":category/article  relations:"+jsonMapper.toJson(pageMap));
        return  templateService.freeMarkerContent(pageMap,template,id,type);
    }

    private List<Map> getBlockIncludes(Template template,Integer categoryId) {
        //查询频道模板下所有的区块
        dao.fetchLinks(template,Template.BLOCKS);
        if(Lang.isEmpty(template.getBlocks())){
            return null;
        }
        List<Map>includes = new ArrayList<>();
        template.getBlocks().forEach(block -> {
            Integer blockTemplateId = block.getBlockTemplateId();
            if(blockTemplateId == null){
                return;
            }
            Template blockTemplate = dao.fetch(Template.class,blockTemplateId);
            if(blockTemplate ==null){
                return;
            }
            List<BlockRelation> relations =  dao.query(BlockRelation.class,Cnd.where("source_id","=",categoryId)
                    .and("block_id","=",block.getId()).and("del_flag","=",BlockRelation.STATUS_ONLINE).asc(BlockRelation.WEIGHT));
            if(!Lang.isEmpty(relations)){
                relations.forEach(relation -> blockRelationService.setInfo(relation));
            }
            //区块模板生成
            log.info("区块模板生成,"+block.getId()+":info  relations:"+jsonMapper.toJson(relations));
            templateService.freeMarkerContent(relations,blockTemplate,block.getId());
            String value = templateService.getHtmlPrefix(null,blockTemplate.getType())+block.getId()+".html";
            if(block.getTag()== null || value == null){
                return;
            }
            //每一个tag作为一个对象
            Map tagMap = new HashMap();
            tagMap.put("name",block.getTag());
            tagMap.put("url",value);
            includes.add(tagMap);
        });
        return  includes;
    }

    private Map getPageMap(Map map,Integer categoryId){
        //页面拼接
        String code = "id";
        String  articleStr = "article";
        String  categoryStr = "result";
        Map pageMap = new HashMap<String,Object>();
        //文章、列表模板生成
        if(map != null && map.size() >0){
            //文章详情，则pageMap中的id为文章的编码，否则为频道的编码
            if(map.get(code) == null){
                pageMap.put(code,categoryId);
            }else {
                pageMap.put(code,map.get(code));
            }
            //文章详情
            Object articleObj = map.get(articleStr);
            if(articleObj!=null){
                pageMap.put(articleStr,articleObj);
            }
            //频道列表模板生成
            Object categoryObj = map.get(categoryStr);
            if(categoryObj!=null){
                pageMap.put(categoryStr,categoryObj);
            }
        }
        return pageMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(Integer id){
        Category category = dao.fetch(Category.class,id);
        if(category == null){
            return;
        }
        if(BlockRelation.STATUS_OFFLINE == category.getDelFlag()){
            category.setDelFlag(Category.STATUS_ONLINE);
            updateCategoryTemplateAsync(category);
        }else if(BlockRelation.STATUS_ONLINE == category.getDelFlag()){
            category.setDelFlag(BlockRelation.STATUS_OFFLINE);
            category.setUrl(null);
            templateService.offLine(Template.CATEGORY_TEMPLATE_TYPE,id);
        }
        dao.updateIgnoreNull(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void flushCategories(Integer siteId,Integer newPid){
        List<Category> categories = dao.query(Category.class,Cnd.where("parent_id","=",1).and("site_id","=",siteId).and("del_flag","=",Category.STATUS_ONLINE));
        if(!Lang.isEmpty(categories)){
            categories.forEach(category -> {
                if(newPid.equals(category.getId())){
                    return;
                }
                category.setParentId(newPid);
                super.save(category);
            });
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initRootCategory(Site site){
        int parentId= 0;
        Category category = new Category();
        category.setName(site.getName());
        category.setSiteId(site.getId());
        category.setParentId(parentId);
        category.setParentIds(parentId+",");
        category.setDelFlag(Category.STATUS_ONLINE);
        dao.insert(category);
    }

    @Override
    public Boolean isSiteRoot(Integer id){
        Category  category = dao.fetch(Category.class,id);
        if(category == null){
            return false;
        }
        //TODO:后期优化掉根据名称判断的部分
        return category.getParentId()>0 &&  !"首页".equals(category.getName()) ? false:true;
    }
}