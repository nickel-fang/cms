package cn.people.cms.modules.guestbook.service;

import cn.people.cms.base.service.IBaseService;
import cn.people.cms.modules.guestbook.model.Guestbook;
import cn.people.cms.modules.guestbook.model.front.GuestbookVO;
import org.nutz.dao.QueryResult;

/**
 * Created by sunday on 2017/4/11.
 */
public interface IGuestbookService extends IBaseService<Guestbook> {

    QueryResult findSearchPage(Integer pageNumber, Integer pageSize, GuestbookVO guestbookVO);
}
