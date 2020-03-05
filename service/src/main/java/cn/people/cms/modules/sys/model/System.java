package cn.people.cms.modules.sys.model;

import cn.people.cms.entity.BaseEntity;
import cn.people.cms.modules.user.model.Menu;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import java.util.List;

@Table("sys_system")
@Data
public class System extends BaseEntity {
    @Column
    @ColDefine(width = 100)
    @Comment("系统名称")
    private String name;

    @Column(hump = true)
    @ColDefine(width = 500)
    @Comment("系统图标")
    private String icon;

    @Column
    @ColDefine(width = 100)
    @Comment("系统编码")
    @Name
    private String code;

    @Column
    @ColDefine(width = 100)
    @Comment("系统根地址")
    private String baseUrl;

    @Column
    @ColDefine(type = ColType.INT)
    @Comment("系统属性:1,非第三方;2,第三方系统")
    private Integer type;

    @Column
    @ColDefine(width = 100)
    @Comment("备注")
    private String remark;

    @Column
    @ColDefine(width = 100)
    @Comment("副标题")
    private String subtitle;

    @Column
    @ColDefine(width = 100)
    @Comment("e-mail")
    private String email;

    @Column(hump = true)
    @ColDefine(width = 100)
    @Comment("页脚文本")
    private String footerText;

    @Column(hump = true)
    @Comment("评论是否自动上线")
    private Boolean isAutoOnline;

    @Column(hump = true)
    @ColDefine(width = 100)
    @Comment("推送key")
    private String appKey;

    @Column(hump = true)
    @ColDefine(width = 100)
    @Comment("推送id")
    private String appId;

    @Column(hump = true)
    @ColDefine(width = 100)
    @Comment("推送秘钥")
    private String masterSecret;

    @Column
    @Comment("编辑器选择")
    private Integer editor;

    @Column(hump = true)
    @ColDefine(width = 500)
    @Comment("系统首页")
    private String homePage;

    @Column(hump = true)
    @ColDefine(width = 500)
    @Comment("upms_url")
    private String upmsUrl;

    @Many(field = "systemId")
    private List<Menu> menus;

//    @ManyMany(from = "sysid", relation = "sys_user_system", target = User.class, to = "userid")
//    private List<User> userList;

//    public static final String USER_LIST = "userList";
}
