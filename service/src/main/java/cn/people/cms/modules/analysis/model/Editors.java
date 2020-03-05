package cn.people.cms.modules.analysis.model;

import cn.people.cms.entity.BaseEntity;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

/**
 * Created by cuiyukun on 2017/6/30.
 */
@Table("analysis_editor")
@Data
public class Editors extends BaseEntity {

    @Column
    @ColDefine(width = 100)
    @Comment("编辑姓名")
    private String name;

    @Column
    @ColDefine(type = ColType.INT)
    @Comment("发稿量")
    private Integer articles;

    @Column
    @ColDefine(type = ColType.INT)
    @Comment("访问量")
    private Integer hits;

    @Column
    @ColDefine(type = ColType.INT)
    @Comment("评论量")
    private Integer comments;

    /**
     * 不写入数据库的表中
     */
    private Integer delFlag;
}
