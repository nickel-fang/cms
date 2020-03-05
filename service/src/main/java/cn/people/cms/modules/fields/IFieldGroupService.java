package cn.people.cms.modules.fields;

import cn.people.cms.base.service.IBaseService;
import cn.people.cms.modules.fields.model.FieldGroup;
import org.nutz.dao.QueryResult;

/**
* 字段组Service
* @author lml
*/
public interface IFieldGroupService extends IBaseService<FieldGroup> {

    QueryResult listPage(Integer pageNo, Integer pageSize);
}