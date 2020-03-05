package cn.people.cms.modules.cms.service.impl;

import cn.people.cms.base.service.impl.BaseService;
import cn.people.cms.modules.cms.model.Vote;
import cn.people.cms.modules.cms.service.IVoteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by lml on 2017/3/21.
 */
@Service
@Transactional(readOnly = true)
public class VoteService  extends BaseService<Vote> implements IVoteService {

    @Transactional
    public int delete(Integer id) {
        return this.vDelete(id);
    }
}
