package cn.people.cms.modules.user.model;

import cn.people.cms.entity.BaseEntity;
import cn.people.cms.modules.cms.model.Site;
import cn.people.cms.modules.sys.model.Category;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import java.util.List;
import java.util.Map;

@Table("sys_role")
@Data
public class Role extends BaseEntity {
    @Column
    @ColDefine(width = 100)
    @Comment("角色名称")
    @Name
    private String name;

//    @Column(hump = true)
//    @ColDefine(type = ColType.INT)
//    @Comment("数据范围")
//    private Integer dataScope;

//    @Column(hump = true)
//    @ColDefine(type = ColType.INT)
//    @Comment("机构ID")
//    private Integer officeId;

    @Column
    @ColDefine(width = 100)
    @Comment("备注")
    private String remark;

//    @Comment("机构名称")
//    private String officeName;

    @Column(hump = true)
    @ColDefine(width = 500)
    @Comment("前台站点频道对应关系")
    private String frontSiteCategory;

    @Column(hump = true)
    @ColDefine(width = 500)
    @Comment("后台站点频道对应关系")
    private String backSiteCategory;

    private List<Integer> menuIds;

    @ManyMany(from = "roleid", relation = "sys_role_menu", target = Menu.class, to = "menuid")
    private List<Menu> menus;

//    @ManyMany(from = "roleid", relation = "sys_user_role", target = User.class, to = "userid")
//    private List<User> users;

    /**
     * 前台角色站点关联
     */
    @ManyMany(from = "roleid", relation = "front_role_site",target = Site.class, to = "siteid")
    private List<Site> frontSiteList;

    /**
     * 前台角色频道关联
     */
    @ManyMany(from = "roleid", relation = "front_role_category",target = Category.class, to = "cateid")
    private List<Category> frontCategoryList;

    /**
     * 后台角色站点关联
     */
    @ManyMany(from = "roleid", relation = "back_role_site",target = Site.class, to = "siteid")
    private List<Site> backSiteList;

    /**
     * 后台角色频道关联
     */
    @ManyMany(from = "roleid", relation = "back_role_category",target = Category.class, to = "cateid")
    private List<Category> backCategoryList;

    /**
     * 前台站点频道列表
     */
    private List<Map<String, String>> frontSC;

    /**
     * 后台站点频道列表
     */
    private List<Map<String, String>> backSC;

    public static final Integer DATA_SCOPE_OWNER = 4;

//    public static final String OFFICES = "offices";
    public static final String MENUS = "menus";
    public static final String USERS = "users";
    public static final String FRONT_CATEGORY = "frontCategoryList";
    public static final String FRONT_SITE = "frontSiteList";
    public static final String BACK_CATEGORY = "backCategoryList";
    public static final String BACK_SITE = "backSiteList";
}
