package cn.people.cms.modules.fields.impl;

import cn.people.cms.base.dao.BaseDao;
import cn.people.cms.base.service.impl.BaseService;
import cn.people.cms.modules.cms.service.IArticleMetaService;
import cn.people.cms.modules.fields.IFieldGroupService;
import cn.people.cms.modules.fields.IFieldService;
import cn.people.cms.modules.fields.model.Field;
import cn.people.cms.modules.fields.model.FieldGroup;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.QueryResult;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 字段组Service
 *
 * @author lml
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class FieldGroupService extends BaseService<FieldGroup> implements IFieldGroupService {

    @Autowired
    private IFieldService fieldService;
    @Autowired
    private IArticleMetaService articleMetaService;

    @Autowired
    private BaseDao dao;

    @Transactional
    private void deleteMetas(FieldGroup fieldGroup) {
        List slugList = new ArrayList();
        fieldGroup.getFields().forEach(field -> slugList.add(field.getSlug()));
        List<Field> fields = fetch(fieldGroup.getId()).getFields();
        if (!Lang.isEmpty(fields)) {
            fields.forEach(field -> {
                //如果新保存的组中包含了已有的字段slug则不清除，否则清除meta表中的扩展字段信息
                if (!slugList.contains(field.getSlug())) {
                    articleMetaService.deleteMetas(field.getSlug(), fieldGroup.getId());
                }
            });
        }
    }

    @Transactional
    private void saveRelation(Boolean isInsert, FieldGroup fieldGroup, String... fieldNames) {
        if (null !=fieldNames && fieldNames.length > 0) {
            //先清除关联字段再更新
            Arrays.asList(fieldNames).forEach(fieldName -> {
                if (!isInsert) {
                    dao.clearLinks(fetch(fieldGroup.getId()), fieldName);
                }
                dao.insertRelation(fieldGroup, fieldName);
            });

        }
    }

    @Override
    @Transactional
    public int delete(Integer id) {
        //清除关联关系
        FieldGroup fieldGroup = fetch(id);
        if (null != fieldGroup) {
            //扩展字段清理
            articleMetaService.deleteMetas(null, id);
            //关联关系处理
            dao.clearLinks(fieldGroup, FieldGroup.FIELDS);
            dao.clearLinks(fieldGroup, FieldGroup.CATEGORY_MODELS);
            return vDelete(id);
        }
        return 0;
    }

    @Override
    @Transactional
    public Object save(FieldGroup fieldGroup) {
        fieldGroup.setDelFlag(FieldGroup.STATUS_ONLINE);
        //字段部分
        if (!Lang.isEmpty(fieldGroup.getFields())) {
            fieldService.batchInsert(fieldGroup.getFields());
        }
        if (null !=fieldGroup.getId()) {
            super.save(fieldGroup);
            //字段排序处理
            deleteMetas(fieldGroup);
            //更新组关联栏目本身
            saveRelation(false, fieldGroup, FieldGroup.CATEGORY_MODELS, FieldGroup.FIELDS);
        } else {
            super.save(fieldGroup);
            //字段排序处理
            saveRelation(true, fieldGroup, FieldGroup.CATEGORY_MODELS, FieldGroup.FIELDS);
        }
        sort2Json(fieldGroup);
        return fieldGroup;
    }

    @Transactional
    private void sort2Json(FieldGroup fieldGroup){
        List<Field> list = fieldGroup.getFields();
        list.forEach(field -> {
            try {
                HashMap hashMap;
                if(field.getSort() ==null){
                    field.setSort(field.getId());
                }
                if(null ==field.getSortJson()){
                    hashMap = new HashMap();
                }else {
                    hashMap = JSONObject.parseObject(field.getSortJson(),HashMap.class);
                }
                hashMap.put(fieldGroup.getId(),field.getSort());
                field.setSortJson(JSONObject.toJSONString(hashMap));
                dao.updateIgnoreNull(field);
            }catch (Exception ex){
                log.error(ex.getMessage());
            }
        });
    }


    /**
     * 获取字段组详情(字段信息 关联栏目 关联栏目模型)
     *
     * @param id
     * @return
     */
    @Override
    public FieldGroup fetch(Integer id) {
        //查找关联的栏目模型 栏目本身 
        FieldGroup fieldGroup = dao.fetch(FieldGroup.class, id);
        fieldGroup = dao.fetchLinks(dao.fetchLinks(fieldGroup, FieldGroup.CATEGORY_MODELS), FieldGroup.FIELDS,getDelFlag(null));
        if(!Lang.isEmpty(fieldGroup.getFields())){
            List<Field> list = fieldGroup.getFields();
            list.forEach(field->{
                try {
                    if(null !=field.getSortJson()){
                        HashMap map = JSONObject.parseObject(field.getSortJson(),HashMap.class);
                        if(map.get(id)!=null){
                            Integer sort = Integer.valueOf(map.get(id).toString());
                            field.setSort(sort);
                        }
                        Integer sort = (Integer) map.get(id);
                        if(sort!=null){
                            field.setSort(sort );
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            Collections.sort(list);
        }
        return fieldGroup;
    }

    @Override
    public QueryResult listPage(Integer pageNo, Integer pageSize) {
        QueryResult result = super.listPage(pageNo, pageSize, getDelFlag(null));
        if(!Lang.isEmpty(result.getList())){
            List<FieldGroup>list = (List<FieldGroup>)result.getList();
            list.forEach(fieldGroup->{
                dao.fetchLinks(fieldGroup, FieldGroup.CATEGORY_MODELS);
            });
        }
        return result;
    }
}