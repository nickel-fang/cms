package cn.people.cms.modules.cms.model;

import cn.people.cms.entity.BaseEntity;
import cn.people.cms.modules.sys.model.Category;
import cn.people.cms.modules.templates.model.Template;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import java.util.List;

/**
 * Created by lml on 2016/12/22.
 */
@Table("cms_site")
@Data
public class Site extends BaseEntity {
    @Column
    @Comment("站点名称")
    private String name; // 站点名称
    @Name
    @Comment("站点别名")
    private String slug; // 站点别名
    @Column
    @Comment("描述")
    private String description;// 描述
    @Column
    @Comment("简称")
    private String simpleName;// 简称
    @Column(hump=true)
    @Comment("域名")
    private String domainPath;// 域名
    @Column
    @Comment("静态路径")
    private String path;//静态路径
    @Column(hump = true)
    @Comment("站点首页模板编号")
    public Integer templateId;
    @One(field = "templateId")
    private Template template;
    @Column(hump = true)
    @Comment("是否是当前站点")
    private Boolean isCurrentSite;

    private List<Category> categories;//频道

    public static final String TEMPLATE="template";
}
