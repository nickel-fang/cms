package cn.people.cms.base.service.impl;

import cn.people.cms.base.service.ITreeService;
import cn.people.cms.entity.BaseEntity;
import cn.people.cms.entity.TailCall;
import cn.people.cms.entity.TailCalls;
import cn.people.cms.entity.TreeEntity;
import lombok.extern.slf4j.Slf4j;
import org.nutz.lang.Strings;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Service基类
 */
@Slf4j
@Transactional(readOnly = true)
public abstract class TreeService<T extends TreeEntity<T>> extends BaseService<T> implements ITreeService<T> {

	private static Integer rootId = 1;
	@Override
	@Transactional
	public Object save(T entity) {

		Class<T> entityClass = tClass;
		// 如果没有设置父节点，则代表为跟节点，有则获取父节点实体
		if (null == entity.getParentId()){
			entity.setParentId(0);
		}else{
			T parent = super.fetch(entity.getParentId());
			if(null == parent){
				// 如果传入的父节点不存在，则父节点为根节点
				entity.setParentId(rootId);
				entity.setParent(fetch(rootId));
			}else {
				entity.setParent(parent);
				// 设置新的父节点串
				entity.setParentIds(Strings.sNull(entity.getParent().getParentIds()) +entity.getParent().getId()+",");
			}
		}

		// 获取修改前的parentIds，用于更新子节点的parentIds
		String oldParentIds = entity.getParentIds();



		// 保存或更新实体
		super.save(entity);

		// 更新子节点 parentIds
		T o = null;
		try {
			o = entityClass.newInstance();
		} catch (Exception e) {
			log.error("初始化实例错误",e.getStackTrace());
		}
		o.setParentIds("%,"+entity.getId()+",%");
		List<T> list = findByParentIdsLike(o.getParentIds());
		for (T e : list){
			if (e.getParentIds() != null && oldParentIds != null){
				e.setParentIds(e.getParentIds().replace(oldParentIds, entity.getParentIds()));
				super.save(e);
			}
		}
	//	return entity;
		if(null == entity.getParentId()){
			entity.setParentId(0);
		}
		return super.save(entity);
	}

	@Override
	public List queryByParentId(Integer parentId,Integer delFlag) {
		List list = dao.query(tClass, getDelFlag(delFlag).and("parent_id", "=", parentId).desc("sort"));
		return list;
	}

	@Override
	public List queryByParentId(Boolean filterView,Integer parentId) {
		List list;
		if(filterView!=null){
			list = dao.query(tClass, getDelFlag(null).and("parent_id", "=", parentId).and("is_show","=",filterView).desc("sort"));
		}else {
			list = dao.query(tClass, getDelFlag(null).and("parent_id", "=", parentId).desc("sort"));
		}
		return list;
	}

	@Override
	public List<T> findByParentIdsLike(String parentIds) {
		return dao.query(tClass, getDelFlag(0).and("parent_ids", "like", "%"+parentIds+"%"));
	}

	@Override
	@Transactional
	public T changeOnlineStatus(Integer id){
		T t = dao.fetch(tClass,id);
		if(null !=t ){
			if(BaseEntity.STATUS_ONLINE == t.getDelFlag() ){
				t.setDelFlag(BaseEntity.STATUS_OFFLINE);
			}else {
				t.setDelFlag(BaseEntity.STATUS_ONLINE);
			}
		}
		return t;
	}

	@Override
	@Transactional
	public int vDelete(Integer id){
		if (id == 1){
			//根节点不允许删除
			return 0;
		}else {
			return super.vDelete(id);
		}
	}


	@Override
	public void tree(T t, Boolean filterView){
		if(null == t){
			return;
		}
		List<T> child = queryByParentId(filterView, t.getId());
		if(null != child && child.size() > 0){
			t.setChild(child);
			for (T c : child){
				tree(c,filterView);
			}
		}
	}

	@Override
	public void treeList(List<T> list, Boolean filterView){
		if(null == list || list.size() < 1){
			return;
		}
		for (T t : list){
			List<T> child = queryByParentId(filterView, t.getId());
			if(null != child && child.size() > 0){
				t.setChild(child);
				treeList(child,filterView);
			}
		}
	}

	@Override
	public TailCall<Set<Integer>> parentIds(Integer id, Set<Integer> parentIds){
		T t = fetch(id);
		parentIds.add(t.getParentId());
		if(t.getParentId() == 0){
			parentIds.add(id);
			return TailCalls.done(parentIds);
		}
		return TailCalls.call(() -> parentIds(t.getParentId(), parentIds));
	}
}
