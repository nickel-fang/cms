package cn.people.cms.modules.cms.model;

import cn.people.cms.entity.BaseEntity;
import cn.people.domain.BaseModel;
import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Table;

@Table("sys_user_favorite")
@Data
public class Favorite extends BaseEntity {

    @Column(hump = true)
    @Comment("文章ID")
    private Integer articleId;

    @Column
    @Comment("文章标题")
    private String title;

    @Column(hump = true)
    @Comment("收藏状态")
    private Integer delFlag;

    @Column(hump = true)
    @Comment("用户ID")
    private Integer userId;

    @Column("url")
    @Comment("收藏文章路径")
    private String url;
}
