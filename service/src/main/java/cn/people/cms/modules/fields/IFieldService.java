package cn.people.cms.modules.fields;

import cn.people.cms.base.service.IBaseService;
import cn.people.cms.modules.fields.model.Field;
import org.nutz.dao.QueryResult;

import java.util.List;

/**
* 字段描述Service
* @author lml
*/
public interface IFieldService extends IBaseService<Field> {

    QueryResult listPage(Integer pageNo, Integer pageSize);

    Boolean slugExist(String slug);

    void batchInsert(List<Field> fields);
}