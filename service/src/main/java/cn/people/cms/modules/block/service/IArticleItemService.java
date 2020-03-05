package cn.people.cms.modules.block.service;

import cn.people.cms.base.service.IBaseService;
import cn.people.cms.modules.block.model.ArticleItem;

import java.util.List;

/**
 * Created by lml on 2018/4/11.
 */
public interface IArticleItemService  extends IBaseService<ArticleItem> {
    List<ArticleItem> queryByRelationId(Integer relationId);
}
