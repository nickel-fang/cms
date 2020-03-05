package cn.people.cms.modules.user.model;

import cn.people.cms.entity.BaseEntity;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import java.util.List;

@Table("sys_user")
@Data
public class User extends BaseEntity {

    private static final long serialVersionUID = -969706516931557499L;

    @Column(hump = true)
    @Comment("用户id")
    @ColDefine(type = ColType.INT)
    private Integer userId;

    @Column
    @ColDefine(width = 100)
    @Comment("登录名")
    @Name
    private String username;

    @Column
    @ColDefine(width = 100)
    @Comment("姓名")
    private String name;

    private String password;

    @Column
    @ColDefine(width = 100)
    @Comment("ip")
    private String ip;

    @Column
    @ColDefine(width = 100)
    @Comment("备注")
    private String remark;

    /**
     * 用户的菜单权限列表
     */
    private List<String> permissions;

    private String token;

    /**
     * 用户的角色id列表
     */
    private List<Integer> roleIds;

    @ManyMany(from = "userid", relation = "sys_user_role",target = Role.class, to = "roleid")
    private List<Role> roleList;

    private String validateCode;

    public static final String ROLE_LIST = "roleList";

    public boolean isSuper() {
        return this.isSuper(this.getId());
    }

    public boolean isSuper(int id) {
        return 1 == id;
    }
}