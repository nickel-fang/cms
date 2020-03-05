package cn.people.cms.modules.user.model;

import cn.people.cms.entity.TreeEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutz.dao.entity.annotation.*;

@Table("sys_menu")
@Data
@NoArgsConstructor
public class Menu extends TreeEntity<Menu> {

    @Column
    @ColDefine(width = 100)
    @Comment("编码")
    private String code;

    @Column
    @ColDefine(width = 200)
    @Comment("资源路径")
    private String href;

    @Column
    @ColDefine(width = 50)
    @Comment("目标,例子：mainFrame、_blank、_self、_parent、_top")
    private String target;

    @Column
    @ColDefine(width = 100)
    @Comment("图标")
    private String icon;

    @Column(hump = true)
    @ColDefine(width = 50)
    @Comment("资源类型，[menu|button]")
    private String resourceType;

    @Column
    @ColDefine(width = 200)
    @Comment("权限标示,menu例子：role:*，button例子：role:create,role:update,role:delete,role:view")
    private String permission;

    @Column(hump = true)
    @ColDefine(type = ColType.BOOLEAN)
    @Comment("是否显示")
    private boolean isShow;

    @Column(hump = true)
    @Comment("系统id")
    private Integer systemId;

    @Column
    @ColDefine
    @Comment("类型")
    private String type;

    @Column(hump = true)
    @ColDefine
    @Comment("类目id")
    private Integer categoryId;

}
