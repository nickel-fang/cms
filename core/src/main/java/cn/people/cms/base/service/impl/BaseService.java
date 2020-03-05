package cn.people.cms.base.service.impl;

import cn.people.cms.base.dao.BaseDao;
import cn.people.cms.base.service.IBaseService;
import cn.people.cms.entity.BaseEntity;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.*;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by lml on 2016/12/10.
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public abstract class BaseService<T extends BaseEntity> implements IBaseService<T> {

    protected Class<T> tClass;

    @Autowired
    protected BaseDao dao;

    public BaseService() {
        tClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    public int count(String tableName, Condition cnd) {
        return dao.count(tableName, cnd);
    }

    @Override
    public T fetch(Integer id) {
        return dao.fetch(tClass, Cnd.where("id","=",id));
    }

    @Override
    public T fetch(String name) {
        return dao.fetch(tClass, name);
    }

    @Override
    @Transactional
    public int delete(String name) {
        return dao.delete(this.tClass, name);
    }

    @Override
    @Transactional
    public Object save(T t) {
        if(t.isNew()){
            t.init();
            return dao.insert(t);
        }else{
            return dao.update(t);
        }
    }

    @Override
    @Transactional
    public T insert(T t) {
        return dao.insert(t);
    }

    @Override
    @Transactional
    public int update(T t, String fieldName) {
        return Daos.ext(dao, FieldFilter.create(tClass, null, fieldName, true)).update(t);
    }

    /**
     * 忽略值为null的字段
     */
    @Override
    @Transactional
    public int updateIgnoreNull(T t) {
        return dao.updateIgnoreNull(t);
    }

    @Override
    @Transactional
    public int delete(Integer id) {
        return dao.delete(this.tClass, id);
    }

    @Override
    public int getMaxId() {
        return dao.getMaxId(this.tClass);
    }

    @Override
    public int getMinId() {
        String tableName = dao.getEntity(tClass).getTableName();
        Sql sql = Sqls.create("SELECT MIN(id) FROM "+tableName);
        sql.setCallback((conn, rs, sl) -> {
            int minId = 0;
            if(null != rs && rs.next()){
                minId = rs.getInt(1);
            }
            return minId;
        });
        return dao.execute(sql).getInt();
    }

    /**
     * 批量删除
     */
    @Override
    @Transactional
    public void delete(Integer[] ids) {
        dao.clear(tClass, Cnd.where("id", "in", ids));
    }

    /**
     * 伪删除
     */
    @Override
    @Transactional
    public int vDelete(Integer id) {
        return dao.update(this.tClass, Chain.make(BaseEntity.FIELD_STATUS, BaseEntity.STATUS_DELETE), Cnd.where("id", "=", id));
    }

    /**
     * 批量伪删除
     */
    @Override
    @Transactional
    public int vDelete(Integer[] ids) {
        return dao.update(this.tClass, Chain.make(BaseEntity.FIELD_STATUS, true), Cnd.where("id", "in", ids));
    }


    @Override
    public List<T> query(String fieldName, Condition cnd) {
        return Daos.ext(dao, FieldFilter.create(tClass, fieldName))
                .query(tClass, cnd);
    }

    /**
     * 自定义SQL统计
     */
    @Override
    public int count(Sql sql) {
        sql.setCallback((conn, rs, s) -> {
            int count = 0;
            if(null != rs && rs.next())
                count = rs.getInt(1);
            return count;
        });
        dao.execute(sql);
        return sql.getInt();
    }

    /**
     * 自定义SQL返回Record记录集，Record是个MAP但不区分大小写
     * 别返回Map对象，因为MySql和Oracle中字段名有大小写之分
     */
    @Override
    public List<T> list(Sql sql) {
        sql.setCallback(Sqls.callback.records());
        dao.execute(sql);
        return sql.getList(tClass);
    }

    /**
     * 分页查询(cnd)
     */
    @Override
    public QueryResult listPage(Integer pageNumber, Integer pageSize, Condition cnd) {
        return listPage(tClass,setPageNumber(pageNumber),setPageSize(pageSize),cnd);
    }

    @Override
    public QueryResult listPage(Class clazz, Integer pageNumber, Integer pageSize, Condition cnd) {
        Pager pager = dao.createPager(setPageNumber(pageNumber), setPageSize(pageSize));
        List<T> list = dao.query(clazz, cnd, pager);
        pager.setRecordCount(dao.count(clazz, cnd));
        return new QueryResult(list, pager);
    }

    /**
     * 分页查询,获取部分字段(cnd)
     */
    @Override
    public QueryResult listPage(Integer pageNumber, Integer pageSize, Condition cnd, String fieldName) {
        return listPage(tClass,setPageNumber(pageNumber), setPageSize(pageSize), cnd, fieldName);
    }

    /**
     * 分页查询,获取部分字段(cnd)
     */
    @Override
    public QueryResult listPage(Class clazz, Integer pageNumber, Integer pageSize, Condition cnd, String fieldName) {
        Pager pager = dao.createPager(setPageNumber(pageNumber), setPageSize(pageSize));
        List<T> list;
        if(StringUtils.isNotBlank(fieldName)){
            list = Daos.ext(dao, FieldFilter.create(clazz, fieldName)).query(clazz, cnd,pager);
        }else {
            list = dao.query(clazz,cnd,pager);
        }
        pager.setRecordCount(dao.count(clazz, cnd));
        return new QueryResult(list, pager);
    }

    /**
     * 分页查询(sql)
     */
    @Override
    public QueryResult listPage(Integer pageNumber, Integer pageSize, Sql sql) {
        Pager pager = dao.createPager(setPageNumber(pageNumber), setPageSize(pageSize));
        pager.setRecordCount((int) Daos.queryCount(dao, sql.toString()));// 记录数需手动设置
        sql.setPager(pager);
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(tClass));
        dao.execute(sql);
        return new QueryResult(sql.getList(tClass), pager);
    }

    @Override
    public List<T> findByIds(String ids) {
        List<T> list= Lists.newArrayList();
        Arrays.stream(ids.split(",")).forEach(id -> {
            T t = fetch(Integer.parseInt(id));
            if(Objects.nonNull(t)){
                list.add(t);
            }
        });
        return list;
    }

    @Override
    public Cnd getDelFlag(Integer delFlag){
        if(null !=delFlag){
            return Cnd.where(BaseEntity.FIELD_STATUS," = ", delFlag);
        }else {
            return Cnd.where(BaseEntity.FIELD_STATUS,"<", BaseEntity.STATUS_DELETE);
        }

    }

    @Transactional
    public void batchUpdate(List<T> list){
        if(!Lang.isEmpty(list)){
            list.stream().forEach(t -> updateIgnoreNull(t));
        }
    }

    private Integer setPageSize(Integer pageSize){
        if (null == pageSize || pageSize == 0){
            return 30;
        }
        return pageSize;
    }

    private Integer setPageNumber(Integer pageNumber){
        if(null == pageNumber || pageNumber == 0){
            return 1;
        }
        return pageNumber;
    }
}
