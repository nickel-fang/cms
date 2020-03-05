package cn.people.cms.modules.cms.service.impl;

import cn.people.cms.base.dao.BaseDao;
import cn.people.cms.base.service.impl.BaseService;
import cn.people.cms.entity.BaseEntity;
import cn.people.cms.modules.cms.model.ArticleMeta;
import cn.people.cms.modules.cms.service.IArticleMetaService;
import cn.people.cms.modules.fields.IFieldService;
import cn.people.cms.modules.fields.model.Field;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lml on 2016/12/23.
 */
@Service
@Transactional(readOnly = true)
public class ArticleMetaService extends BaseService<ArticleMeta> implements IArticleMetaService {
    @Autowired
    private BaseDao dao;
    @Autowired
    private IFieldService fieldService;

    @Override
    @Transactional
    public void deleteMetas(String slug, Integer groupId) {
        Cnd cnd;
        if (groupId != null && slug != null) {
            cnd = Cnd.where(ArticleMeta.FIELD_CODE, "=", slug).and(ArticleMeta.FIELD_GROUP_ID, "=", groupId);
        } else if (groupId == null && slug != null) {
            cnd = Cnd.where(ArticleMeta.FIELD_CODE, "=", slug);
        } else if (groupId != null && slug == null) {
            cnd = Cnd.where(ArticleMeta.FIELD_GROUP_ID, "=", groupId);
        } else {
            cnd = null;
        }
        List<ArticleMeta> list = dao.query(ArticleMeta.class, cnd);
        list.forEach(articleMeta -> delete(articleMeta.getId()));
    }

    @Override
    public List<String> getFieldsCode() {
        List<String> fieldNames = new ArrayList<>();
        Sql sql = Sqls.create("select distinct field_code from cms_article_meta where del_flag = @delFlag");
        sql.params().set(BaseEntity.FIELD_STATUS, ArticleMeta.STATUS_ONLINE);
        List list = list(sql);
        if (!Lang.isEmpty(list)) {
            list.forEach(fieldCode -> {
                Field field = fieldService.fetch(fieldCode.toString());
                if (field != null && field.getIsAllowSearch() && StringUtils.isNotBlank(field.getName())) {
                    fieldNames.add(field.getName());
                }
            });
        }
        return fieldNames;
    }
}
