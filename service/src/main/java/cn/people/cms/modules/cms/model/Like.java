package cn.people.cms.modules.cms.model;

import cn.people.cms.entity.BaseEntity;
import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Table;

@Table("sys_user_like")
@Data
public class Like extends BaseEntity {

    @Column
    @Comment("文章标题")
    private String title;

    @Column(hump = true)
    @Comment("文章ID")
    private Integer articleId;

    @Column(hump = true)
    @Comment("点赞状态 0：点赞 3：取消点赞")
    private Integer delFlag;

    @Column(hump = true)
    @Comment("用户ID")
    private Integer userId;

    @Column("url")
    @Comment("点赞文章路径")
    private String url;
}
