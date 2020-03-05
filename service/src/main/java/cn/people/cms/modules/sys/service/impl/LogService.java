package cn.people.cms.modules.sys.service.impl;

import cn.people.cms.base.dao.BaseDao;
import cn.people.cms.modules.sys.model.Log;
import cn.people.cms.modules.sys.model.front.LogVO;
import cn.people.cms.modules.sys.service.ILogService;
import cn.people.cms.modules.user.model.User;
import cn.people.cms.modules.user.service.IUserService;
import cn.people.cms.util.time.DateFormatUtil;
import cn.people.domain.IUser;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.dao.TableName;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* 日志Service
*/
@Service
public class LogService implements ILogService {

    @Autowired
    private BaseDao dao;
    @Autowired
    private IUserService userService;

    @Override
    public QueryResult page(LogVO logVO) {
        Criteria cri = Cnd.cri();
        if (StringUtils.isNotBlank(logVO.getURI())) {
            cri.where().andLike("request_uri", "%" + logVO.getURI().trim() + "%");
        }
        if (null != logVO.getUserId()) {
            cri.where().andEquals("create_by", logVO.getUserId());
        }
        if (null != logVO.getBeginTime()) {
            cri.where().and("create_date", ">", logVO.getBeginTime());
        }
        if (null != logVO.getEndTime()) {
            cri.where().and("create_date", "<", logVO.getEndTime());
        }
        if (null != logVO.getIsException()) {
            if (logVO.getIsException().equals(2)) {
                cri.where().andEquals("type", 2);
            } else if (logVO.getIsException().equals(1)) {
                cri.where().andEquals("type", 1);
            }
        }
        if(null != logVO.getModify() && logVO.getModify()){
            String[] methods = {"POST", "DELETE", "PATCH"};
            cri.where().andIn("method", methods);
        }
        cri.getOrderBy().desc("create_date");
        if (null == logVO.getPageNumber()) {
            logVO.setPageNumber(1);
        }
        if (null == logVO.getPageSize()) {
            logVO.setPageSize(20);
        }
        try {
            TableName.set(DateFormatUtil.formatDate("yyyy", new Date()));
            Pager pager = dao.createPager(logVO.getPageNumber(), logVO.getPageSize());
            List<Log> logs = dao.query(Log.class, cri, pager);
            logs.stream().forEach(log -> {
                if (log != null && log.getCreateBy() != null) {
                    User user = userService.fetch(log.getCreateBy());
                    if (user != null) {
                        log.setUserName(user.getName());
                    }
                }
            });
            pager.setRecordCount(dao.count(Log.class, cri));
            return new QueryResult(logs, pager);
        }finally {
            TableName.clear();
        }
    }
    @Override
    public void insert(Log log) {
//        TableName.run(DateFormatUtil.formatDate("yyyy", new Date()), () -> dao.insert(log));
    }
}