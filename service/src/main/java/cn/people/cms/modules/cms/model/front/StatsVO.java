package cn.people.cms.modules.cms.model.front;

import cn.people.cms.entity.BaseEntity;
import lombok.Data;

/**
 * Created by maliwei.tall on 2017/4/11.
 */
@Data
public class StatsVO extends BaseEntity {
    private String authors;//作者

    private String parentId;//父栏目id
    private String parentName;//父栏目
    /**
     * 栏目id
     */
    private String categoryId;
    private String categoryName;//栏目
    private int articleCount;//稿件数
    private int hitsCount;//点击量
    private int commentsCount;//评论数
    private String updateTime;//最后更新时间
}
