package cn.people.cms.modules.sys.service;

import cn.people.cms.base.service.IBaseService;
import cn.people.cms.modules.sys.model.Dict;
import org.nutz.dao.QueryResult;

import java.util.List;

/**
* 字典Service
* @author cuiyukun
*/
public interface IDictService extends IBaseService<Dict> {

    List<Dict> getListByType(String type);
    QueryResult listPage(Integer pageNo, Integer pageSize, Dict dict);

    List getTypes();

    public List<Dict> query(String value, String type);
}