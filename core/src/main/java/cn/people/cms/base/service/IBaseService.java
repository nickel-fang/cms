package cn.people.cms.base.service;

import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.QueryResult;
import org.nutz.dao.sql.Sql;

import java.util.List;

/**
 * Created by lml on 2016/12/22.
 */
public interface IBaseService<T> {

    int count(String tableName, Condition cnd);

    int count(Sql sql);

    T fetch(Integer id);

    T fetch(String name);

    int delete(String name);

    Object save(T t);

    T insert(T t);

    int update(T t, String fieldName);

    int updateIgnoreNull(T t);

    int delete(Integer id);

    int getMaxId();

    int getMinId();

    void delete(Integer[] ids);

    int vDelete(Integer id);

    int vDelete(Integer[] ids);

    List<T> query(String fieldName, Condition cnd);

    List<T> list(Sql sql);

    QueryResult listPage(Integer pageNumber, Integer pageSize, Condition cnd);

    QueryResult listPage(Class clazz, Integer pageNumber, Integer pageSize, Condition cnd);

    QueryResult listPage(Integer pageNumber, Integer pageSize, Condition cnd, String fieldName);

    QueryResult listPage(Class clazz, Integer pageNumber, Integer pageSize, Condition cnd, String fieldName);

    QueryResult listPage(Integer pageNumber, Integer pageSize, Sql sql);

    List<T> findByIds(String ids);

    Cnd getDelFlag(Integer delFlag);

    void batchUpdate(List<T> list);

}
