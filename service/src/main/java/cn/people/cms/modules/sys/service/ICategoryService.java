package cn.people.cms.modules.sys.service;

import cn.people.cms.base.service.ITreeService;
import cn.people.cms.modules.cms.model.Site;
import cn.people.cms.modules.sys.model.Category;
import org.nutz.dao.QueryResult;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lml on 2016/12/22.
 */
public interface ICategoryService extends ITreeService<Category> {

    List<Category> getTree(Integer id);
    List<Category> getTree(Integer id, Integer siteId, Boolean role);
    List<Category> getTree(Integer id, Integer siteId,Integer delFlag, Boolean role);
    void batchSort(List<Category> list);
    QueryResult listPage(Integer pageNo, Integer pageSize);
    List<Category> getAllChildrenList(Set<Integer> ids, List<Category> list, Integer categoryId);
    Category getSimpleCategory(Integer id);
    String generateTemplates(Integer templateId,Category category,String type,Map map);
    String  generateTemplates(Integer templateId,Category category);
    void manuallyUpdate(Integer id,boolean isOwn,String type);
    String  dynamicUpdateById(Category category,String type);
    void changeStatus(Integer id);
    String updateCategoryTemplateAsync(Category category);
    String generateMapAndTemplates(Integer templateId,Integer categoryId,String  type);
    String generateMapAndTemplates(Integer templateId,Integer categoryId);
    List<Category> getAllChildrenList(List<Category> list, Integer categoryId);
    void flushCategories(Integer siteId,Integer newPid);
    void initRootCategory(Site site);
    Boolean isSiteRoot(Integer id);
}
