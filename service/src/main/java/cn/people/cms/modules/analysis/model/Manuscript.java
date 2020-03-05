package cn.people.cms.modules.analysis.model;

import cn.people.cms.entity.BaseEntity;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

/**
 * Created by cuiyukun on 2017/6/30.
 */
@Table("analysis_manuscript")
@Data
public class Manuscript extends BaseEntity {

    @Column
    @ColDefine(type = ColType.INT)
    @Comment("稿件量")
    private Integer articles;

    @Column
    @ColDefine(type = ColType.INT)
    @Comment("访问量")
    private Integer hits;

    @Column
    @ColDefine(type = ColType.INT)
    @Comment("评论量")
    private Integer comments;

    @Column
    @ColDefine(type = ColType.INT)
    @Comment("点赞量")
    private Integer likes;

    @Column
    @ColDefine(type = ColType.INT)
    @Comment("推送量")
    private Integer push;

    /**
     * 不写入数据库的表中
     */
    private Integer delFlag;
}
