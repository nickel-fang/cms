package cn.people.cms.modules.block.model;

import cn.people.cms.entity.BaseEntity;
import cn.people.cms.modules.templates.model.Template;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

/**
 * Created by lml on 2018/4/10.
 */
@Data
@Table("cms_block")
@TableIndexes({@Index(name = "INDEX_CMS_BLOCK", fields = {"templateId"}, unique = false)})
public class Block extends BaseEntity {

    @Column
    @Comment("名称")
    private String name;

    @Column
    @Comment("描述")
    private String description;

    @Column(hump = true)
    @Comment("模板中区块的编号")
    private Integer templateId;

    @Column(hump = true)
    @Comment("排序")
    private Integer sort = 0;

    @Column(hump = true)
    @Comment("区块样式的模板编号")
    private Integer blockTemplateId;

    @Column(hump = true)
    @Comment("区块样式的模板名称")
    private String blockTemplateName;

    @One(field = "blockTemplateId")
    private Template blockTemplate;

    private BlockRelation relation;
    @Column(hump = true)
    @Comment("区块样式的模板url")
    private String tag;

    public final static String TAG= "TAG_";
}
