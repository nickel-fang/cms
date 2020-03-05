package cn.people.cms.modules.analysis.service;

import cn.people.cms.base.service.IBaseService;
import cn.people.cms.modules.analysis.model.Editors;
import org.nutz.dao.QueryResult;

/**
 * 人员统计service
 *
 * Created by cuiyukun on 2017/7/3.
 */
public interface IEditorService extends IBaseService<Editors> {

    QueryResult listPage(Integer pageNumber, Integer pageSize, String startTime, String endTime, String type);

}
