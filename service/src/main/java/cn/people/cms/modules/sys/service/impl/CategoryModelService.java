package cn.people.cms.modules.sys.service.impl;

import cn.people.cms.base.dao.BaseDao;
import cn.people.cms.base.service.impl.BaseService;
import cn.people.cms.entity.BaseEntity;
import cn.people.cms.modules.fields.IFieldGroupService;
import cn.people.cms.modules.fields.model.FieldGroup;
import cn.people.cms.modules.sys.model.CategoryModel;
import cn.people.cms.modules.sys.service.ICategoryModelService;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by lml on 2017/4/28.
 */
@Service
@Transactional(readOnly = true)
public class CategoryModelService extends BaseService<CategoryModel> implements ICategoryModelService {

    @Autowired
    private BaseDao dao;

    @Autowired
    private IFieldGroupService fieldGroupService;

    @Override
    public List<CategoryModel> getAll(){
        return dao.query(CategoryModel.class, Cnd.where(BaseEntity.FIELD_STATUS,"<",BaseEntity.STATUS_DELETE));
    }

    /**
     * 获取栏目模型详情  模型下的文章扩展字段的信息
     * @param id
     * @return
     */
    @Override
    public CategoryModel fetch(Integer id) {
        CategoryModel categoryModel = super.fetch(id);
        if(categoryModel==null){
            return categoryModel;
        }
        //查询关联字段
        categoryModel = dao.fetchLinks(categoryModel, CategoryModel.FIELD_GROUPS);
        if(!Lang.isEmpty(categoryModel.getFieldGroups())){
            categoryModel.getFieldGroups().forEach(fieldGroup -> {
                dao.fetchLinks(fieldGroup, FieldGroup.FIELDS);
            });
        }
        return categoryModel;
    }

    @Override
    public QueryResult listPage(Integer pageNo, Integer pageSize) {
        QueryResult result = super.listPage(pageNo, pageSize, getDelFlag(null));
        return result;
    }

}
