package cn.people.cms.base.service;

import cn.people.cms.entity.TailCall;

import java.util.List;
import java.util.Set;

/**
 * Created by lml on 2017/1/5.
 */
public interface ITreeService<T> extends IBaseService<T> {

    List queryByParentId(Integer parentId, Integer delFlag);

    List queryByParentId(Boolean filterView, Integer parentId);

    List findByParentIdsLike(String parentIds);

    T changeOnlineStatus(Integer id);

    int vDelete(Integer id);

    void tree(T t, Boolean filterView);

    void treeList(List<T> list, Boolean filterView);

    TailCall<Set<Integer>> parentIds(Integer id, Set<Integer> parentIds);
}
