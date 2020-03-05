package cn.people.cms.modules.block.service.impl;

import cn.people.cms.base.dao.BaseDao;
import cn.people.cms.base.service.impl.BaseService;
import cn.people.cms.modules.block.model.ArticleItem;
import cn.people.cms.modules.block.service.IArticleItemService;
import org.nutz.dao.Cnd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lml on 2018/4/11.
 */
@Service
public class ArticleItemService  extends BaseService<ArticleItem> implements IArticleItemService {

    @Autowired
    private BaseDao dao;

    @Override
    public List<ArticleItem> queryByRelationId(Integer relationId) {
        return dao.query(ArticleItem.class, Cnd.where(ArticleItem.BLOCK_RELATION_ID,"=",relationId));
    }
}
