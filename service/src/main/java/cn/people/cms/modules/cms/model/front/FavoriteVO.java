package cn.people.cms.modules.cms.model.front;

import lombok.Data;
import org.nutz.dao.entity.annotation.Comment;

@Data
public class FavoriteVO {

    @Comment("文章ID")
    private Integer articleId;

    @Comment("文章标题")
    private String title;


    @Comment("收藏状态 0:收藏 3：未收藏")
    private Integer delFlag;


    @Comment("用户ID")
    private Integer userId;


    @Comment("收藏文章路径")
    private String url;

}
