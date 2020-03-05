package cn.people.cms.entity;

import cn.people.cms.util.base.UserUtil;
import cn.people.cms.util.time.ClockUtil;
import cn.people.domain.IUser;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import java.beans.Transient;
import java.io.Serializable;
import java.util.Date;

/**
 * 基础实体类
 */
@Data
public abstract class BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Comment("主键")
	@ColDefine(type = ColType.INT)
	private Integer id;

	@Column(hump = true)
	@Comment("状态标记(0：正常；1：下线；2：审核；3：删除；4:审核未通过)")
	@ColDefine(type = ColType.INT, width = 1)
	private Integer delFlag;

	@Column(hump = true)
	@Comment("创建人")
	@Prev(els = @EL("$me.currentUid()"))
	@ColDefine(type = ColType.INT)
	private Integer createBy;

	@Column(hump = true)
	@Comment("创建时间")
	@Prev(els = @EL("$me.currentTime()"))
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	private Date createAt;

	@Column(hump = true)
	@Comment("最后修改人")
	@Prev(els = @EL("$me.currentUid()"))
	@ColDefine(type = ColType.INT)
	private Integer updateBy;

	@Column(hump = true)
	@Comment("最后修改时间")
	@Prev(els = @EL("$me.currentTime()"))
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	private Date updateAt;

//	private Integer uid;

	public BaseEntity(){
	}

	public BaseEntity(Integer id){
		this.id = id;
	}

	@Transient
	public boolean isNew() {
		return this.id == null;
	}

	public Integer currentUid() {
		IUser user = UserUtil.getUser();
		if(null != user){
			return user.getId();
		}
		return null;
	}

	public Date currentTime(){
		return ClockUtil.currentDate();
	}

	// 状态标记（0：正常；1：下线；2：审核；3：删除；）
	public static final String FIELD_STATUS = "del_flag";
	public static final int STATUS_ONLINE = 0;
	public static final int STATUS_OFFLINE = 1;
	public static final int STATUS_AUDIT = 2;
	public static final int STATUS_DELETE = 3;
	public static final int STATUS_NO_AUDIT = 4;

	public static final Integer DEFAULT_PAGE_NO=1;
	public static final Integer DEFAULT_PAGE_SIZE=10;

	public void init() {
		this.setDelFlag(STATUS_ONLINE);
	}

}
