package cn.people.cms.modules.fields.impl;

import cn.people.cms.base.dao.BaseDao;
import cn.people.cms.base.service.impl.BaseService;
import cn.people.cms.modules.cms.service.IArticleMetaService;
import cn.people.cms.modules.fields.IFieldService;
import cn.people.cms.modules.fields.model.Field;
import com.alibaba.fastjson.JSONObject;
import org.nutz.dao.QueryResult;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

/**
 * 字段描述Service
 *
 * @author lml
 */
@Service
@Transactional(readOnly = true)
public class FieldService extends BaseService<Field> implements IFieldService {

    @Autowired
    private BaseDao dao;
    @Autowired
    private IArticleMetaService articleMetaService;

    @Override
    public QueryResult listPage(Integer pageNo, Integer pageSize) {
        QueryResult result = listPage(pageNo, pageSize, getDelFlag(null));
        List<Field> fields = (List<Field>) result.getList();
        if (null != result  && !Lang.isEmpty(fields)) {
            fields.forEach(field -> dao.fetchLinks(field, Field.FIELD_GROUPS));
        }
        return result;
    }

    @Override
    @Transactional
    public Object save(Field field){
        super.save(field);
        if(null == field.getSort()){
            field.setSort(field.getId());
        }
        dao.updateIgnoreNull(field);
        return field;
    }

    @Override
    @Transactional
    public int delete(Integer id) {
        Field field = fetch(id);
        if (null == field) {
            return 0;
        }
        articleMetaService.deleteMetas(field.getSlug(), null);
        dao.clearLinks(fetch(id),Field.FIELD_GROUPS);
        return dao.delete(Field.class,id);
    }

    @Override
    public Boolean slugExist(String slug) {
        List<Field> list = dao.query(Field.class, getDelFlag(null).and("slug", "=", slug));
        if (!Lang.isEmpty(list)) {
            return true;
        }
        return false;
    }

    @Override
    public Field fetch(Integer id) {
        Field field = super.fetch(id);
        return dao.fetchLinks(field, Field.FIELD_GROUPS);
    }

    @Override
    public void batchInsert(List<Field> fields) {
        fields.forEach(field -> {
            if(field == null)
                return;
            if (null == field.getDelFlag()) {
                field.setDelFlag(Field.STATUS_ONLINE);
            }
            super.save(field);
        });
    }

    @Transactional
    public void batchSort(List<Field> list) {
        list.forEach(field -> {
            if(null != field.getGroupId() && null !=field.getSort()){
                HashMap hashMap;
                try {
                    if(null != field.getSortJson()){
                        hashMap = JSONObject.parseObject(field.getSortJson(),HashMap.class);
                    }else {
                        hashMap = new HashMap();
                    }
                    hashMap.put(field.getGroupId(),field.getSort());
                    field.setSortJson(JSONObject.toJSONString(hashMap));
                    dao.updateIgnoreNull(field);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}