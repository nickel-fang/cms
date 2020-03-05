package cn.people.cms.modules.sys.service;

import cn.people.cms.modules.sys.model.Log;
import cn.people.cms.modules.sys.model.front.LogVO;
import org.nutz.dao.QueryResult;

/**
* 日志Service
*/
public interface ILogService {

	QueryResult page(LogVO logVO);
	void insert(Log log);
}