package cn.people.cms.modules.cms.service;


import cn.people.cms.base.service.IBaseService;
import cn.people.cms.modules.cms.model.ArticleMeta;

import java.util.List;

/**
 * Created by lml on 2016/12/23.
 */
public interface IArticleMetaService extends IBaseService<ArticleMeta> {

    void deleteMetas(String slug, Integer groupId);

    List<String> getFieldsCode();
}
