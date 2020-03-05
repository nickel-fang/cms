package cn.people.cms.modules.templates.model;

import cn.people.cms.entity.BaseEntity;
import cn.people.cms.modules.block.model.Block;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutz.dao.entity.annotation.*;

import java.util.List;

/**
 * Created by lml on 2018/1/17.
 */
@Data
@Table("code_templates")
@NoArgsConstructor
public class Template extends BaseEntity {
    @Column
    @Comment("模板内容")
    @ColDefine(customType = "LONGTEXT")
    private String content;
    @Column
    @Comment("模板名称")
    private String name;
    @Column
    @Comment("标记名称")
    private String tag;
    @Column(hump = true)
    @Comment("模板路径")
    private String ftlPath;
    @Column
    @Comment("描述")
    private String description;
    @Column
    @Comment("类型")
    private String type;
    @Column(hump = true)
    @Comment("站点编号")
    private Integer siteId;
    @Column
    @Comment("排序")
    private Integer sort;
    @Column(hump = true)
    @Comment("include前缀")
    private String includePrefix;
    @Column(hump = true)
    @Comment("资源跳转前缀")
    private String resourcePrefix;
    @Comment
    @Column(hump = true)
    @ColDefine(customType = "LONGTEXT")
    private String resourceJson;
    @Comment("模板区块")
    @Many(field = "templateId")
    private List<Block> blocks;

    public static final String SITE_ID="site_id";
    public static final String SORT="sort";
    public static final String NAME="name";
    public static final String Tag="tag";
    public static final String TYPE="type";
    public static final String BLOCKS="blocks";
    public static final String RESOURCE_PREFIX="resourcePrefix";

    public static final String ARTICLE_TEMPLATE_TYPE="detail";
    public static final String CATEGORY_TEMPLATE_TYPE="category";
    public static final String BLOCK_TEMPLATE_TYPE="block";
}


