package cn.people.cms.modules.guestbook.service.impl;

import cn.people.cms.base.service.impl.BaseService;
import cn.people.cms.modules.guestbook.model.Guestbook;
import cn.people.cms.modules.guestbook.model.front.GuestbookVO;
import cn.people.cms.modules.guestbook.service.IGuestbookService;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.dao.sql.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 反馈service
 * Created by sunday on 2017/4/11.
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class GuestbookService extends BaseService<Guestbook> implements IGuestbookService {

    /**
     * 反馈列表页面搜索查询
     *
     * @param pageNumber
     * @param pageSize
     * @param guestbookVO
     * @return
     */
    @Override
    public QueryResult findSearchPage(Integer pageNumber, Integer pageSize, GuestbookVO guestbookVO) {
        Criteria cri = Cnd.cri();
        if (null != guestbookVO.getType()) {
            cri.where().and("type", " = ", guestbookVO.getType());
        }
        if (null != guestbookVO.getContent()) {
            cri.where().and("content", "like", "%" + guestbookVO.getContent() + "%");
        }
        if(null != guestbookVO.getBeginTime()){
            cri.where().and("create_at", ">",guestbookVO.getBeginTime().getTime());
        }
        if(null != guestbookVO.getEndTime()){
            cri.where().and("create_at","<", guestbookVO.getEndTime().getTime());
        }
        cri.getOrderBy().desc("id");
        QueryResult result = listPage(pageNumber, pageSize, cri);
        return result;
    }
}
