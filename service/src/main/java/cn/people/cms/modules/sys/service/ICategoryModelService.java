package cn.people.cms.modules.sys.service;

import cn.people.cms.base.service.IBaseService;
import cn.people.cms.modules.sys.model.CategoryModel;
import org.nutz.dao.QueryResult;

import java.util.List;

/**
 * Created by lml on 2017/4/28.
 */
public interface ICategoryModelService  extends IBaseService<CategoryModel> {

    List<CategoryModel> getAll();

    QueryResult listPage(Integer pageNo, Integer pageSize);
}
