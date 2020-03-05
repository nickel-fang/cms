package cn.people.cms.entity;

import cn.people.cms.util.reflect.ReflectionUtil;
import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;

import java.util.List;

@Data
public abstract class TreeEntity<T> extends BaseEntity {

	private static final long serialVersionUID = 1L;

	protected T parent;	// 父级菜单
	@Column(hump = true)
	@Comment("父级编号")
	private Integer parentId;// 父级编号
	@Column(hump = true)
	@Comment("所有父级编号")
	protected String parentIds; // 所有父级编号
	@Column
	@Comment("名称")
	protected String name; 	// 名称
	@Column
	@Comment("排序")
	protected Integer sort;		// 排序

	private List<T> child;//子菜单
	
	public TreeEntity() {
		super();
	}
	
	public TreeEntity(Integer id) {
		super(id);
	}
	
	public Integer getParentId() {
		Integer id = null;
		if(null !=parentId){
			id = parentId;
		}else if (parent != null){
			id = ReflectionUtil.getFieldValue(parent, "id");
		}
		return id;
	}
}
