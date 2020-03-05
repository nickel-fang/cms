package cn.people.cms.modules.cms.model.front;

import lombok.Data;
import org.nutz.dao.entity.annotation.Comment;

@Data
public class LikeVO {

    @Comment("文章标题")
    private String title;

    @Comment("文章ID")
    private Integer articleId;


    @Comment("点赞状态 0：点赞 3：取消点赞")
    private Integer delFlag;


    @Comment("用户ID")
    private Integer userId;

    @Comment("点赞文章路径")
    private String url;


}
